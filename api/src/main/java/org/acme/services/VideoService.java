package org.acme.services;

import java.util.List;
import java.util.stream.Collectors;
import org.acme.repositories.VideoRepository;
import org.acme.video.Video;
import org.acme.video.VideoDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VideoService {

    @Inject
    VideoRepository videoRepo;

    public Uni<List<VideoDTO>> getAllVideos() {
        return videoRepo.getAllVideos().map(
                // this::mapVideoToVideoDTO is a method reference to mapVideoToVideoDTO
                videos -> videos.stream().map(this::mapVideoToVideoDTO)
                        .collect(Collectors.toList()));
    }

    public Uni<VideoDTO> getVideoById(Long id) {
        return videoRepo.getVideoById(id).map(this::mapVideoToVideoDTO);
    }

    private VideoDTO mapVideoToVideoDTO(Video video) {
        if (video == null) {
            return null;
        }

        VideoDTO videoDTO = new VideoDTO();
        videoDTO.setId(video.id);
        videoDTO.setTitle(video.title);
        videoDTO.setUrl(video.url);
        videoDTO.setThumbnailUrl(video.thumbnailUrl);
        videoDTO.setLikes(video.likes);

        return videoDTO;
    }

}
