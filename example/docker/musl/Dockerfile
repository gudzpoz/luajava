FROM alpine:latest

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:11-alpine $JAVA_HOME $JAVA_HOME

ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN mkdir /opt/app
COPY example-all.jar /opt/app
RUN apk add gcompat
RUN apk add libstdc++
RUN ldd --version || true
CMD ["java", "-jar", "/opt/app/example-all.jar", "--test"]
