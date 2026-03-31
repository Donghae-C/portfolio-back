# Java 17 런타임 이미지 사용
FROM eclipse-temurin:17-jre

# 컨테이너 내부 작업 경로 설정
WORKDIR /app

# timezone 관련 패키지 설치 및 KST로 고정
# - tzdata가 있어야 Asia/Seoul 시간대 정보를 제대로 인식할 수 있음
# - /etc/localtime, /etc/timezone까지 맞춰두면 OS 레벨 date도 KST로 나옴
RUN apt-get update && apt-get install -y tzdata \
    && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

# 배포 폴더 안의 app.jar를 이미지 내부로 복사
COPY app.jar app.jar

# 컨테이너 기본 환경변수 설정
# - TZ: OS 레벨 시간대
# - JAVA_TOOL_OPTIONS: JVM 시간대
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# 스프링부트 포트 오픈
EXPOSE 8080

# 컨테이너 시작 시 스프링부트 실행
ENTRYPOINT ["java", "-jar", "app.jar"]