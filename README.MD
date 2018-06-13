# NDelius User Management Tool
Developed in Angular 6 and Spring Boot to manage data across the various user data sources for the National Delius 
system ie. Oracle Database, Oracle Internet Directory (OID) and multiple Active Directory (AD) instances. This is 
intended to enable NPS-ICT to effectively create and manage users, and to assist in the migration activity from Steria 
to DOM1.

## Building
### Requirements
The following dependencies are required to build this application:
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [npm 5+](https://www.npmjs.com/)
* [Angular CLI 6+](https://cli.angular.io/) (`npm install -g angular-cli`)

### Build and run tests
```bash
./gradlew build
```
### Assemble the app
```bash
./gradlew clean assemble
```
This will also package and build the UI module into the application. For more info on the UI module, see /ui/README.MD.

## Configuration
The application is configured with [conventional Spring parameters](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

### Data Sources
There are additional properties to configure access to the various data sources
These currently all follow the same convention as `spring.ldap` (see [LdapProperties](https://github.com/spring-projects/spring-boot/blob/v2.0.2.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/ldap/LdapProperties.java)).
```yaml
oid
ad.primary
ad.secondary
```

### JWT
The below properties are used to configure JWT token generation and verification:
```yaml
jwt:
  secret: secret      # Secret key used for signing JWT tokens.
  cookie: AUTH-TOKEN  # Name of the cookie to be used for storing the auth token
  expiry: 30          # JWT token expiry in minutes.
```

## Running the API
### Run with the default profile
By default, the app will run on http://localhost:8080. This can be overridden with the parameters `server.address` and 
`server.port`
```bash
java -jar /build/libs/ndelius-um-api.jar
```

### Run with the development profile
Activating the `dev` profile will start the application in Basic Auth mode with an embedded LDAP server to emulate OID, 
and an embedded H2 database. 
```bash
java -jar /build/libs/ndelius-um-api.jar --spring.profiles.active=dev
```

Once running, go to http://localhost:8080 in your browser and login with the credentials:
* username=*test.user*
* password=*secret*

## Authentication 
Authentication against the API should be first performed against the `/api/login` endpoint:
```bash
curl -u test.user:secret http://localhost:8080/api/login
```

A successful authentication response will contain a signed JWT token in the response body, as well as in a `Set-Cookie` header.
Subsequent requests to the API should contain this token, either in the `Authorization` header or as a cookie.

The `/api/login` endpoint will assert Basic Auth by default, and will verify the credentials against OID. To enable 
single sign-on using SPNEGO/Kerberos, make sure to configure the primary Active Directory domain in `ad.primary`, as 
well as the `spnego` parameters as below:
```yaml
spnego:
  enabled: true
  debug: true
  service-principal: HTTP/foo.example.com@EXAMPLE.COM
  keytab: /tmp/example.keytab
  
ad.primary:
  urls: ldap://example.com:389
  base: cn=Users,dc=example,dc=com
  username: cn=Administrator,cn=Users,dc=example,dc=com
  password: secret
```
