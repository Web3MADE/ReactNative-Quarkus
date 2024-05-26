package org.acme.services;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.acme.repositories.UserRepository;
import org.acme.repositories.VideoRepository;
import org.acme.user.User;
import org.acme.video.FileUploadInput;
import org.acme.video.Video;
import org.acme.video.VideoDTO;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.quarkus.test.InjectMock;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@QuarkusTest
public class VideoServiceTest {

    @Inject
    VideoService videoService;

    @InjectMock
    BlobService blobService;

    @InjectMock
    VideoRepository videoRepo;

    @InjectMock
    UserRepository userRepo;

    @org.mockito.Mock
    private FileUploadInput fileUploadInput;

    @org.mockito.Mock
    private FileUpload fileUpload;

    private User user;
    private Video video;

    @BeforeEach
    public void setUp() {
        // Set up mock user
        user = new User();
        user.id = 1L;
        user.name = "test";
        user.email = "test@example.com";
        user.password = "test";

        // Set up mock video
        video = new Video();
        video.id = 1L;
        video.title = "test";
        video.url = "test.com";
        video.thumbnailUrl = "test.com";
        video.uploader = user;

        // Set the relationships
        user.uploadedVideos = Set.of(video);
        user.likedVideos = Set.of(video);
    }

    @RunOnVertxContext
    @Test
    void testGetAllVideos(UniAsserter asserter) {
        // arrange
        List<Video> videos = List.of((video));
        // act
        asserter.execute(() -> {
            when(videoRepo.getAllVideos()).thenReturn(Uni.createFrom().item(videos));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.getAllVideos(), videoDTOs -> {
            videoDTOs.equals(List.of(new VideoDTO(video)));
        });
    }

    @RunOnVertxContext
    @Test
    void testGetVideoById(UniAsserter asserter) {
        // arrange
        Long id = video.id;
        // act
        asserter.execute(() -> {
            when(videoRepo.getVideoById(id)).thenReturn(Uni.createFrom().item(video));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.getVideoById(id), videoDTO -> {
            videoDTO.equals(new VideoDTO(video));
        });
    }

    @RunOnVertxContext
    @Test
    void testGetVideosByUploader(UniAsserter asserter) {
        // arrange
        Long id = user.id;
        // act
        asserter.execute(() -> {
            when(videoRepo.getVideosByUploader(id))
                    .thenReturn(Uni.createFrom().item(List.of(video)));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.getVideosByUploader(id), videoDTOs -> {
            videoDTOs.equals(List.of(new VideoDTO(video)));
        });
    }

    @RunOnVertxContext
    @Test
    void getLikedVideosByUser(UniAsserter asserter) {
        // arrange
        Long id = user.id;
        // act
        asserter.execute(() -> {
            when(userRepo.getUserById(id)).thenReturn(Uni.createFrom().item(user));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.getVideosByUploader(id), videoDTOs -> {
            videoDTOs.equals(List.of(new VideoDTO(video)));
        });
    }

    @RunOnVertxContext
    @Test
    void testCreateVideo(TransactionalUniAsserter asserter) {
        // arrange
        String title = "test";
        String url = "test.com";
        String thumbnailUrl = "test.com";
        // act
        asserter.execute(() -> {
            when(videoRepo.createVideo(title, url, thumbnailUrl, user))
                    .thenReturn(Uni.createFrom().item(video));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.createVideo(title, url, thumbnailUrl, user),
                createdVideo -> {
                    createdVideo.equals(video);
                });
    }

    @RunOnVertxContext
    @Test
    void testPersistAndFlush(TransactionalUniAsserter asserter) {
        // act
        asserter.execute(() -> {
            when(videoRepo.persistAndFlush(video)).thenReturn(Uni.createFrom().item(video));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.persistAndFlush(video), persistedVideo -> {
            persistedVideo.equals(video);
        });
    }

    @RunOnVertxContext
    @Test
    void testUploadVideo(TransactionalUniAsserter asserter) {
        // act
        asserter.execute(() -> {
            // arrange
            // mock fileUploadInput methods
            when(fileUploadInput.title).thenReturn("test");
            when(fileUploadInput.video).thenReturn(fileUpload);
            when(fileUploadInput.thumbnail).thenReturn(fileUpload);
            when(fileUploadInput.uploaderId).thenReturn(user.id);
            when(fileUpload.uploadedFile()).thenReturn(Paths.get("path/to/video.mp4"));
            // Mock userRepo and blobService methods
            when(userRepo.getUserById(user.id)).thenReturn(Uni.createFrom().item(user));
            when(blobService.uploadFiles(anyString(), anyString(), anyString(), any(Path.class),
                    any(Path.class))).thenReturn(
                            Uni.createFrom().item(new String[] {"videoUrl", "thumbnailUrl"}));
            // mock saveVideo method
            when(videoRepo.createVideo(anyString(), anyString(), anyString(), any(User.class)))
                    .thenReturn(Uni.createFrom().item(video));
            return Uni.createFrom().voidItem();
        });

        asserter.assertThat(() -> videoService.uploadVideo(fileUploadInput), createdVideo -> {
            createdVideo.equals(new VideoDTO(video));
        });
    }


}
