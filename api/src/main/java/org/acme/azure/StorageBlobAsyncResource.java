package org.acme.azure;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import java.time.LocalDateTime;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.BlockBlobItem;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import reactor.core.publisher.Mono;;

@Path("/quarkus-azure-storage-blob-async")
@ApplicationScoped
public class StorageBlobAsyncResource {

        @Inject
        BlobServiceAsyncClient blobServiceAsyncClient;

        @POST
        public Uni<Response> uploadBlob() {
                Mono<BlockBlobItem> blockBlobItem = blobServiceAsyncClient
                                .createBlobContainerIfNotExists(
                                                "container-quarkus-azure-storage-blob-async")
                                .map(it -> it.getBlobAsyncClient(
                                                "quarkus-azure-storage-blob-async.txt"))
                                .flatMap(it -> it.upload(BinaryData.fromString(
                                                "Hello quarkus-azure-storage-blob-async at "
                                                                + LocalDateTime.now()),
                                                true));

                return Uni.createFrom().completionStage(blockBlobItem.toFuture())
                                .map(it -> Response.status(CREATED).build());
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
