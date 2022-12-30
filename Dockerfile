# This file is left here for reference, but is no longer used.
# The preferred way to build a docker image from source is using the Spring Boot CNB feature (./gradlew buildBootImage).
FROM openjdk:17-jre
WORKDIR /app
COPY build/libs/*.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK CMD wget --quiet --tries=1 --spider http://localhost:8080/umt/actuator/health
ENV PROFILE=default
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=${PROFILE}"]
