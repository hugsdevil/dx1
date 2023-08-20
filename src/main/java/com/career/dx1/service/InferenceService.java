package com.career.dx1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.career.dx1.controller.body.InferenceRequest;
import com.career.dx1.domain.ai.AiResponse;
import com.career.dx1.domain.entity.InferenceImageInfo;
import com.career.dx1.service.storage.StorageService;
import com.career.dx1.util.MultipartFileToJpegImage;

@Service
public class InferenceService {
    Logger logger = LoggerFactory.getLogger(InferenceService.class);

    private RestTemplate restTemplate;
    private StorageService storageService;

    public InferenceService(RestTemplate restTemplate, StorageService storageService) {
        this.restTemplate = restTemplate;
        this.storageService = storageService;
    }

    public void inference(InferenceRequest req, List<MultipartFile> files) {
        List<InferenceImageInfo> imageInfos = files.stream()
            .map(file -> MultipartFileToJpegImage.convert(file))
            .map(image -> InferenceImageInfo.fromImage(image))
            .collect(Collectors.toList());
        imageInfos.stream()
            .forEach(imageInfo -> {
                try {
                    storageService.upload(imageInfo.getFileName(), imageInfo.getBytes());
                    sendAiRequest(imageInfo, "", "");
                } catch (Exception e) {
                    logger.error("{}", e);
                }
            });
    }

    public void sendAiRequest(InferenceImageInfo imageInfo, String url, String token) {
        try {
            if (!StringUtils.hasText(url)) {
                return;
            }
            ResponseEntity<AiResponse> responseEntity = 
                restTemplate.exchange(url, HttpMethod.POST, 
                    imageInfo.getImage().getHttpEntity(token), AiResponse.class);
            AiResponse resp = responseEntity.getBody();
            if (resp == null) {
                throw new RuntimeException("resp is null pointer");
            }
            imageInfo.setSendResultData(resp);
        } catch (Exception e) {
            logger.error("{}", e);
            imageInfo.setException(e);
        }
    }
}
