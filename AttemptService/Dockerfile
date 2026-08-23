FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

WORKDIR /app

COPY common-library /app/common-library

RUN cd /app/common-library && \
    mvn clean install -DskipTests

COPY AttemptService /app/AttemptService

RUN cd /app/AttemptService && \
    mvn clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/AttemptService/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]