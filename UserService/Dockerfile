FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY common-library /app/common-library

RUN cd /app/common-library && \
    mvn clean install -DskipTests


COPY UserService /app/UserService

RUN cd /app/UserService && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/UserService/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]