# email service

## Run the applications

### Demo

Access the application in the cloud: https://es.yang.to 

API Swagger: https://es.yang.to/swagger-ui.html

### Quick start with docker

- run a postgres instance

```
docker run --name postgres -p 5432:5432 -d \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=password \
    -e POSTGRES_DB=emailservice  \
    postgres:9.6
```

- start the API server
```
docker run -name email-service -it \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/emailservice \
    -e SENDGRID_APIKEY=XXX \
    -e OPENWEATHER_APIKEY=xx \
    chaoyangnz/email-service
```

Now the server is up and listening on `8080`: http://localhost:8080

For API Swagger documentation, go to http://localhost:8080/swagger-ui.html

Done. You can access it: http://localhost:8080


### How to build your own

#### API

You should have a Mysql running in `localhost:5432` and has a database named `emailservice`.

Go to the repo root, and run `./gradlew bootRun`, it will run that API server in your local


## Testing

The testing can be run in different levels, I make unit tests, integration tests and API tests for email-service API.

- API

```
./gradlew test
```

## Deployment

I have `Dockerfile` in each repo root and have pre-built images published in Dockerhub.

- [chaoyangnz/email-service](https://hub.docker.com/repository/docker/chaoyangnz/email-service)

### CI Pipeline

Internally, I am using GCP Cloud Build to build the image and publish to GCR. But you can do that similarly in any CI tools or platforms.

### CD tool

I am using [Weaveworks Flux](https://github.com/fluxcd/flux) to automatically deploy the applications to GCP Kubernetes Cluster. I have a separate repo to host the infrastructure and Kubernetes 
resource charts. See https://github.com/chaoyangnz/email-service-gitops
Still in progress, but I did the similar things many times.

## Design and Further development

### API General

Email Service API could be designed as coarse-grained, which means it should be client-agnostic, stateless and versatile. The reasons:
- web may be not the only client, we may have other type of clients, like CLI, Terraform.
- it is stateless for better scalability. If we have authentication and security requirement, we could introduce more robust OAuth and token-based authentication.
- error-prevention, we should validate the input whenever a request format is received, never assume the client could send the well-formed payload. But we also need to keep the
tolerance in mind, for example, we may ignore unknown fields when the upstream calls forward these additional fields. 
- API should be not directly exposed to public. If we have to, mulitple auth approaches are needed for server-server and browser/other clients authentication.

API is following design first strategy, I developed the swagger YAML in swagger editor first, then I generate the Swagger UI. Code should not be
polluted by spring-swagger annotations which I think is too invasive as per we have a bunch of annotations already.

### Database

Database schema is designed without constrains: we don't enforce constrains (foreign keys, unique etc) and data integrity, including the column length. All these things 
should be done in the application level.
- I encourage to use varying chars without length, which gives us the flexibility
- static lookup data is stored in database, but the application should ensure the retrieval performance by use of caching or like
- [Liquibase](https://www.liquibase.org) is used for database schema migration

### application design (more details)

The API code is a layered structure which is inspired by Clean architecture with simplification.

- Multiple DTOs and transformations should be acceptable, but keep the trade-off in mind. currently, I just defined domain object and persistence
entity objects, and serve the domain objects as view objects. I think it is OK when this application scale is quite small.

- Date handling could be a tricky thing and if not carefully, it would cause logical defects. 
    - I am use `Instant` to persist into database and present to API client. The client is supposed to do the conversion of local datetime when needed. 
    - When I receive requests from the client, I require the client to tell me the timezone, so I prefer to use `ZonedTimestamp` to include the timezone information. 
    - Database is always storing UTC time which is exactly a moment regardless of the timezone.