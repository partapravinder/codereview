
#FROM newgenecmregistry.azurecr.io/frog.base/ubuntu.java.fb
FROM openjdk:8u92-jre-alpine
MAINTAINER Mayur
ENV SERVICE_NAME=ZuulGatewayService
ENV SERVICE_TAGS=${SERVICE_NAME},rest
ADD zuul_gateway_service-0.0.1-SNAPSHOT.jar /tmp/app.jar
EXPOSE 8197
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /tmp/app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tmp/app.jar"]
#COPY storage_cred.conf /etc/supervisor/
