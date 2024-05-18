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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/videos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class VideoResouce {
    // define custom class for FileUploadInput
    public static class FileUploadInput {
        // values are URL decoded by default
        // form field name is specified in the @FormParam annotation
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

    @POST
    @PermitAll
    @WithTransaction
    public Uni<Response> upload(FileUploadInput input) {

        System.out.println("title: " + input.title);
        System.out.println("uploaderId: " + input.uploaderId);
        System.out.println("fileUploadInput: " + input.video);
        System.out.println("thumbnailUpload: " + input.thumbnail);

        // TODO: WHY is this always null?
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


}
