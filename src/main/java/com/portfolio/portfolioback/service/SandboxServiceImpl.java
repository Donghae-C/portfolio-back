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
    public SandboxResponseDTO runCode(String code) {
        Path tempDir = null;
        String containerName = "java-runner-" + UUID.randomUUID();

        try {
            // 임시 디렉토리 생성
            tempDir = Files.createTempDirectory("java-run");

            // Main.java 파일 경로 생성
            Path file = tempDir.resolve("Main.java");

            // Docker 볼륨 마운트를 위해 경로를 / 형식으로 변경
            String path = tempDir.toAbsolutePath().toString().replace("\\", "/");

            // 전달받은 자바 코드를 파일에 저장
            Files.writeString(file, code);

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

            // 표준 에러도 표준 출력으로 합침
            pb.redirectErrorStream(true);

            Process process = pb.start();

            StringBuilder output = new StringBuilder();

            // 출력 읽기 스레드
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

            // 3초 타임아웃
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);

            if (!finished) {
                // docker run 프로세스 강제 종료
                process.destroyForcibly();

                // 실제 컨테이너도 제거
                killAndRemoveContainer(containerName);

                outputThread.join(500);

                return SandboxResponseDTO.builder()
                        .status(SandboxStatus.TIMEOUT)
                        .result(null)
                        .error("실행 시간이 제한을 초과했음")
                        .build();
            }

            // 출력 스레드 종료 대기
            outputThread.join();

            // 프로세스 종료 코드 확인
            int exitCode = process.exitValue();
            String outputText = output.toString();

            // 종료코드가 0이면 정상 실행 성공
            if (exitCode == 0) {
                return SandboxResponseDTO.builder()
                        .status(SandboxStatus.SUCCESS)
                        .result(outputText)
                        .error(null)
                        .build();
            }

            // 컴파일 에러인지 런타임 에러인지 단순 분기
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
            // 혹시 남은 컨테이너가 있으면 제거
            killAndRemoveContainer(containerName);

            // 임시 디렉토리 삭제
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
