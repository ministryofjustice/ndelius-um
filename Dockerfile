FROM openjdk:8-jre-alpine
WORKDIR /app
COPY build/libs/bcl-ndelius-um*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
