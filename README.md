# PhoneNumberLookup

`PhoneNumberLookup` is a REST API that parses and validates phone numbers and returns a normalized breakdown of the number into:

- `phoneNumber`
- `countryCode`
- `areaCode`
- `localPhoneNumber`

The service is implemented with Spring Boot and Google's `libphonenumber`.

## Project Overview

The application exposes one endpoint:

- `GET /v1/phone-numbers`

Query parameters:

- `phoneNumber` (required)
- `countryCode` (optional, ISO 3166-1 alpha-2 uppercase code such as `US`, `GB`, `MX`)

Example request:

```http
GET /v1/phone-numbers?phoneNumber=%2B12125690123&countryCode=US
```

Example success response:

```json
{
  "phoneNumber": "+12125690123",
  "countryCode": "US",
  "areaCode": "212",
  "localPhoneNumber": "5690123"
}
```

Example error response:

```json
{
  "phoneNumber": "+1-212-569-0123",
  "error": {
    "PhoneNumber": "Invalid phone number format. The phone number should contain only digits, spaces, and an optional leading '+'."
  }
}
```

More example requests:

- `http://localhost:8080/v1/phone-numbers?phoneNumber=%2B12125690123`
- `http://localhost:8080/v1/phone-numbers?phoneNumber=%2B52%20631%203118150`
- `http://localhost:8080/v1/phone-numbers?phoneNumber=%2B34%20915%20872200`
- `http://localhost:8080/v1/phone-numbers?phoneNumber=351%2021%20094%20%202000`
- `http://localhost:8080/v1/phone-numbers?phoneNumber=%2B12125690123&countryCode=USA`

## Installation And Run Instructions

### Prerequisites

- Java 26
- `JAVA_HOME` set to the Java 26 installation
- Maven 3.9.14, or use the included Maven Wrapper

### Run locally

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The application starts on the default Spring Boot port:

```text
http://localhost:8080
```

### Run tests

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS or Linux:

```bash
./mvnw test
```

### Build a jar

```powershell
.\mvnw.cmd clean package
```

Then run:

```powershell
java -jar target\phonenumberlookup-0.0.1-SNAPSHOT.jar
```

## Tech Stack

- Programming Language: Java 26
- Build Tool: Apache Maven 3.9.14
- Maven Wrapper: 3.3.4
- Framework: Spring Boot 4.0.4
- Web Framework: Spring MVC via `spring-boot-starter-webmvc`
- Unit Test Library: JUnit 5 via Spring Boot test support
- Phone Number Parsing Library: Google `libphonenumber` 8.13.50
- Logging: SLF4J with Spring Boot's default logging setup

## Why This Stack

### Programming language: Java

Java is a practical choice for this API because it is mature, strongly typed, and widely used for backend services. It also integrates cleanly with `libphonenumber`, which is the key library used for parsing and validating international phone numbers.

### Framework: Spring Boot

Spring Boot reduces the amount of setup needed for a REST service. It provides:

- embedded server support
- simple request mapping with controllers
- structured exception handling
- straightforward Maven packaging

For an HTTP API like this, Spring Boot keeps the implementation concise and easy to extend.

### Library: Google libphonenumber

`libphonenumber` is the correct choice for this problem because phone number parsing rules are country-specific and non-trivial. Re-implementing those rules manually would be error-prone. This library provides:

- international parsing support
- validation by region
- region detection
- extraction helpers for number components

## Production Deployment Approach

Production deployment approach:

1. Build the application jar with `.\mvnw.cmd clean package`.
2. Containerize it with a small Java runtime image.
3. Deploy the container to a platform such as Azure App Service, AWS ECS/Fargate, Kubernetes, or Cloud Run.
4. Externalize configuration with environment variables.
5. Put the service behind a load balancer or API gateway.
6. Add health checks, logs, metrics, and alerting.

## Assumptions Made

1. Phone Number Format:
   The phone number may contain an optional `+`, digits, and single spaces between digit groups. Other special characters such as hyphens and parentheses are treated as invalid.
2. Country Code:
   When provided, it must be an ISO 3166-1 alpha-2 uppercase code such as `US` or `GB`.
3. Country Code Derivation:
   If no country code is provided, the service attempts to derive the region from the number itself.
4. Validation Source:
   The service relies on `libphonenumber` for region-aware parsing and validation.
5. API Scope:
   The service is stateless and processes one phone number per request.

## Future Improvements

- Batch processing for multiple phone numbers in one request
- Additional number formats such as E.164, national, and international output
- Carrier lookup support
- Phone type detection such as mobile, landline, or VoIP
- Controller-level integration tests
- OpenAPI or Swagger documentation

## Source Layout

Key files:

- `src/main/java/com/oxiotest/phonenumberlookup/controller/PhoneNumberLookupController.java`
- `src/main/java/com/oxiotest/phonenumberlookup/service/PhoneNumberLookupServiceImpl.java`
- `src/main/java/com/oxiotest/phonenumberlookup/exception/GlobalExceptionHandler.java`
- `src/test/java/com/oxiotest/phonenumberlookup/PhoneNumberLookupApplicationTests.java`

## Project Structure

```text
PhoneNumberLookup/
|
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   `-- com/
|   |   |       `-- oxiotest/
|   |   |           `-- phonenumberlookup/
|   |   |               |-- PhoneNumberLookupApplication.java
|   |   |               |   # Spring Boot application entry point
|   |   |               |
|   |   |               |-- controller/
|   |   |               |   `-- PhoneNumberLookupController.java
|   |   |               |       # Exposes GET /v1/phone-numbers
|   |   |               |
|   |   |               |-- service/
|   |   |               |   |-- PhoneNumberLookupService.java
|   |   |               |   |   # Lookup service contract
|   |   |               |   `-- PhoneNumberLookupServiceImpl.java
|   |   |               |       # Validation, parsing, and extraction logic
|   |   |               |
|   |   |               |-- model/
|   |   |               |   `-- PhoneNumberLookupResponse.java
|   |   |               |       # Response DTO returned to API clients
|   |   |               |
|   |   |               `-- exception/
|   |   |                   |-- GlobalExceptionHandler.java
|   |   |                   |   # Converts exceptions into HTTP 400 responses
|   |   |                   |-- InvalidCountryException.java
|   |   |                   |   # Raised for invalid country code input
|   |   |                   `-- InvalidPhoneException.java
|   |   |                       # Raised for invalid phone number input
|   |   |
|   |   `-- resources/
|   |       |-- application.properties
|   |       |   # Application configuration and logging level
|   |       |-- static/
|   |       |   # Static assets directory
|   |       `-- templates/
|   |           # Template directory
|   |
|   `-- test/
|       `-- java/
|           `-- com/
|               `-- oxiotest/
|                   `-- phonenumberlookup/
|                       `-- PhoneNumberLookupApplicationTests.java
|                           # Unit tests for the service logic
|
|-- .mvn/
|   `-- wrapper/
|       `-- maven-wrapper.properties
|           # Maven Wrapper configuration
|
|-- mvnw
|   # Maven Wrapper script for macOS/Linux
|-- mvnw.cmd
|   # Maven Wrapper script for Windows
|-- pom.xml
|   # Maven dependencies and build configuration
|-- HELP.md
|   # Spring Boot generated helper file
|-- README.md
|   # Project documentation
`-- target/
    # Maven build output
```
