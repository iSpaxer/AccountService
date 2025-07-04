FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -f pom.xml clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar application.jar
COPY pom.xml .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]

