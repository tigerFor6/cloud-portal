FROM docker.io/relateiq/oracle-java8
#VOLUME /tmp
ENV LANG C.UTF-8
ENV TZ=Asia/Shanghai
ADD zinger-tag.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]