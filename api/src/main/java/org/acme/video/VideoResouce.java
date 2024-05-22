package org.acme.video;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.acme.azure.BlobService;
import org.acme.user.User;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/videos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class VideoResouce {

    // define custom class for FileUploadInput
    public static class FileUploadInput {
        // values are URL decoded by default
        // form field name is specified in the @FormParam annotation
        // MAY need to change to RestForm if can't upload to Azure
        @RestForm("video")
        public FileUpload video;

        @RestForm("thumbnail")
        public FileUpload thumbnail;

        @RestForm("title")
        public String title;

        @RestForm("uploaderId")
        public Long uploaderId;
    }

    @Inject
    BlobService blobService;

    @GET
    @PermitAll
    public Uni<List<VideoDTO>> getAllVideos() {
        return Video.listAll().map(videos -> videos.stream()
                .map(video -> new VideoDTO((Video) video)).collect(Collectors.toList()));
    }

    @GET
    @PermitAll
    @Path("/uploader/{id}")
    public Uni<List<VideoDTO>> getVideosByUploader(@PathParam("id") Long id) {
        return Video.find("uploader.id", id).list().map(videos -> videos.stream()
                .map(video -> new VideoDTO((Video) video)).collect(Collectors.toList()));
    }

    @GET
    @Path("{id}")
    @PermitAll
    public Uni<Response> getVideoById(@PathParam("id") Long id) {
        return Video.findById(id).onItem().ifNotNull()
                .transform(video -> Response.ok(new VideoDTO((Video) video)).build()).onItem()
                .ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @PermitAll
    @WithTransaction
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> upload(FileUploadInput input) {
        String videoFileName = UUID.randomUUID().toString() + ".mp4";
        String thumbnailFileName = UUID.randomUUID().toString() + ".jpg";
        java.nio.file.Path videoPath = input.video.uploadedFile();
        java.nio.file.Path thumbnailPath = input.thumbnail.uploadedFile();
        // TODO refactor: containerName should be a constant
        String containerName = "container-quarkus-azure-storage-blob-async";

        return blobService.uploadBlob(containerName, videoFileName, videoPath)
                .flatMap(videoUrl -> blobService
                        .uploadBlob(containerName, thumbnailFileName, thumbnailPath)
                        .flatMap(thumbnailUrl -> Panache.withSession(
                                () -> User.findById(input.uploaderId).flatMap(userObj -> {
                                    User user = (User) userObj;
                                    Video video = new Video();
                                    video.title = input.title;
                                    video.url = videoUrl;
                                    video.thumbnailUrl = thumbnailUrl;
                                    video.uploader = user;

                                    return Panache.withTransaction(video::persist)
                                            .replaceWith(() -> {
                                                user.uploadedVideos.add(video);
                                                return Response.ok(new VideoDTO(video))
                                                        .status(Response.Status.CREATED).build();
                                            });
                                }))));
    }

    @POST
    @Path("{videoId}/like")
    @PermitAll
    // @RolesAllowed({"User", "Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> likeVideo(@PathParam("videoId") Long videoId, LikeRequest request) {
        Long userId = request.userId;

        return Panache
                .withTransaction(() -> User.findById(userId).onItem().transformToUni(userObj -> {
                    if (userObj == null) {
                        return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND)
                                .entity("User not found").build());
                    }
                    return Video.findById(videoId).onItem().transformToUni(videoObj -> {
                        User user = (User) userObj;
                        Video video = (Video) videoObj;

                        if (video == null) {
                            // LOGGER.errorf("Video with ID %d not found", videoId);
                            return Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND)
                                    .entity("Video not found").build());
                        }
                        if (video.likedByUsers.add(user)) {
                            video.likes += 1;
                            user.likedVideos.add(video);
                        }
                        return video.persistAndFlush()
                                .replaceWith(Response.ok(new VideoDTO((Video) video)).build());
                    });
                }));
    }


}
