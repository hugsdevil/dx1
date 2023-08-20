package com.career.dx1.service.storage;

public interface StorageService {
    void upload(String blobName, byte[] bytes) throws Exception;
    byte[] download(String blobName) throws Exception;
    void delete(String blobName) throws Exception;
}
