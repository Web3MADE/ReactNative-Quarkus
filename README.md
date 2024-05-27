# ReactNative-Quarkus

A React Native application for uploading Images and videos (similar to Instagram).

**IMPORTANT NOTE**: This project has been abandoned, as I am switching to React Native CLI instead of Expo.

## Tech Stack

- Quarkus API (Reactive programming for User Auth and Transactional logic)
- Nativewind (CSS)
- Expo (Routing & DevEx)
- Postgres (Database)

## Dev Notes

- Make sure the debugger can be opened, else restart the server and clear expo cache with `npm run start`
- Videos are removed due to being too large to push
- Run postgres locally with this command `docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:13.3` or cd to `/api` and run `docker-compose up -d`

## Screenshots

<img width="432" alt="Screenshot 2024-05-06 at 12 21 54" src="https://github.com/Web3MADE/ReactNative-Quarkus/assets/115392932/c5fc6cc3-06b2-4bb9-b1a7-830e673c97be">

<img width="435" alt="Screenshot 2024-05-06 at 12 22 06" src="https://github.com/Web3MADE/ReactNative-Quarkus/assets/115392932/47156d4d-bbe0-4d64-ae39-a62ef3046517">
