package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.FileUploadResponse;
import com.abatef.fastc2.enums.UploadStatus;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AzureStorageService implements StorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;


    private final Logger logger = LoggerFactory.getLogger(AzureStorageService.class);


    @Override
    public FileUploadResponse upload(MultipartFile file) throws IOException {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("upload");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = timestamp + "_" + originalFileName;
            logger.info("Uploading file {}", uniqueFileName);
            BlobClient blobClient = blobContainerClient.getBlobClient(uniqueFileName);
            try (InputStream data = file.getInputStream()) {
                blobClient.upload(data, file.getSize());
            }
            logger.info("Uploaded file {}", uniqueFileName);
            logger.info("Uploaded file extension {}", fileExtension);
            logger.info("Uploaded file content {}", file.getContentType());
            logger.info("Uploaded file timestamp {}", timestamp);
            logger.info("Uploaded file url {}", blobClient.getBlobUrl());

            return FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(blobClient.getBlobUrl())
                    .fileType(file.getContentType())
                    .message("Successfully uploaded " + file.getOriginalFilename())
                    .status(UploadStatus.SUCCESS)
                    .build();
        } catch (Exception e) {
            return FileUploadResponse.builder()
                    .message("Failed to upload file.")
                    .status(UploadStatus.FAILED)
                    .build();
        }
    }
}
