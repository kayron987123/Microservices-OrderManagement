FROM eclipse-temurin:21.0.7_6-jdk-alpine-3.21
EXPOSE 8080
WORKDIR /root
COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

RUN ./mvnw dependency:go-offline

COPY ./src /root/src

RUN ./mvnw clean install

ENTRYPOINT ["java", "-jar", "/root/target/msvc-gateway-0.0.1-SNAPSHOT.jar"]