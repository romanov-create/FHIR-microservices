FROM openjdk:17-jdk-slim

LABEL authors="Dmytro Romanov"

RUN apt-get update && apt-get install -y wget

COPY target/fhir-endpoints-0.0.1-SNAPSHOT.jar /app/fhir-endpoints-0.0.1-SNAPSHOT.jar
RUN wget -O /app/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh

WORKDIR /app

RUN chmod +x /app/wait-for-it.sh

CMD ["./wait-for-it.sh", "kafka:9092", "--", "java", "-jar", "fhir-endpoints-0.0.1-SNAPSHOT.jar"]
