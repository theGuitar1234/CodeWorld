This is a simple Spring Boot project for managing Teachers and Students in a course. 

Run src\main\java\az\codeworld\springboot\SpringBootApplication.java to start the application

Required .env Variables include your Database name, username and password for database connection. Postgres and H2 is used currently.

Go to src\main\resources\application.properties to set the profile. There are 2 profiles : Dev and Prod. Set one of them via spring.profiles.active

Run Keycloak : 

docker run -d --name keycloak -p 8080:8080 \
 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
 quay.io/keycloak/keycloak:latest start-dev

Check keycloak : 

docker ps -a | grep keycloak

Start keycloak : 

docker start keycloak

Check logs : 

docker logs -f keycloak

Remove the keycloak container : 

docker stop keycloak

docker rm -f keycloak

Stop keycloak : 

docker stop keycloak

If you want to use multiple containers, then you have to define with different 
names and ports : 

docker run -d --name keycloak2 -p 8081:8080 \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev

Get token : 

curl -i -v -X POST "http://localhost:8081/realms/REALM/protocol/openid-connect/token" ^
 -H "Content-Type: application/x-www-form-urlencoded" ^
 -d "grant_type=client_credentials" ^
 -d "client_id=CLIENT_ID" ^
 -d "client_secret=SECRET"
