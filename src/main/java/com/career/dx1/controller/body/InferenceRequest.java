package com.career.dx1.controller.body;

import java.util.Map;

public class InferenceRequest {
    private Map<String, String> fileData;

    public Map<String, String> getFileData() {
        return fileData;
    }

    public void setFileData(Map<String, String> fileData) {
        this.fileData = fileData;
    }
}
