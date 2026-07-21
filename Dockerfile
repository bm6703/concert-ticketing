# 1단계: 빌드 (코드를 실행 가능한 jar 파일로 컴파일)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon

# 2단계: 실행 (빌드 결과물만 가져와서 가볍게 실행)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]