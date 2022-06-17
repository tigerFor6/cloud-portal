FROM docker.io/relateiq/oracle-java8
#VOLUME /tmp
ADD zinger-portal.jar app.jar
ENV LANG C.UTF-8
ENV TZ=Asia/Shanghai
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]