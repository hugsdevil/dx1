package com.career.dx1.service.storage;

import com.azure.core.util.BinaryData;
import com.azure.spring.cloud.autoconfigure.implementation.storage.blob.properties.AzureStorageBlobProperties;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!default")
public class AzureBlobStorageServiceImpl implements StorageService {
    private final String CONAINTER_NAME = "blob-container";

    private BlobContainerClient blobContainerClient;

    public AzureBlobStorageServiceImpl(AzureStorageBlobProperties storageProperties) {
        StorageSharedKeyCredential credential =  new StorageSharedKeyCredential(
            storageProperties.getAccountName(), 
            storageProperties.getAccountKey());
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .endpoint(storageProperties.getEndpoint())
            .credential(credential)
            .buildClient();

        this.blobContainerClient = blobServiceClient.getBlobContainerClient(CONAINTER_NAME);
    }

    @Override
    public void upload(String blobName, byte[] bytes) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.upload(BinaryData.fromBytes(bytes));
    }

    @Override
    public byte[] download(String blobName) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        byte[] bytes = blobClient.downloadContent().toBytes();
        return bytes;
    }

    @Override
    public void delete(String blobName) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.delete();
    }
    
}
