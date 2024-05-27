# LearnReactNative API

This is the API for the React Native app, which handles authenticated routes & file uploads.

The purpose of this project is to better understand how React Native & Quarkus operate.

**Note**: Currently, all endpoints are unauthenticated for development purposes. Just uncomment out the `@RolesAllowed` code to re-authenticate them.

## Hibernate Reactive with Panache

The API uses Hibernate Reactive with Panache, an extension of Hibernate ORM that works with reactive, non-blocking code.

So, why use non-blocking IO?

Traditional Hibernate ORM operations are reliant on database operations to complete, thus becoming very inefficient/slow at high, simultaneous requests.

A non-blocking IO allows for high-concurrency where many users can interact simultaneusly.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Services](#services)
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

The api will now be running on port 8080 at `http://localhost:8080`, unless you have changed it.

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

You can get the user you just created by calling the `getUserById` endpoint. Simply pass the userId and a valid JWT token.

```
curl -X GET http://localhost:8080/api/users/1 \
-H 'Content-Type: application/json' \
-d '{
"userId": 1,
"token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2V4YW1wbGUuY29tL2lzc3VlciIsInVwbiI6ImpvaG4uZG9lQGV4YW1wbGUuY29tIiwiZ3JvdXBzIjpbIlVzZXIiXSwiYmlydGhkYXRlIjoiMjAwMS0wNy0xMyIsImlhdCI6MTcxNjc5NTA1NSwiZXhwIjoxNzE2Nzk1MzU1LCJqdGkiOiJkZjQ2MTAwNi1hMWY0LTQyNDQtOTc1My0yNGE1NTZkZjlkYmYifQ.QkcohRIDKz_9OKNHisydrxLtzXM5q-Ha0789zrvFpcvRThLTRnpZCqr6Sy46QW3uVrcLmaZym7CttGokckL6W9AEA2N3ltiV0tqQO_erL_gbruTlLmrMTk0jCrfxRuM1_nY_GtHZWKKzIvlp-AcG2HoXfMVBXKnommvKOg3GQdFLQzwt05uKvUj0ru-atc633RysGrqegnnakYv_nXuap-d1BToIoyDyP3q2xujXiNTcUL41lCqINODL-26FZmgHAnp1lGSsnHhlySPGKNgrKSwSZS1213nfDbxYGkXn2XM_wnTGAgX68Hcnvk_MB-tgnLOpaLki2YztoI4l1VL8gw"
}'
```

Your response should look like this:

```
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "uploadedVideos": [],
  "likedVideos": []
}
```

## API Endpoints

A list of available API endpoints are outlined below:

### User Controller

The `UserController` handles all API requests regarding User information, except for videos which is managed in the `VideoController`.

- `createUser(UserDTO user)`
  - Accepts a `UserDTO` object and Creates a new user.
  - Returns a `UserResponse` object
- `getAllUsers()`
  - Returns all users as a list of `UserDTO`
- `getById(@PathParam("id") Long id)`
  - Accepts an `id` in the path parameter at `http://localhost:8080/api/users/{id}`
  - Returns a `UserDTO` object
- `login(UserDTO user)`
  - Accepts a `UserDTO` object & fetches the user if valid.
  - Returns a `UserResponse` object

### Video Controller

The `VideoController` handles all API requests regarding uploaded videos, including user-related videos.

- `getAllVideos()`
  - Returns a List of `VideoDTO` objects
- `getVideosByUploader(@PathParam("id) Long id)`
  - Accepts an `id` in the path parameter at `http://localhost:8080/api/videos/uploader/{id}`
  - Returns a `Response` object containg a List of `VideoDTO` objects from the `uploadedVideos` from a aspecific user
- `getVideoById(@PathParam("id") Long id)`
  - Accepts an `id` in the path parameter at `http://localhost:8080/api/videos/{id}`
  - Returns a `Response` object containg a specific `VideoDTO` object
- `getLikedVideosByUser(@PathParam("id") Long id)`
  - Accepts an `id` in the path parameter at `http://localhost:8080/api/videos/liked/{id}`
  - Returns a List of `VideoDTO` objects containing the `likedVideos` from a specific user
- `searchVideos(@QueryParam("query") String query)`
  - Searches the PostgresDB instance by the query passed at `http://localhost:8080/api/videos/search?query={query}`
  - Returns a List of `VideoDTO` objects
- `upload(FileUploadInput input)`
  - Accepts a `FileUploadInput` object, which is a custom type containing a videoUrl, thumbnailUrl, title and uploaderId
  - Uploaded files are stored in a public Azure container
  - Returns a `Repsonse` object containing the uploaded files (video + thumbnail).
- `likeVideo(@PathParam("videoId") Long videoId, LikeRequest request)`
  - Accepts a `videoId` and `request` (custom type for unknown reasons it wouldn't work otherwise) that likes a specific video from a specific user
  - Returns a `Response` object containing the liked video.

## Services

There are four services used in this API. Each are explained below.

### UserService

This service is responsible

### VideoService

- brief summary

### BlobService

- more detailed

### JwtTokenService

- more detailed

## Error Handling

This is a TODO for later, but it would follow the format:

1. Create `exception` folder
2. Define custom exception classes
3. Implement exception mappers
4. Implement custom exceptions into services

## Testing

### Service Tests

- outline the features of quarkus testing in services

### Controller Tests

- outline the features of quarkus testing in controllers
