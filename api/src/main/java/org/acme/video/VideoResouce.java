package org.acme.video;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.acme.user.User;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
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
        @FormParam("video")
        public FileUpload video;

        @FormParam("thumbnail")
        public FileUpload thumbnail;

        @FormParam("title")
        public String title;

        @FormParam("uploaderId")
        public Long uploaderId;
    }

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

        System.out.println("title: " + input.title);
        System.out.println("uploaderId: " + input.uploaderId);
        System.out.println("fileUploadInput: " + input.video);
        System.out.println("thumbnailUpload: " + input.thumbnail);

        if (input.video == null) {
            // create a Uni (async operation) to emit a BAD REQUEST response
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Video file is missing").build());
        }
        if (input.thumbnail == null) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Thumbnail file is missing").build());
        }

        String videoFileName = UUID.randomUUID().toString() + ".mp4";
        String thumbnailFileName = UUID.randomUUID().toString() + ".jpg";

        System.out.println("videoFileName: " + videoFileName);
        System.out.println("thumbnailFileName: " + thumbnailFileName);

        return Panache.withTransaction(() -> User.findById(input.uploaderId)).onItem().ifNotNull()
                .transformToUni(uploader -> {
                    Video video = new Video();
                    video.title = input.title;
                    video.url = "/uploads/" + videoFileName;
                    video.thumbnailUrl = "/uploads/" + thumbnailFileName;
                    video.uploader = (User) uploader;

                    return video.persist().replaceWith(Response.ok(new VideoDTO(video))
                            .status(Response.Status.CREATED).build());
                });
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
