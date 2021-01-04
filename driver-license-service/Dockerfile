FROM adoptopenjdk/openjdk14:jre-14.0.1_7-alpine

RUN mkdir -p /opt/driving-license-service
ADD target/driving-license-service-${project.version}.jar /opt/driving-license-service/app.jar

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Moscow"

EXPOSE 8080
VOLUME /opt/driving-license-service/logs

WORKDIR /opt/driving-license-service/

CMD ["java","-jar","app.jar"]