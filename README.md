# Authenticator

## Prerequisites
- Docker
- Docker Compose
- Liquibase
- Maven

## Run
```
export DB_HOST=172.0.0.10
export DB_PORT=5432
export DB_NAME=authenticator
export DB_USERNAME=authenticator
export DB_PASSWORD=authenticator

docker-compose up -d
liquibase \
    --driver=org.postgresql.Driver \
    --classpath=db/postgresql-42.1.3.jar \
    --changeLogFile=db/changelog.xml \
    --url="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}" \
    --username=${DB_USERNAME} \
    --password=${DB_PASSWORD} \
    migrate
mvn spring-boot:run \
    -DDB_HOST=${DB_HOST} \
    -DDB_PORT=${DB_PORT} \
    -DDB_NAME=${DB_NAME} \
    -DDB_USERNAME=${DB_USERNAME} \
    -DDB_PASSWORD=${DB_PASSWORD}
```