-- Create tables
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE videos (
    id BIGINT PRIMARY KEY,
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
INSERT INTO users (id, name, email) VALUES (3, 'Charlie', 'charlie@example.com');

-- Insert mock data for videos
INSERT INTO videos (id, title, url, thumbnailUrl, likes, uploader_id) VALUES (1, 'Video 1', '/uploads/mockMp4.mp4', '/uploads/twooptions.jpeg', 10, 1);
INSERT INTO videos (id, title, url, thumbnailUrl, likes, uploader_id) VALUES (2, 'Video 2', '/uploads/mockMp4.mp4', '/uploads/twooptions.jpeg', 20, 2);
INSERT INTO videos (id, title, url, thumbnailUrl, likes, uploader_id) VALUES (3, 'Video 3', '/uploads/video3.mp4', '/uploads/thumbnail3.jpg', 30, 3);
INSERT INTO videos (id, title, url, thumbnailUrl, likes, uploader_id) VALUES (4, 'Video 4', '/uploads/video4.mp4', '/uploads/thumbnail4.jpg', 40, 1);
INSERT INTO videos (id, title, url, thumbnailUrl, likes, uploader_id) VALUES (5, 'Video 5', '/uploads/video5.mp4', '/uploads/thumbnail5.jpg', 50, 2);

-- Insert mock data for liked videos
INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 1);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 2);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (2, 2);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (2, 3);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (3, 3);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (3, 4);
INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 5);
