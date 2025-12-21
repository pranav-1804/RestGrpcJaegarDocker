FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY grpc-service/pom.xml .
RUN mvn -q dependency:go-offline

COPY grpc-service/src ./src

RUN mvn -q clean package -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

COPY opentelemetry-javaagent.jar /opentelemetry/opentelemetry-javaagent.jar

EXPOSE 9090

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java","-jar","/app/app.jar"]