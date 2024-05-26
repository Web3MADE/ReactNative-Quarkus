package org.acme.controllers;

import java.util.List;
import java.util.stream.Collectors;
import org.acme.services.BlobService;
import org.acme.services.UserService;
import org.acme.services.VideoService;
import org.acme.user.User;
import org.acme.video.FileUploadInput;
import org.acme.video.LikeRequest;
import org.acme.video.Video;
import org.acme.video.VideoDTO;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/videos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class VideoController {

    @Inject
    VideoService videoService;

    @Inject
    UserService userService;

    @Inject
    BlobService blobService;

    @GET
    @PermitAll
    /**
     * @dev This WithSession indicates a session for reactive thread - had random issue earlier with
     *      this so placing on all endpoints
     */
    @WithSession
    public Uni<List<VideoDTO>> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GET
    @PermitAll
    @Path("/uploader/{id}")
    @WithSession
    public Uni<List<VideoDTO>> getVideosByUploader(@PathParam("id") Long id) {
        return videoService.getVideosByUploader(id);
    }

    @GET
    @Path("{id}")
    @PermitAll
    @WithSession
    public Uni<Response> getVideoById(@PathParam("id") Long id) {
        return videoService.getVideoById(id).onItem().ifNotNull()
                .transform(video -> Response.ok(video).build()).onItem().ifNull()
                .continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/liked/{id}")
    @PermitAll
    public Uni<List<VideoDTO>> getLikedVideosByUser(@PathParam("id") Long id) {
        return userService.getUserById(id).onItem().ifNotNull().transformToUni(user -> {
            List<VideoDTO> likedVids = userService.getLikedVideosByUser(user);
            return Uni.createFrom().item(likedVids);
        });
    }

    @GET
    @Path("/search")
    @PermitAll
    public Uni<List<VideoDTO>> searchVideos(@QueryParam("query") String query) {
        return Video.findByTitle(query).map(videos -> videos.stream()
                .map(video -> new VideoDTO((Video) video)).collect(Collectors.toList()));
    }

    @POST
    @PermitAll
    @WithTransaction
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> upload(FileUploadInput input) {
        return videoService.uploadVideo(input)
                .map(videoDTO -> Response.ok(videoDTO).status(Response.Status.CREATED).build())
                .onFailure()
                .recoverWithItem(th -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(th.getMessage()).build());
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
