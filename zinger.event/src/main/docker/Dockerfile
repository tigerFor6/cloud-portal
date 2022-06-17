FROM docker.io/relateiq/oracle-java8
#VOLUME /tmp
ADD zinger-event.jar app.jar
ENV LANG C.UTF-8
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]