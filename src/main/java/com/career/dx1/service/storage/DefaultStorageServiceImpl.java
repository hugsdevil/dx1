package com.career.dx1.service.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
public class DefaultStorageServiceImpl implements StorageService {

    @Override
    public void upload(String blobName, byte[] bytes) throws Exception {
        //
    }

    @Override
    public byte[] download(String blobName) throws Exception {
        return null;
    }

    @Override
    public void delete(String blobName) throws Exception {
        //
    }
    
}
