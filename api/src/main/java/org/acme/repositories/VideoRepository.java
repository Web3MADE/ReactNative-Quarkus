package org.acme.repositories;

import java.util.List;
import org.acme.video.Video;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VideoRepository implements PanacheRepository<Video> {

    public Uni<List<Video>> getAllVideos() {
        return Video.listAll();
    }

    public Uni<Video> getVideoById(Long id) {
        return Video.findById(id);
    }

}
