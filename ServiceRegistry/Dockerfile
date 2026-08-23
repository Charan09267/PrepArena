FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY ServiceRegistry /app/ServiceRegistry

RUN cd /app/ServiceRegistry && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/ServiceRegistry/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]