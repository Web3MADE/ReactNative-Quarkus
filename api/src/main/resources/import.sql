-- src/main/resources/import.sql
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL
);

INSERT INTO users (name, email, password) VALUES (1, 'Bomberman', 'Bomberman@gmail.com', '123456');
