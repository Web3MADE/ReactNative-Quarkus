# LearnReactNative API

This is the API for the React Native app, which handles authenticated routes & file uploads.

The purpose of this project is to better understand how React Native & Quarkus operate.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Request and Response Examples](#request-and-response-examples)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Testing](#testing)

### Prerequisites

List of software and tools that are required to set up the project.

- **Java 17+**
- **Maven**: 3+ For building the project. Run `mvn -v` to check your version.
- **quarkus-CLI**: If you want a slightly better dev experience.
- **Docker**: If you are using Docker for database or other services, ensure Docker is installed and running.
- **Postgres**: (if you want Docker)

### Installation

1. Clone the repository at `https://github.com/Web3MADE/ReactNative-Quarkus.git` and `cd LearnReactNative`
2. navigate to `/api` and run `quarkus dev` or `mvn quarkus:dev` to start the api

## Usage

First step is to create a user. This will be saved to a postgres instance that is spun up by Quarkus - so you do not need to manage any docker containers.

```
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

Then post the user from the REST API client of your choice, the example below uses curl.

```
curl -X POST http://localhost:8080/api/users \
-H 'Content-Type: application/json' \
-d '{
"name": "John Doe",
"email": "john.doe@example.com",
"password": "password123"
}'
```

Your response should look contain a JWT token and the userId:

```
{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2V4YW1wbGUuY29tL2lzc3VlciIsInVwbiI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwiZ3JvdXBzIjpbIlVzZXIiXSwiYmlydGhkYXRlIjoiMjAwMS0wNy0xMyIsImlhdCI6MTcxNjc5NDY4MiwiZXhwIjoxNzE2Nzk0OTgyLCJqdGkiOiJmZDJhZDMxNy1iYzZmLTQ0MTEtYWZlMy0wYTU3ZjE2MWY4MTYifQ.CzwgtYbCOjJ9YzlMkqu9OUXxO2Bg-3QbjjHhZtONZjWqcl34624hafPvHFtKTfk9q1LJ82X1fbFCrP91WMdO5lJ29PEY9WxJZG_2aXtoKyLG3AjlZnwOhnm6gTIpoxJL5ofdJhIrLqOd2P5fNNuyD4jLOoHIcvtwMF_ac95CzVNYM4JLYByBEXYn1HOq40dQqGIC1cmbQJEaJt7DrphrTRB3n2GrTeLV1KpAwS50L_kCCeafD8IfZzSkE6pLrICHpJMFt8OmrIRrM03VVwaRXKiRflL5oS-ETr9n_N74HPXxyj37aDY2ZZ0JKd1aq38YyPRTMIGknwpC69uPEakcmg","userId":1}
```

From here on, you may pass the JWT token to the authenticated endpoints, the one's annotated with `@RolesAllowed({ "User" })`
