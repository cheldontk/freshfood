Steps to run this project:

- Generate the image to prometheus
    ```
    docker build -f Dockerfile.prometheus -t freshfood-prometheus:v2.44.0 . 
    ```
- Create docker Volume
    ```
    docker volume create pgdata
    ```
- Run 
    ``` 
    docker compose up
    ```

Requeriments to run the services:
- Java 17
- Maven >=3.9.0


To run microservices:

- Catalog microservice: Access catalog folder and run
    ```
    mvn quarkus:dev -Ddebug=5005
    ```

- Marketplace microservice: Access marketplace folder and run
    ```
    mvn quarkus:dev -Ddebug=5006
    ```

- Order microservice: Access order folder and run
    ```
    mvn quarkus:dev -Ddebug=5007
    ```


To access the services using:
- CatalogApi: http://localhost:8080/q/swagger-ui/#/
- MarketplaceApi: http://localhost:8081/q/swagger-ui/#/
- OrderApi: http://localhost:8082/q/swagger-ui/#/
- MarketplaceWeb: http://localhost:3003
- Keycloak: http://localhost:8180/auth
- ArtemisMQ: http://localhost:8161
- Jaeger: http://localhost:16686
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

Users and passwords available in docker-compose.

to run marketplace web access folder web and run:
- to install dependencies
```
yarn install
```
- to run
```
yarn start
```

