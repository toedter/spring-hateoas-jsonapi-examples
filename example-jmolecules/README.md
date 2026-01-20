# Example: JModulith with JSON:API

This example demonstrates how to use JModulith and JMolecules concepts with JSON:API for Spring HATEOAS.

## Key Concepts Demonstrated

### 1. Value Object Identifiers
Following the patterns from [Issue #176](https://github.com/toedter/spring-hateoas-jsonapi/issues/176), this example uses value objects for identifiers:

- `MovieId` - UUID-based identifier for Movie aggregate
- `ImdbId` - Domain-specific value object for IMDB identifiers
- `Rating` - Value object with validation (0.0-10.0)
- `MovieTitle` - Value object for movie titles with validation

### 2. Aggregate Roots
The example defines two aggregate roots using JMolecules annotations:

- `Movie` - Movie catalog aggregate (in `movie` module)


`Movie` implement `AggregateRoot<T, ID>` from JMolecules.

### 3. JSON:API Integration
The example shows how JSON:API for Spring HATEOAS works with:

- Value object identifiers in URLs (`/api/movies/{movieId}`)
- Proper serialization of value objects in JSON:API responses
- JMolecules annotations and Spring HATEOAS integration

## Running the Example

```bash
./gradlew :example-jmodulith:bootRun
```

The application starts on port 8080 with sample data.

## Sample API Requests

### Get all movies
```bash
curl http://localhost:8080/api/movies \
  -H "Accept: application/vnd.api+json"
```

### Get a specific movie
```bash
curl http://localhost:8080/api/movies/{movieId} \
  -H "Accept: application/vnd.api+json"
```

## Key Features

1. **Value Object Serialization**: Value objects like `MovieId`, `ImdbId`, and `Rating` are properly serialized as scalars in JSON:API responses, not as nested objects.

2. **Type-Safe Identifiers**: Using value objects for IDs provides type safety and prevents mixing up different entity IDs.

3. **Cross-Module References**: Movies reference directors (and vice versa) using value object identifiers, maintaining modular boundaries.

4. **JMolecules Integration**: The example uses JMolecules annotations and integrations for JPA and Jackson.

5. **Spring Modulith**: Module boundaries are defined using `@ApplicationModule` annotations in `package-info.java` files.

## Dependencies

This example adds the following dependencies beyond the basic example:

- `org.jmolecules:jmolecules-ddd` - Core DDD annotations
- `org.jmolecules.integrations:jmolecules-jackson` - Jackson support for value objects
- `org.jmolecules.integrations:jmolecules-jpa` - JPA support for value objects
- `org.springframework.modulith:spring-modulith-api` - Module structure support
- `org.springframework.modulith:spring-modulith-runtime` - Runtime module verification
- `org.springframework.modulith:spring-modulith-starter-test` - Testing support
