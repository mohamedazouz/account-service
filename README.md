Build and Deploy
----------------------

Make sure you have all necessary tools installed

    install/start docker
    Java 8, If you don't have docker!
    
Build the application:

    mvn clean install
    docker build -t "account-service" .
     
Run locally
----------------------

    * Dockerfile
        docker run -t "account-service"
    * Java
        java -jar target/transaction-service.jar 

EndPoints:
----------
*    POST   /v1/accounts: create account
*    GET    /v1/accounts: get all accounts
*   DELETE /v1/accounts/{id}: delete account
*    POST   /v1/accounts/{id}/deposits: add deposit
*    POST   /v1/accounts/{id}/withdraws: withdraw money
*    POST   /v1/accounts/{id}/transfers: transfer money between 2 accounts

