package com.career.dx1.domain.ai;

import java.util.List;
import java.util.Map;

public class AiResult {
    private String transactionId;
    private List<Map<String, Object>> result;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }
}
