package org.acme.video;

import java.util.Set;
import org.acme.user.User;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
public class Video extends PanacheEntity {
    public String title;
    public String url;
    public String thumbnailUrl;
    public String description;
    public String category;
    public int likes;
    
    @ManyToOne
    public String uploader;
    @ManyToMany(mappedBy = "likedVideos")
    public Set<User> likedByUsers;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getUploader() {
        return uploader;
    }

    public int getLikes() {
        return likes;
    }



}
