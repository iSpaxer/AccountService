FROM maven:3.8.5-openjdk-17 AS build
# COPY ../shared/pom.xml /shared
COPY ../pom.xml /
COPY /src /main-api/src
COPY pom.xml /main-api
RUN mvn -f /main-api/pom.xml clean package

FROM openjdk:17-jdk-slim
COPY --from=build /main-api/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]