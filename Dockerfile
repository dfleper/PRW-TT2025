# =========================================
# TT2025 - Render Dockerfile
# Spring Boot 3.5.x | Java 17 | WAR ejecutable
# =========================================

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/TT2025.war /app/app.war

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh","-c","java -jar /app/app.war"]
