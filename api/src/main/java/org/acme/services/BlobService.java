package org.acme.services;

import java.nio.file.Path;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
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
                        System.out.println("Uploading blob: " + blobName + " to container: "
                                        + containerName + " from file: " + filePath);
                        BinaryData binaryData = BinaryData.fromFile(filePath);

                        if (binaryData.getLength() == null) {
                                System.out.println("Binary data is null for input stream of blob: "
                                                + blobName);
                                return Uni.createFrom().failure(new IllegalArgumentException(
                                                "Binary data cannot be null"));
                        }

                        // Explicitly set headers for mp4 files to enable video streaming instead of
                        // downloading
                        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(
                                        blobName.endsWith(".mp4") ? "video/mp4" : "image/jpeg");

                        return Uni.createFrom()
                                        .completionStage(blobClient.uploadWithResponse(
                                                        binaryData.toFluxByteBuffer(),
                                                        binaryData.getLength(), headers, null, null,
                                                        null, null).toFuture())
                                        .map(response -> {
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
                                        "Exception occurred while creating BinaryData from file for blob: "
                                                        + blobName + ". Error: " + e.getMessage());
                        return Uni.createFrom().failure(e);
                }
        }

        public Uni<String[]> uploadFiles(String containerName, String videoFileName,
                        String thumbnailFileName, Path videoPath, Path thumbnailPath) {
                Uni<String> videoUrlUni = uploadBlob(containerName, videoFileName, videoPath);
                Uni<String> thumbnailUrlUni =
                                uploadBlob(containerName, thumbnailFileName, thumbnailPath);

                return Uni.combine().all().unis(videoUrlUni, thumbnailUrlUni).asTuple()
                                .map(tuple -> new String[] {tuple.getItem1(), tuple.getItem2()});
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
