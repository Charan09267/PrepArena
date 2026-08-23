FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Install common library
COPY common-library /app/common-library

RUN cd /app/common-library && \
    mvn clean install -DskipTests

# Build contest service
COPY ContestService /app/ContestService

RUN cd /app/ContestService && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/ContestService/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]