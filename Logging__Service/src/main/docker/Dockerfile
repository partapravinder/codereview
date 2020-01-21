#FROM @DOCKER_REPOSITORY@/frog.base/ubuntu.java.fb
FROM openjdk:8u92-jre-alpine
MAINTAINER Anushree Jain
ENV SERVICE_NAME=LoggingService
ENV SERVICE_TAGS=${SERVICE_NAME},rest
ADD logging_service-0.0.1-SNAPSHOT.jar /tmp/app.jar
EXPOSE 8198
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /tmp/app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/app.jar"]
#COPY logging_service.conf /etc/supervisor/
