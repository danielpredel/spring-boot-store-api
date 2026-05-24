FROM eclipse-temurin:21-alpine

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir /logs && chown spring:spring /logs

USER spring:spring

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
