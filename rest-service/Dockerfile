FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY rest-service/pom.xml .
RUN mvn -q dependency:go-offline

COPY rest-service/src ./src

RUN mvn -q clean package -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app

# COPY OTEL AGENT
COPY opentelemetry-javaagent.jar /opentelemetry/opentelemetry-javaagent.jar

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java","-jar","/app/app.jar"]