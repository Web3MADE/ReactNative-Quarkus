package org.acme.azure;

import java.io.InputStream;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.BlockBlobItem;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import reactor.core.publisher.Mono;;

@Path("/azure")
@ApplicationScoped
public class StorageBlobAsyncResource {

        public static class StorageFileUpload {
                // TODO: POST endpoint only accepts RestForm - will sync with videoResource later
                @RestForm("fileUpload")
                public FileUpload uploadedFile;
        }

        @Inject
        BlobServiceAsyncClient blobServiceAsyncClient;

        @POST
        // test uploading file works
        // 1. convert file to InputStream
        // 2. upload file to Azure Blob Storage using BinaryData.fromStream method
        // integrate into Video upload endpoint in VideoResource
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        public Uni<Response> uploadBlob(StorageFileUpload input) {
                FileUpload fileUpload = input.uploadedFile;
                String containerName = "container-quarkus-azure-storage-blob-async";
                String blobName = "quarkus-azure-storage-blob-async-" + System.currentTimeMillis()
                                + ".txt";

                System.out.println("Uploading blob: " + blobName);

                Mono<BlockBlobItem> blockBlobItem = blobServiceAsyncClient
                                .createBlobContainerIfNotExists(containerName)
                                .map(container -> container.getBlobAsyncClient(blobName))
                                .flatMap(blobClient -> {
                                        try (InputStream inputStream = fileUpload.uploadedFile()
                                                        .toFile().toURI().toURL().openStream()) {
                                                return blobClient.upload(
                                                                BinaryData.fromStream(inputStream),
                                                                true);
                                        } catch (Exception e) {
                                                return Mono.error(e);
                                        }
                                });


                return Uni.createFrom().completionStage(blockBlobItem.toFuture())
                                .map(item -> (Response) Response.status(Response.Status.ACCEPTED)
                                                .entity("File uploaded successfully").build())
                                .onFailure().recoverWithItem(ex -> {
                                        System.out.println("Error: " + ex.getMessage());
                                        return Response.status(
                                                        Response.Status.INTERNAL_SERVER_ERROR)
                                                        .entity("File upload failed").build();
                                });
        }

        @GET
        public Uni<Response> downloadBlob() {
                BlobAsyncClient blobAsyncClient = blobServiceAsyncClient
                                .getBlobContainerAsyncClient(
                                                "container-quarkus-azure-storage-blob-async")
                                .getBlobAsyncClient("quarkus-azure-storage-blob-async.txt");

                return Uni.createFrom().completionStage(blobAsyncClient.downloadContent()
                                .map(it -> Response.ok().entity(it.toString()).build()).toFuture());
        }
}
