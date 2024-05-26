package org.acme.services;


import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Set;
import org.acme.repositories.VideoRepository;
import org.acme.user.User;
import org.acme.video.Video;
import org.acme.video.VideoDTO;
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
    VideoRepository videoRepo;

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


}
