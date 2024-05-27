package org.acme.repositories;

import java.util.List;
import org.acme.user.User;
import org.acme.video.Video;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VideoRepository implements PanacheRepository<Video> {

    public Uni<List<Video>> getAllVideos() {
        return listAll();
    }

    public Uni<Video> getVideoById(Long id) {
        return findById(id);
    }

    public Uni<List<Video>> getVideosByUploader(Long id) {
        return list("uploader.id", id);
    }

    public Uni<List<Video>> searchByTitle(String title) {
        return list("LOWER(title) LIKE LOWER(?1)", "%" + title + "%");
    }

    public Uni<Video> createVideo(String title, String url, String thumbnailUrl, User user) {
        Video video = new Video();
        video.title = title;
        video.url = url;
        video.thumbnailUrl = thumbnailUrl;
        video.uploader = user;

        return video.persist().onItem().transform(v -> video);
    }

    public Uni<Video> persistAndFlush(Video video) {
        return video.persistAndFlush().onItem().transform(v -> video);
    }


}
