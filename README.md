# email service

## Run the applications

### Demo

Access the application in the cloud: https://es.yang.to 

API Swagger UI: https://es.yang.to/swagger-ui.html

Note:
- the application is configured with `gmail.com` domain whitelisted.
So if you feel like a different domain name, I can do a redeployment with a different environment variable. 
- API key is needed for auth - `Authorization: Bearer whatever_your_api_key` 

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
    -e SENDGRID_APIKEY=<change_me> \
    -e OPENWEATHER_APIKEY=<change_me> \
    -e EMAILSERVICE_RECIPIENTFILTER_ENABLE=true \
    -e EMAILSERVICE_RECIPIENTFILTER_DOMAIN=gmail.com \
    -e EMAILSERVICE_APIKEY=<change_me> \
    chaoyangnz/email-service
```

Now the server is up and listening on `8080`: http://localhost:8080

For API Swagger documentation, go to http://localhost:8080/swagger-ui.html


### How to build your own

You should have a Mysql running in `localhost:5432` and has a database named `emailservice`.

Go to the repo root, and run `./gradlew bootRun`, it will run that API server in your local


## Testing

The testing can be run in different levels, I make unit tests, integration tests / API tests for email-service API.

- Unit test

```
./gradlew test
```

- Integration test

I haven't used a mock server for SendGrid and OpenWeather, so you have to specify the environment variables to run integration test.

```
SENDGRID_APIKEY=<change_me> OPENWEATHER_APIKEY=<change_me> ./gradlew test
```

## Deployment

I have `Dockerfile` in each repo root and have pre-built images published in Dockerhub.

- [chaoyangnz/email-service](https://hub.docker.com/repository/docker/chaoyangnz/email-service)

### CI/CD Pipeline

I am using GCP Cloud Build to build the image and publish to GCR.
Then it will be automatically deployed to Cloud Run.
But you can do that similarly in any CI tools or platforms.

## Design

### API General

Email Service API could be designed as client-agnostic, stateless and versatile. The reasons:
- web may be not the only client, we may have other type of clients, like CLI, Terraform.
- it is stateless for better scalability. If we have authentication and security requirement, we could introduce more robust OAuth/OIDC and token-based authentication.
- error-prevention, we should validate the input whenever a request format is received, never assume the client could send the well-formed payload. But we also need to keep the
tolerance in mind, for example, we may ignore unknown fields when the upstream calls forward these additional fields. 
- API should be not directly exposed to public. If we have to, mulitple auth approaches are needed for server-server and browser/other clients authentication.

- API is following design first strategy, I developed the swagger YAML in swagger editor first, then I generate the Swagger UI. Code should not be
polluted by spring-swagger annotations which I think is too invasive as per we have a bunch of annotations already.

### Database

- Database schema is designed without constrains: we don't enforce constrains (foreign keys, unique etc) and data integrity, including the column length. All these things 
should be done in the application level, as this way provides better flexibility and keeps logic in one place.
- I encourage to use varying chars without length, which gives us the flexibility
- [Liquibase](https://www.liquibase.org) is used for database schema migration

### Application design

The API code is a layered structure which is inspired by a [Clean Architecture variation](https://github.com/mattia-battiston/clean-architecture-example).
![](https://github.com/mattia-battiston/clean-architecture-example/raw/master/docs/images/clean-architecture-diagram-1.png)

- core layer should be POJO without any framework code. Configuration is to wire these components.

- Twelve-Factor App
    - externalize config/secrets and store in environment
    - Dev/prod parity
    
- multiple DTOs and transformations across layers should be reasonable, but keep the trade-off in mind. currently, I just defined domain object and persistence
entity objects, and serve the domain objects as view objects. I think it is OK when this application scale is quite small.

- Date handling could be a tricky thing and if not carefully, it would cause logical defects. 
    - I am use `Instant` to persist into the database and present to API client. The client is supposed to do the conversion of local datetime when needed.
    - Database is always storing UTC time which is exactly a moment regardless of the timezone.
    
## Further development

- Feature

A status check endpoint is needed, as the email delivery is an asynchronous operation. We have to query the delivery status with a message id.

- Throttling / Debounce

We could have the capability to avoid too many traffics from a client in a short time.

- Security

I am only implementing a simple API key auth filter. To be more robust, Spring  Security can be enabled and 
more sophisticated mechanism can be introduced, typically OAuth and client id + JWT based token with expiration.

- Error handling

Currently, I only handled successful and failed response from external APIs. The error
handling could be done more gracefully and with grain granularity. For example, we can map SendGrid error code
to our error code definition.

For REST API request, we can also set more configs, like connection timeout, even we can have a retry mechanism to avoid
important emails are unable to be delivered.

- Logging & Monitoring

I should be able to refine the logging and make it production ready for monitoring.
I have to differentiate the severity:
- business exceptions: normal flow, just logging is fine
- critical exceptions: trigger alarms

Newrelic APM integration.

- Testing

MVC controller tests and persistence layer integration tests are missing.

