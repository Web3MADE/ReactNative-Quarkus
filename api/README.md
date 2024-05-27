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
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2V4YW1wbGUuY29tL2lzc3VlciIsInVwbiI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwiZ3JvdXBzIjpbIlVzZXIiXSwiYmlydGhkYXRlIjoiMjAwMS0wNy0xMyIsImlhdCI6MTcxNjc5NTA1NSwiZXhwIjoxNzE2Nzk1MzU1LCJqdGkiOiJkZjQ2MTAwNi1hMWY0LTQyNDQtOTc1My0yNGE1NTZkZjlkYmYifQ.QkcohRIDKz_9OKNHisydrxLtzXM5q-Ha0789zrvFpcvRThLTRnpZCqr6Sy46QW3uVrcLmaZym7CttGokckL6W9AEA2N3ltiV0tqQO_erL_gbruTlLmrMTk0jCrfxRuM1_nY_GtHZWKKzIvlp-AcG2HoXfMVBXKnommvKOg3GQdFLQzwt05uKvUj0ru-atc633RysGrqegnnakYv_nXuap-d1BToIoyDyP3q2xujXiNTcUL41lCqINODL-26FZmgHAnp1lGSsnHhlySPGKNgrKSwSZS1213nfDbxYGkXn2XM_wnTGAgX68Hcnvk_MB-tgnLOpaLki2YztoI4l1VL8gw",
  "userId": 1,
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

From here on, you may pass the JWT token to the authenticated endpoints, the one's annotated with `@RolesAllowed({ "User" })`
