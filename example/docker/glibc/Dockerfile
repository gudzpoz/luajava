FROM ubuntu:14.04

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME

ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN mkdir /opt/app
COPY example-all.jar /opt/app
RUN ldd --version
CMD ["java", "-jar", "/opt/app/example-all.jar", "--test"]
