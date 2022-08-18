FROM eclipse-temurin:11-alpine

RUN mkdir /opt/app
COPY example-all.jar /opt/app
RUN apk add gcompat
RUN apk add libstdc++
RUN ldd --version || true
CMD ["java", "-jar", "/opt/app/example-all.jar", "--test"]

