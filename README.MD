# NDelius User Management Tool

[![Security](https://github.com/ministryofjustice/ndelius-um/actions/workflows/security.yml/badge.svg)](https://github.com/ministryofjustice/ndelius-um/actions/workflows/security.yml)
[![Accessibility](https://github.com/ministryofjustice/ndelius-um/actions/workflows/accessibility.yml/badge.svg)](https://github.com/ministryofjustice/ndelius-um/actions/workflows/accessibility.yml)
[![Code Quality](https://github.com/ministryofjustice/ndelius-um/actions/workflows/code-quality.yml/badge.svg)](https://github.com/ministryofjustice/ndelius-um/actions/workflows/code-quality.yml)
[![Tests](https://github.com/ministryofjustice/ndelius-um/actions/workflows/test.yml/badge.svg)](https://github.com/ministryofjustice/ndelius-um/actions/workflows/test.yml)

Developed by [Unilink](https://www.unilink.com) using Angular and Spring Boot to enable NPS-ICT and CRC admins to effectively create and maintain user accounts in the National Delius application, and to allow external services to securely authenticate Probation staff.
User accounts are stored across an LDAP cluster (for authentication, user preferences and functional authorisation), and an Oracle database (for data authorisation and caseload
management).

## Build
### Dependencies
The following dependencies are required to build this application:
* [JDK 21](https://jdk.java.net/)
* [Angular CLI](https://cli.angular.io/) (`npm install -g @angular/cli`)

### Gradle
To build/test a Jar from the source code, run:
```shell script
./gradlew clean build
```

### Docker
To build a docker image, using [Cloud Native Buildpacks](https://buildpacks.io/), run:
```shell script
./gradlew clean buildBootImage
```

## Run

To run the application using a configuration file, use the command:
```shell script
java -jar build/libs/delius-user-management.jar --spring.config.location=file:/path/to/application.properties
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
java -jar build/libs/delius-user-management.jar --spring.profiles.active=dev
```
* Using Docker:
```shell script
docker run -P --env SPRING_PROFILES_ACTIVE=dev delius-user-management:latest
```

Once started, point your browser at http://localhost:8080/umt and login with the credentials:
* Username: *test.user*
* Password: *secret*

### Front-end development
To enable live reloading of the front-end during development, run
```shell script
npm start --prefix=ui
```
and open http://localhost:4200/umt in your browser. Any changes to the UI files will trigger a browser refresh.

Note: This expects the back-end to also be running on http://localhost:8080/umt.

## Configure
The application is configured with [conventional Spring parameters](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html).

See [application-example.yml](src/main/resources/application-example.yml) for an example configuration file.
Alternatively, look at [application-dev.yml](src/main/resources/application-dev.yml) to see how the app can be configured with in-memory datasources (see: [Dev Profile](#dev-profile))

### Data Sources
* `spring.datasource` defines the Database connection, which is used for user, staff and dataset access information.
* `spring.ldap` defines the LDAP connection, which contains user authentication, role-based access control (RBAC), and NDelius user preferences.
* `spring.data.redis` defines the Redis connection details, which is used for storing OAuth2 access tokens and authorisation codes.

### Custom configuration items
* `delius.secret` is a shared secret key between this application and Delius to support authenticating users who have already logged in to Delius (See [Preauthenticated grant](#Preauthenticated grant)).
* `delius.ldap.base.*` defines the base context where users, roles, groups etc are stored within the Delius LDAP.

## Performance Testing
Load tests are written in Scala using the [Gatling](https://gatling.io) library.
These tests run from an [AWS CodeBuild project](https://eu-west-2.console.aws.amazon.com/codesuite/codebuild/130975965028/projects/del-perf-usermanagement-performance-tests-build)
in the Delius performance test environment  (see [buildspec.load-test.yml](buildspec.load-test.yml) and [performance-test-umt.tf](https://github.com/ministryofjustice/hmpps-delius-pipelines/blob/master/components/delius-core/performance-test-umt.tf)).

To run the tests locally,
```shell script
BASE_URL=http://localhost:8080/umt \
TEST_USERNAME=test.user \
TEST_PASSWORD=secret \
CONCURRENT_USERS=100 \
DURATION=300 \
./gradlew gatlingRun
```

## Authentication

The User Management Tool acts as an [OAuth2](https://oauth.net/2/) identity provider for National Delius, and supports the `authorization_code`, `client_credentials` and
`refresh_token` grant types as well as a custom `preauthenticated` grant type.

OAuth clients are registered in the LDAP under the `cn=EISUsers` container, with scopes being defined by the Delius roles assigned to them. To register a new client, follow the instructions in the [hmpps-ndelius-rbac](https://github.com/ministryofjustice/hmpps-ndelius-rbac#service-users--clients) repository.

The front-end application authenticates with the back-end using the [Authorization Code](https://oauth.net/2/grant-types/authorization-code/) flow as the UserManagement-UI client, by making use of the [angular-oauth2-oidc](https://github.com/manfredsteyer/angular-oauth2-oidc) library.

The following commands show how you can authenticate as a user (`test.user`) to authorize a client (`test.web.client`), using the Authorization Code flow.
```shell script
# User gets an authorization code
> curl -X GET -u 'test.user:secret' 'http://localhost:8080/umt/oauth2/authorize?client_id=test.web.client&response_type=code&redirect_uri=/login-success'
< 303 See Other: /login-success?code=<auth_code>

# Client swaps the authorization code for an access token
> curl -X POST -u 'test.web.client:secret' 'http://localhost:8080/umt/oauth2/token?code=<auth_code>&grant_type=authorization_code&redirect_uri=/login-success'
< {"access_token":"<token>", ...}

# Client sends the access token in the Authoriation header to access API resources
> curl -X GET -H 'Authorization: Bearer <token>' 'http://localhost:8080/umt/api/whoami'
< {"username": "test.user", ...}
```
See [AuthorizationCodeAuthTest.java](src/test/java/uk/co/bconline/ndelius/config/security/auth/AuthorizationCodeAuthTest.java).

### Preauthenticated grant

Currently the NDelius application authenticates against the LDAP directly, and this authentication can't be shared with other services.
This means that when NDelius redirects to external applications, users would have to reauthenticate against the OAuth identity provider.

To improve user experience, the `preauthenticated` grant type has been introduced.
This works by allowing NDelius to pass the signed/encrypted username to the external application as a request parameter, which can then be used for password-less authentication for
a limited period of time.
When NDelius constructs the URL for the external service, it will append two parameters:
* `u` - The username of the authenticated user
* `t` - The timestamp of when the URL was constructed

Both parameters will be encrypted by NDelius using a symmetric key that is shared between NDelius and UMT (configured using `delius.secret`).

These two parameters should then be provided to the `/oauth2/token` endpoint with `grant_type=preauthenticated`.
The parameters will be verified for authenticity using the configured `delius.secret` key, and an OAuth2 access token will be returned on success.

For example,
```shell script
> curl -X POST -u test.web.client:secret "http://localhost:8080/umt/oauth2/token?u=${encryptedUsername}&t=${encryptedTimestamp}&client_id=test.web.client&grant_type=preauthenticated&scope=UMBI001"
< {"token_type":"bearer","access_token":"...","refresh_token":"...","expires_in":43199,"scope":"UMBI001"}
```

For more details on how this is implemented, see [PreAuthenticatedGrantAuthenticationProvider.java](src/main/java/uk/co/bconline/ndelius/config/security/provider/PreAuthenticatedGrantAuthenticationProvider.java).

## Support
For any issues or questions, please contact the National Delius service team via the [#ndelius_service_team](https://moj.enterprise.slack.com/archives/C6C1KGRME)
Slack channel. Or feel free to create a [new issue](https://github.com/ministryofjustice/ndelius-um/issues/new)
in this repository.
