package com.career.dx1.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.career.dx1.controller.body.InferenceRequest;
import com.career.dx1.service.InferenceService;

@RestController
@RequestMapping("/inferences")
public class InferenceController {
    private InferenceService inferenceService;

    public InferenceController(InferenceService inferenceService) {
        this.inferenceService = inferenceService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public void inference(
            @RequestPart(value = "files", required = true) List<MultipartFile> files) {
        inferenceService.inference(null, files);
    }

    @PostMapping("/results")
    public void result(
            @RequestPart(value = "json", required = true) InferenceRequest req,
            @RequestPart(value = "file", required = true) MultipartFile file) {
    }
}
