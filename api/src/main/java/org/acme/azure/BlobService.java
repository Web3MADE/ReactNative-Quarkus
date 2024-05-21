package org.acme.azure;

import java.nio.file.Path;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.specialized.BlobAsyncClientBase;
import com.azure.storage.blob.specialized.BlockBlobAsyncClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;;

@ApplicationScoped
public class BlobService {

        @Inject
        BlobServiceAsyncClient blobServiceAsyncClient;

        public Uni<String> uploadBlob(String containerName, String blobName, Path filePath) {

                BlockBlobAsyncClient blobClient = blobServiceAsyncClient
                                .getBlobContainerAsyncClient(containerName)
                                .getBlobAsyncClient(blobName).getBlockBlobAsyncClient();

                try {
                        BinaryData binaryData = BinaryData.fromFile(filePath);

                        System.out.println(binaryData);

                        if (binaryData.getLength() == null) {
                                System.out.println("Binary data is null for input stream of blob: "
                                                + blobName);
                                return Uni.createFrom().failure(new IllegalArgumentException(
                                                "Binary data cannot be null"));
                        }

                        return Uni.createFrom().completionStage(
                                        blobClient.upload(binaryData, true).toFuture())
                                        .map(blockBlobItem -> {
                                                String blobUrl = blobClient.getBlobUrl();
                                                System.out.println("Successfully uploaded blob: "
                                                                + blobName + " to URL: " + blobUrl);
                                                return blobUrl;
                                        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                                        .onFailure()
                                        .invoke(ex -> System.out.println("Failed to upload blob: "
                                                        + blobName + ". Error: "
                                                        + ex.getMessage()));

                } catch (Exception e) {
                        System.out.println(
                                        "Exception occurred while creating BinaryData from input stream for blob: "
                                                        + blobName + ". Error: " + e.getMessage());
                        return Uni.createFrom().failure(e);
                }
        }

        public Uni<Response> downloadBlob(String fileName) {
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
