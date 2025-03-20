FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY /target/iNote-0.0.1-SNAPSHOT.jar /app/inote.jar

ENTRYPOINT ["java", "-jar", "inote.jar"]