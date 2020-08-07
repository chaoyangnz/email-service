FROM openjdk:8-jdk-slim as build-stage

WORKDIR /app
COPY . .
RUN ./gradlew bootJar


FROM openjdk:8-jre-slim
WORKDIR /app

COPY --from=build-stage /app/build/libs/email-service-*.jar /app/email-service.jar

EXPOSE 8080

ARG BUILD_VERSION
ENV BUILD_VERSION=${BUILD_VERSION}

CMD ["java", "-jar", "/app/email-service.jar"]
