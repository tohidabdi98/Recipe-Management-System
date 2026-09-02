# Recipe Management System

A Spring Boot REST API for storing and managing recipes.

## Features

- User registration with email and password validation
- BCrypt password hashing
- HTTP Basic authentication
- Persistent H2 file database storage
- Create, retrieve, update, and delete recipes
- Search recipes by category or name
- Case-insensitive search sorted by newest date
- Recipe ownership rules for updates and deletion

## Technology Stack

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- H2 Database
- Maven

## Run the Application

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

The H2 database is stored at:

```text
../recipes_db
```

## API Endpoints

### Register a user

`POST /api/register`

```json
{
  "email": "chef@example.com",
  "password": "RecipePassword123"
}
```

Registration does not require authentication.

### Create a recipe

`POST /api/recipe/new`

Requires HTTP Basic authentication.

```json
{
  "name": "Fresh Mint Tea",
  "category": "beverage",
  "description": "A light and refreshing drink.",
  "ingredients": [
    "boiled water",
    "honey",
    "fresh mint leaves"
  ],
  "directions": [
    "Boil water",
    "Pour the water into a mug",
    "Add mint leaves and honey"
  ]
}
```

Response:

```json
{
  "id": 1
}
```

### Retrieve a recipe

`GET /api/recipe/{id}`

Requires authentication. Other users may view recipes.

### Update a recipe

`PUT /api/recipe/{id}`

Requires authentication and recipe ownership. The recipe date is updated automatically.

### Delete a recipe

`DELETE /api/recipe/{id}`

Requires authentication and recipe ownership.

### Search recipes

`GET /api/recipe/search?category=beverage`

or:

`GET /api/recipe/search?name=tea`

Exactly one query parameter must be supplied. Search is case-insensitive and results are sorted by date, newest first.

## HTTP Status Codes

- `200 OK` — successful registration, creation, retrieval, or search
- `204 No Content` — successful update or deletion
- `400 Bad Request` — invalid input, duplicate email, or invalid search parameters
- `401 Unauthorized` — missing or invalid authentication
- `403 Forbidden` — authenticated user is not the recipe author
- `404 Not Found` — recipe does not exist

## Testing

```bash
mvn test
```

## Shutdown Endpoint

For testing purposes, the following endpoint is available without authentication:

`POST /actuator/shutdown`
