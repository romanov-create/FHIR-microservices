# FHIR-microservices
## Contains:
- fhir-endpoints - spring boot microservice that exposes the REST endpoint with Spring MVC Web
- fhir-kafka-listener -  spring boot microservice with a Kafka listener
- hapi-fhir-jpaserver-starter
### Also in docker compose file exists servers:
- zookeeper
- kafka

## For quick start :
1) pull project
2) run maven goals 'clean install' for:
  - fhir-endpoints
  - fhir-kafka-listener 
  - hapi-fhir-jpaserver-starter
3) check ports settings in docker-compose file
4) run command: sudo docker-compose up --build

## For test: 
run tests in FHIR-microservices/fhir-endpoints/test/FhirProcessingServiceImplTest

### In Postman:
Method: Post

Url: http://localhost:8001/api/fhir/bundle

Basic Auth: root root

### Example Bundle JSON:
FHIR-microservices/fhir-endpoints/test/resources/validEncounterWithExternalReferences.json
