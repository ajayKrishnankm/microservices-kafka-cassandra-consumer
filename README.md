# microservices-kafka-cassandra-consumer
consumer using springboot, java, kafka cassandra 

## consumer service

* * This project runs on port 8003 by default you can configure it in application.properties
* Have topics with the name **app_updates, employee_updates, app_DLQ** created in your kafka cluster before running the app
* The executable jar file can be found in the root path with the name consumer-service.jar
* Java 8 should be configured in your system to run the jar file

#### Steps to run the executable jar file are

* Clone the project
* Navigate to the project's root folder
* Run the following command

```bash
java -jar consumer-service.jar
```
