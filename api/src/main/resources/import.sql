-- Mock data for users
INSERT INTO users (id, name, email, password) VALUES (1, 'Alice', 'alice@example.com', '123');
INSERT INTO users (id, name, email, password) VALUES (2, 'Bob', 'bob@example.com', '456');
INSERT INTO users (id, name, email, password) VALUES (3, 'Charlie', 'charlie@example.com', '789');

-- -- Mock data for videos
-- INSERT INTO videos (id, title, url, thumbnail_url, likes, uploader_id) VALUES (1, 'Video 1', '/uploads/video1.mp4', '/uploads/thumbnail1.jpg', 10, 1);
-- INSERT INTO videos (id, title, url, thumbnail_url, likes, uploader_id) VALUES (2, 'Video 2', '/uploads/video2.mp4', '/uploads/thumbnail2.jpg', 20, 2);
-- INSERT INTO videos (id, title, url, thumbnail_url, likes, uploader_id) VALUES (3, 'Video 3', '/uploads/video3.mp4', '/uploads/thumbnail3.jpg', 30, 3);
-- INSERT INTO videos (id, title, url, thumbnail_url, likes, uploader_id) VALUES (4, 'Video 4', '/uploads/video4.mp4', '/uploads/thumbnail4.jpg', 40, 1);
-- INSERT INTO videos (id, title, url, thumbnail_url, likes, uploader_id) VALUES (5, 'Video 5', '/uploads/video5.mp4', '/uploads/thumbnail5.jpg', 50, 2);

-- -- Mock data for liked videos (many-to-many relationship)
-- INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 2);
-- INSERT INTO user_liked_videos (user_id, video_id) VALUES (2, 3);
-- INSERT INTO user_liked_videos (user_id, video_id) VALUES (3, 1);
-- INSERT INTO user_liked_videos (user_id, video_id) VALUES (1, 4);
-- INSERT INTO user_liked_videos (user_id, video_id) VALUES (2, 5);
