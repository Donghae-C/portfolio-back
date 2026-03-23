# 베이스 이미지는 Java 17 런타임 사용
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 jar 파일을 컨테이너 안으로 복사
COPY app.jar app.jar

# 스프링부트 실행
ENTRYPOINT ["java", "-jar", "app.jar"]