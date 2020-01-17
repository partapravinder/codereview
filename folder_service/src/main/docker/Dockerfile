#FROM @DOCKER_REPOSITORY@/frog.base/ubuntu.java.fb
FROM openjdk:8u92-jre-alpine
MAINTAINER Anushree Jain
ENV SERVICE_NAME=FolderService
ENV SERVICE_TAGS=${SERVICE_NAME},rest
ADD folder_service-0.0.1-SNAPSHOT.jar /tmp/app.jar
EXPOSE 8185
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /tmp/app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/app.jar"]
#COPY folder_service.conf /etc/supervisor/