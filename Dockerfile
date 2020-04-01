FROM openjdk:8-jre
WORKDIR /app
COPY build/libs/*.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK CMD wget --quiet --tries=1 --spider http://localhost:8080/umt/actuator/health
ENV PROFILE=default
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=${PROFILE}"]
