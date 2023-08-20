package com.career.dx1.domain.ai;

public class AiResponse {
    private int status;
    private String transactionId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "AiResponse [status=" + status + ", transactionId=" + transactionId + "]";
    }
}
