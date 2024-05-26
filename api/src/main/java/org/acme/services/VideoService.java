package org.acme.services;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.acme.controllers.VideoController.FileUploadInput;
import org.acme.repositories.UserRepository;
import org.acme.repositories.VideoRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.acme.utils.Constants;
import org.acme.video.Video;
import org.acme.video.VideoDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VideoService {

    @Inject
    BlobService blobService;

    @Inject
    UserRepository userRepo;

    @Inject
    UserService userService;

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

    public Uni<List<VideoDTO>> getVideosByUploader(Long id) {
        return videoRepo.getVideosByUploader(id).map(videos -> videos.stream()
                .map(this::mapVideoToVideoDTO).collect(Collectors.toList()));
    }

    public Uni<Video> createVideo(String title, String url, String thumbnailUrl, User user) {
        return videoRepo.createVideo(title, url, thumbnailUrl, user);
    }

    public Uni<Video> persistAndFlush(Video video) {
        return videoRepo.persistAndFlush(video);
    }

    // 1. getUserById(input.uploaderId)
    // 2. uploadFiles (input.video, input.thumbnail, title)
    // 3. saveVideo(user, title, urls[0], urls[1])
    public Uni<VideoDTO> uploadVideo(FileUploadInput input) {
        Uni<User> userUni = userRepo.getUserById(input.uploaderId);
        Uni<String[]> urlsUni = uploadFiles(input);

        return Uni.combine().all().unis(userUni, urlsUni).asTuple().onItem()
                .transformToUni(tuple -> {
                    User user = tuple.getItem1();
                    String[] urls = tuple.getItem2();
                    return saveVideo(user, input.title, urls[0], urls[1]).map(VideoDTO::new);
                });
    }

    private String generateFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }

    private Uni<String[]> uploadFiles(FileUploadInput input) {
        String videoFileName = generateFileName(".mp4");
        String thumbnailFileName = generateFileName(".jpg");
        Path videoPath = input.video.uploadedFile();
        Path thumbnailPath = input.thumbnail.uploadedFile();
        return blobService.uploadFiles(Constants.CONTAINER_NAME, videoFileName, thumbnailFileName,
                videoPath, thumbnailPath);
    }

    private Uni<Video> saveVideo(User user, String title, String videoUrl, String thumbnailUrl) {
        Video video = new Video();
        video.title = title;
        video.url = videoUrl;
        video.thumbnailUrl = thumbnailUrl;
        video.uploader = user;

        return video.persist().flatMap(v -> updateUserWithVideo(user, video));
    }

    private Uni<Video> updateUserWithVideo(User user, Video video) {
        user.uploadedVideos.add(video);
        return userService.persistAndFlush(user).replaceWith(video);
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

    private Video mapVideoDTOtoVideo(VideoDTO videoDTO) {
        if (videoDTO == null) {
            return null;
        }

        Video video = new Video();
        video.id = videoDTO.getId();
        video.title = videoDTO.getTitle();
        video.url = videoDTO.getUrl();
        video.thumbnailUrl = videoDTO.getThumbnailUrl();
        video.likes = videoDTO.getLikes();

        return video;
    }

    // TODO: handle DTO/BO stuff later
    private User mapUserDTOtoUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.id = userDTO.getId();
        user.name = userDTO.getName();
        user.email = userDTO.getEmail();
        user.password = userDTO.getPassword(); // TODO: hash password func
        user.uploadedVideos = userDTO.getUploadedVideos().stream().map(this::mapVideoDTOtoVideo)
                .collect(Collectors.toSet());
        user.uploadedVideos = userDTO.getUploadedVideos().stream().map(this::mapVideoDTOtoVideo)
                .collect(Collectors.toSet());

        return user;
    }

}
