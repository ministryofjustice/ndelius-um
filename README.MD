# NDelius User Management Tool
Developed by [Unilink](https://www.unilink.com) using Angular and Spring Boot to enable NPS-ICT and CRC admins to effectively create and maintain user accounts in the National Delius application, and to allow external services to securely authenticate Probation staff.
User accounts are stored across an LDAP cluster (for authentication, user preferences and functional authorisation), and an Oracle database (for data authorisation and caseload management). 

* [Demo Application](https://umt-dev.bconline.co.uk/umt/)
  * username=test.user
  * password=secret
* [API Documentation](https://umt-dev.bconline.co.uk/umt/swagger-ui.html)


## Build
### Dependencies
The following dependencies are required to build this application:
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Angular CLI](https://cli.angular.io/) (`npm install -g @angular/cli`)

### Gradle
To build/test a Jar from the source code, run:
```shell script
./gradlew clean build
```

### Docker
Once the Gradle build has completed, a Docker image can be built using the following command:
```shell script
docker build -t ndelius-um:latest .
```

## Run
To run the application using a configuration file, use the command: 
```shell script
java -jar build/libs/ndelius-um.jar --spring.config.location=file:/path/to/application.properties
```
By default, the app will run on http://localhost:8080/umt. 
This can be overridden with the properties `server.address`, `server.port` and `server.servlet.context-path`.
See [Configure](#configure).

### Dev profile
To quickly get up and running, you can run the app using the `dev` profile.
This will start the application with an in-memory LDAP server, H2 database and Redis cluster.
Data is seeded from [data.ldif](src/main/resources/data.ldif). and [data.sql](src/main/resources/data.sql).

* Using Java:
```shell script
java -jar build/libs/ndelius-um.jar --spring.profiles.active=dev
```
* Using Docker:
```shell script
docker run -P --env PROFILE=dev ndelius-um:latest
```

Once started, point your browser at http://localhost:8080/umt and login with the credentials:
* Username: *test.user*
* Password: *secret*

### Front-end development
To enable live reloading of the front-end during development, run
```bash
npm start --prefix=ui
```
and open http://localhost:4200/umt in your browser. Any changes to the UI files will trigger a browser refresh.

Note: This expects the back-end to also be running on http://localhost:8080/umt.

## Configure
The application is configured with [conventional Spring parameters](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

### Data Sources
* `spring.datasource` defines the Database connection, which is used for user, staff and dataset access information.
* `spring.ldap` defines the LDAP connection, which contains user authentication, role-based access control (RBAC), and NDelius user preferences.
* `spring.redis` defines the Redis connection details, which is used for storing OAuth2 access tokens and authorisation codes.

See [application-example.yml](src/main/resources/application-example.yml) for an example of how these can be configured. 
Alternatively, look at [application-dev.yml](src/main/resources/application-dev.yml) to see how the app can be configured with in-memory datasources (see: [Dev Profile](#dev-profile))

## Authentication
The User Management Tool acts as an [OAuth2](https://oauth.net/2/) identity provider for National Delius, and supports the `implicit`, `authorization_code`, `client_credentials` and `refresh_token` grant types as well as a custom `preauthenticated` grant type.

OAuth clients are registered in the LDAP under the `cn=EISUsers` container, with scopes being defined by the Delius roles assigned to them.

The front-end application authenticates with the back-end using the [Authorization Code](https://oauth.net/2/grant-types/authorization-code/) flow as the UserManagement-UI client, by making use of the [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc) library.

### Preauthenticated grant
Currently the NDelius application authenticates against the LDAP directly, and this authentication can't be shared with other services. 
This means that when NDelius redirects to external applications, users would have to reauthenticate against the OAuth identity provider.

To improve user experience, the `preauthenticated` grant type has been introduced. 
This works by allowing NDelius to pass the signed/encrypted username to the external application as a request parameter, which can then be used for password-less authentication for a limited period of time. 
When NDelius constructs the URL for the external service, it will append two parameters:
* `u` - The username of the authenticated user
* `t` - The timestamp of when the URL was constructed

Both parameters will be encrypted by NDelius using a symmetric key that is shared between NDelius and UMT (configured using `delius.secret`).

These two parameters should then be provided to the `/oauth/token` endpoint with `grant_type=preauthenticated` and `client_id=NDelius`.
The parameters will be verified for authenticity using the configured `delius.secret` key, and an OAuth2 access token will be returned on success.

For example,
```shell script
> curl -X POST -u NDelius: "http://localhost:8080/umt/oauth/token?u=${encryptedUsername}&t=${encryptedTimestamp}&client_id=NDelius&grant_type=preauthenticated&scope=UMBI001"
< {"token_type":"bearer","access_token":"...","refresh_token":"...","expires_in":43199,"scope":"UMBI001"}
```

For more details on how this is implemented, see [PreAuthenticatedTokenGranter.java](src/main/java/uk/co/bconline/ndelius/config/security/provider/token/PreAuthenticatedTokenGranter.java).
