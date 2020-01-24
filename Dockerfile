FROM java:8

ADD target/account-service.jar account-service.jar

CMD java -jar account-service.jar
