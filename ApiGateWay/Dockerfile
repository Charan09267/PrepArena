FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY common-library /app/common-library

RUN mvn -f /app/common-library/pom.xml clean install -DskipTests

COPY ApiGateWay /app/ApiGateWay

RUN mvn -f /app/ApiGateWay/pom.xml clean package -DskipTests



FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/ApiGateWay/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]