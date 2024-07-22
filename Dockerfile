FROM eclipse-temurin:21-jdk as build

COPY . /EduConnectB
WORKDIR /EduConnectB

RUN chmod +x mvnw
RUN ./mvnw package -DskipTests
RUN mv -f target* EduConnectB-0.0.1-SNAPSHOT.jar

FROM eclipse-temurin:21-jre

ARG PORT
ENV PORT=${PORT}

COPY --from=build /EduConnectB/EduConnectB-0.0.1-SNAPSHOT.jar .

RUN useradd runtime
USER runtime

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "EduConnectB-0.0.1-SNAPSHOT.jar"]

