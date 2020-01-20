#FROM ecmregistry.azurecr.io/frog.base/ubuntu.java.fb
FROM openjdk:8u92-jre-alpine
MAINTAINER Ravinder Partap
ENV SERVICE_NAME=AnnotationService
ENV SERVICE_TAGS=${SERVICE_NAME},rest
ADD annotation_service-0.0.1-SNAPSHOT.jar /tmp/app.jar
EXPOSE 8282
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /tmp/app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/app.jar"]
#COPY cabinet_service.conf /etc/supervisor/
