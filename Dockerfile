FROM maven:3.8.5-openjdk-17 AS builder

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package

FROM openjdk:17.0.1-jdk-slim

ARG JAR_FILE=/target/*SNAPSHOT.jar

RUN mkdir stories pictures static cert

COPY --from=builder ${JAR_FILE} visual_novel.jar

ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "visual_novel.jar"]
