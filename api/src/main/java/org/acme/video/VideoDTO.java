package org.acme.video;

public class VideoDTO {
    public Long id;
    public String title;
    public String url;
    public String thumbnailUrl;
    public int likes;

    public VideoDTO(Video video) {
        this.id = video.id;
        this.title = video.title;
        this.url = video.url;
        this.thumbnailUrl = video.thumbnailUrl;
        this.likes = video.likes;
    }
}
