package org.acme.azure;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletionStage;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.specialized.BlobAsyncClientBase;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
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
import reactor.core.publisher.Mono;;

@Path("/azure")
@ApplicationScoped
public class StorageBlobAsyncResource {

        @Inject
        BlobServiceAsyncClient blobServiceAsyncClient;

        @POST
        // TODO: Remove HTTP logic, turn into a service
        // integrate into Video upload endpoint in VideoResource
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.TEXT_PLAIN)
        public Uni<Response> uploadBlob(@RestForm("fileUpload") FileUpload fileUpload) {
                String containerName = "container-quarkus-azure-storage-blob-async";
                String blobName = "quarkus-azure-storage-blob-async-" + System.currentTimeMillis()
                                + ".txt";

                System.out.println("Uploading blob: " + blobName);

                Mono<BlockBlobItem> blockBlobItemMono = blobServiceAsyncClient
                                .createBlobContainerIfNotExists(containerName)
                                .map(container -> container.getBlobAsyncClient(blobName))
                                .flatMap(blobClient -> {
                                        try {
                                                InputStream inputStream = Files.newInputStream(
                                                                fileUpload.uploadedFile()
                                                                                .toAbsolutePath());
                                                return blobClient.upload(
                                                                BinaryData.fromStream(inputStream),
                                                                true);
                                        } catch (Exception e) {
                                                return Mono.error(e);
                                        }
                                });

                CompletionStage<BlockBlobItem> blockBlobItemStage = blockBlobItemMono.toFuture();

                return Uni.createFrom().completionStage(blockBlobItemStage)
                                .map((BlockBlobItem item) -> Response
                                                .status(Response.Status.CREATED)
                                                .entity("File uploaded successfully").build())
                                .onFailure().recoverWithItem(ex -> {
                                        System.out.println("Error: " + ex.getMessage());
                                        return Response.status(
                                                        Response.Status.INTERNAL_SERVER_ERROR)
                                                        .entity("File upload failed").build();
                                });
        }

        @GET
        @Path("/{fileName}")
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        public Uni<Response> downloadBlob(@PathParam("fileName") String fileName) {
                System.out.println("blob to download: " + fileName);
                BlobAsyncClientBase blobAsyncClient = blobServiceAsyncClient
                                .getBlobContainerAsyncClient(
                                                "container-quarkus-azure-storage-blob-async")
                                .getBlobAsyncClient(fileName);

                return Uni.createFrom()
                                .completionStage(blobAsyncClient.downloadContent().toFuture())
                                .emitOn(Infrastructure.getDefaultWorkerPool()) // Offload to worker
                                                                               // pool
                                .map(data -> Response.ok(data.toBytes()).build()).onFailure()
                                .recoverWithItem(ex -> {
                                        System.out.println("Error: " + ex.getMessage());
                                        return Response.status(
                                                        Response.Status.INTERNAL_SERVER_ERROR)
                                                        .entity("File download failed").build();
                                });
        }
}
