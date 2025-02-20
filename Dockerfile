FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle /app/
RUN gradle dependencies --no-daemon

COPY . .
RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]