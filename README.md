Build and Deploy
----------------------

Make sure you have all necessary tools installed

    install/start docker
    Java 8, If you don't have docker!
    
Build the application:

    mvn clean install
    docker build -t "transaction-service" .
     
Run locally
----------------------

    Dockerfile
    * Java
        java -jar target/transaction-service.jar 


EndPoints:
----------
* /transactions POST
  * For adding new transaction
  * ex. request:
  Body
    ```
    {
    	"amount": 3,
    	"timestamp": 1526817743117
    }
    ```
  * response: 
    * 201: in case of success
    * 204: if transaction is older than 60 seconds 
  
* /statistics GET
  * Get statistics (count, sum, avg, min, max) of transactions for the last 60 seconds
  * example response:
    ```
    {
        "sum": 12,
        "avg": 4,
        "max": 5,
        "min": 3,
        "count": 3
    }
    ```
