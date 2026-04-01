package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.SandboxStatus;
import com.portfolio.portfolioback.dto.SandboxResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SandboxServiceImpl implements SandboxService {
    @Override
    public String runCode() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "-v", "C:\\Users\\choi7\\Desktop\\container test:/app",
                "-w", "/app",
                "eclipse-temurin:17",
                "sh", "-c",
                "javac Main.java && java Main"
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        process.waitFor();

        log.info("result: {}" , result.toString());
        return result.toString();
    }

    @Override
    public SandboxResponseDTO runCode(String code) throws Exception {
        // 호스트와 컨테이너가 동일하게 공유하는 절대 경로여야 함
        Path baseDir = Paths.get("/sandbox");
        Files.createDirectories(baseDir);

        Path tempDir = null;
        String containerName = "java-runner-" + UUID.randomUUID();

        try {
            // 공유 디렉터리 아래에 실행용 임시 폴더 생성
            tempDir = Files.createTempDirectory(baseDir, "java-run-");

            // Main.java 파일 경로 생성
            Path file = tempDir.resolve("Main.java");

            // 전달받은 자바 코드 저장
            Files.writeString(file, code);

            // docker run -v 의 왼쪽 경로는
            // 호스트에서도 실제로 존재하는 절대경로여야 함
            String path = tempDir.toAbsolutePath().toString();

            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "--name", containerName,
                    "--memory=128m",
                    "--network=none",
                    "-v", path + ":/app",
                    "-w", "/app",
                    "eclipse-temurin:17",
                    "sh", "-c",
                    "javac /app/Main.java && java -cp /app Main"
            );

            // 표준 에러를 표준 출력으로 합침
            pb.redirectErrorStream(true);

            Process process = pb.start();

            StringBuilder output = new StringBuilder();

            // 자식 프로세스 출력 읽기
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                )) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (IOException e) {
                    output.append("출력 읽기 오류: ").append(e.getMessage()).append("\n");
                }
            });

            outputThread.start();

            // 최대 3초까지 대기
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);

            if (!finished) {
                // docker run 프로세스 종료
                process.destroyForcibly();

                // 혹시 실제 컨테이너가 살아있으면 제거
                killAndRemoveContainer(containerName);

                outputThread.join(500);

                return SandboxResponseDTO.builder()
                        .status(SandboxStatus.TIMEOUT)
                        .result(null)
                        .error("실행 시간이 제한을 초과했음")
                        .build();
            }

            // 출력 읽기 스레드 종료 대기
            outputThread.join();

            int exitCode = process.exitValue();
            String outputText = output.toString();

            if (exitCode == 0) {
                return SandboxResponseDTO.builder()
                        .status(SandboxStatus.SUCCESS)
                        .result(outputText)
                        .error(null)
                        .build();
            }

            // javac 컴파일 에러 문자열 포함 시 컴파일 에러로 처리
            if (outputText.contains("error:")) {
                return SandboxResponseDTO.builder()
                        .status(SandboxStatus.COMPILE_ERROR)
                        .result(null)
                        .error(outputText)
                        .build();
            }

            return SandboxResponseDTO.builder()
                    .status(SandboxStatus.RUNTIME_ERROR)
                    .result(null)
                    .error(outputText)
                    .build();

        } catch (Exception e) {
            return SandboxResponseDTO.builder()
                    .status(SandboxStatus.SYSTEM_ERROR)
                    .result(null)
                    .error("서버 내부 오류: " + e.getMessage())
                    .build();
        } finally {
            // 남아있는 실행 컨테이너 정리
            killAndRemoveContainer(containerName);

            // 실행 폴더 정리
            deleteTempDirectory(tempDir);
        }
    }

    private void killAndRemoveContainer(String containerName) {
        try {
            new ProcessBuilder("docker", "kill", containerName)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor(2, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // 이미 종료됐거나 존재하지 않으면 무시
        }

        try {
            new ProcessBuilder("docker", "rm", "-f", containerName)
                    .redirectErrorStream(true)
                    .start()
                    .waitFor(2, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // 이미 삭제됐거나 존재하지 않으면 무시
        }
    }

    /**
     * 임시 디렉토리와 내부 파일들을 역순으로 삭제함
     */
    private void deleteTempDirectory(Path tempDir) {
        if (tempDir == null) {
            return;
        }

        try (Stream<Path> walk = Files.walk(tempDir)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                            // 삭제 실패는 일단 무시
                        }
                    });
        } catch (IOException ignored) {
            // 폴더 순회 실패 시 무시
        }
    }
}
