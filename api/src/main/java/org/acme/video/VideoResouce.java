package org.acme.video;

import java.util.List;
import java.util.UUID;
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
    public Uni<List<Video>> getAll() {
        return Video.listAll();
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
                    video.url = "/uploads/" + "15523022-9827-4ef3-8fee-eef8d89cfdfa.jpg";
                    video.thumbnailUrl = "/uploads/" + "a5a1153a-ec0e-4d0f-bf96-72cb360ad9b7.mp4";
                    video.uploader = (User) uploader;

                    return video.persist().replaceWith(
                            Response.ok(video).status(Response.Status.CREATED).build());
                });
    }


}
