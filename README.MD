# NDelius User Management Tool
Developed by Unilink in Angular and Spring Boot to enable NPS-ICT and CRC admins to effectively create and manage user accounts in the National Delius application.
These user accounts are currently stored in an LDAP cluster (for authentication, user preferences and functional authorisation), and an Oracle database (data authorisation and caseload management).

## Building
### Requirements
The following dependencies are required to build this application:
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Angular CLI](https://cli.angular.io/) (`npm install -g @angular/cli`)

### Build and run tests
```bash
./gradlew clean build
```
This will also package and build the user interface module into the application using NPM.

## Configuration
The application is configured with [conventional Spring parameters](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

### Data Sources
See [application-dev.yml](src/main/resources/application-dev.yml) for an example of how these can be configured.

### JWT
The below properties are used to configure JWT token generation and verification:
```yaml
jwt:
  secret: secret      # Secret key used for signing tokens.
  cookie: AUTH-TOKEN  # Name of the cookie to be used for storing the auth token.
  expiry: 30          # Token expiry in minutes.
```
If the secret is not specified, a secure key will be generated on startup.
However this will result in any issued tokens being invalidated after a restart.

## Running the API
### Run with the default profile
By default, the app will run on http://localhost:8080. This can be overridden with the parameters `server.address` and 
`server.port`. A configuration file named `application.properties` should be provided with connection details etc - see
[application-dev.yml](src/main/resources/application-dev.yml) for some example properties.
```bash
java -jar build/libs/ndelius-um.jar --spring.config.location=file:/path/to/application.properties
```

### Run with the development profile
Activating the `dev` profile will start the application in Basic Auth mode with an embedded LDAP server (port 3060), as 
well as an embedded H2 database. 
#### As a jar
```bash
java -jar build/libs/ndelius-um.jar --spring.profiles.active=dev
```
#### Or in a docker container
```bash
docker build -t ndelius-um:latest .
docker run -P --env PROFILE=dev ndelius-um:latest
```

Once running, go to http://localhost:8080 in your browser and login with the credentials:
* username=*test.user*
* password=*secret*

## UI Development
To enable live reloading of the front-end during development, run
```bash
npm start --prefix=ui
```
and go to http://localhost:4200.
Authentication will be performed as `test.user` using the credentials provided in [login.interceptor.ts](ui/src/app/interceptor/login.interceptor.ts). 

## Authentication 
Authentication against the API should be first performed against the `/api/login` endpoint:
```bash
curl -u test.user:secret http://localhost:8080/api/login
```

A successful authentication response will contain a signed JWT token in the response body, as well as in a `Set-Cookie` response header.
Subsequent requests to the API should contain this token, either in the `Authorization` header or as a request cookie.

The `/api/login` endpoint will assert Basic Auth by default, and will verify the credentials against LDAP.