FROM openjdk:17
VOLUME /tmp
ARG JAR_FILE=target/index-service.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]