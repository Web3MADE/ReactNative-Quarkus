package org.acme.video;

public class VideoDTO {
    private Long id;
    private String title;
    private String url;
    private String thumbnailUrl;
    private int likes;

    public VideoDTO(Video video) {
        this.id = video.id;
        this.title = video.title;
        this.url = video.url;
        this.thumbnailUrl = video.thumbnailUrl;
        this.likes = video.likes;
    }

    public VideoDTO() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


}
