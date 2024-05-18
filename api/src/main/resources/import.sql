-- Create tables
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE videos (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    url VARCHAR(255),
    thumbnailUrl VARCHAR(255),
    likes INT,
    uploader_id BIGINT,
    FOREIGN KEY (uploader_id) REFERENCES users(id)
);

CREATE TABLE user_liked_videos (
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, video_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (video_id) REFERENCES videos(id)
);

-- Insert mock data for users
INSERT INTO users (id, name, email) VALUES (1, 'Alice', 'alice@example.com');
INSERT INTO users (id, name, email) VALUES (2, 'Bob', 'bob@example.com');

-- -- Insert mock data for videos
-- INSERT INTO videos (title, url, thumbnailUrl, likes, uploader_id) VALUES ('Video 1', '/uploads/mockMp4.mp4', '/uploads/twooptions.jpeg', 10, 1);
-- INSERT INTO videos (title, url, thumbnailUrl, likes, uploader_id) VALUES ('Video 2', '/uploads/mockMp4.mp4', '/uploads/twooptions.jpeg', 20, 2);

-- Insert mock data for liked videos
INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 1);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 2);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (2, 2);
