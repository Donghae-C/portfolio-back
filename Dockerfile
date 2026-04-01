# Java 17 런타임 이미지 사용
FROM eclipse-temurin:17-jre

# 컨테이너 내부 작업 경로 설정
WORKDIR /app

# timezone 관련 패키지 + docker cli 설치
# - tzdata: Asia/Seoul 시간대 적용
# - docker.io: 컨테이너 안에서 docker 명령 사용
RUN apt-get update && apt-get install -y \
    tzdata \
    docker.io \
    && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

# 배포 폴더 안의 app.jar를 이미지 내부로 복사
COPY app.jar app.jar

# 컨테이너 기본 환경변수 설정
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# 스프링부트 포트 오픈
EXPOSE 8080

# 컨테이너 시작 시 스프링부트 실행
ENTRYPOINT ["java", "-jar", "app.jar"]