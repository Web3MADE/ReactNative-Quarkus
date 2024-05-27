# LearnReactNative API

This is the API for the React Native app, which handles authenticated routes & file uploads.

The purpose of this project is to better understand how React Native & Quarkus operate.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Request and Response Examples](#request-and-response-examples)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Testing](#testing)

## Getting Started
Instructions on how to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

List of software and tools that are required to set up the project.

- **Java 17+**
- **Maven**: 3+ For building the project. Run `mvn -v` to check your version.
- **quarkus-CLI**: If you want a slightly better dev experience.
- **Docker**: If you are using Docker for database or other services, ensure Docker is installed and running.
- **Postgres**: (if you want Docker)

### Installation

1. Clone the repository at `https://github.com/Web3MADE/ReactNative-Quarkus.git` and `cd LearnReactNative`
2. navigate to `/api` and run `quarkus dev` or `mvn quarkus:dev`to start the api

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

curl -X POST http://localhost:8080/api/users \
-H 'Content-Type: application/json' \
-d '{
"name": "John Doe",
"email": "john.doe@example.com",
"password": "password123"
}'
