package com.career.dx1.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.career.dx1.domain.Image;
import com.career.dx1.domain.ai.AiResponse;
import com.career.dx1.domain.type.InferenceStatus;

@Entity
@Table(name = "inference_image_info")
public class InferenceImageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 분석 이미지 ID
    @Column(name = "image_id")
    private Integer imageId;

    // 분석 ID
    @Column(name = "inference_id")
    private Integer inferenceId;

    // 분석 상태 코드
    @Column(name = "status")
    private InferenceStatus status = InferenceStatus.RUNNING;

    // 순서
    @Column(name = "image_order")
    private int imageOrder;

    // 원본 이미지 URL
    @Column(name = "org_image_url")
    private String orgImageUrl;

    // 원본 이미지 넓이
    @Column(name = "org_image_width")
    private String orgImageWidth;

    // 원본 이미지 높이
    @Column(name = "org_image_height")
    private String orgImageHeigth;

    // AI 분석 모델 전달 내용
    @Column(name = "send_data")
    private String sendData;

    // AI 분석 모델 전달 일시
    @Column(name = "send_at")
    private String sendAt;

    // AI 분석 모델 전달 내용
    @Column(name = "send_result_data")
    private String sendResultData;

    // AI 분석 모델 ID
    @Column(name = "inference_transaction_id")
    private String inferenceTransactionId;

    // AI 분석 모델 결과 내용
    @Column(name = "inference_result_data")
    private String inferenceResultData;

    // AI 분석 모델 결과 이미지 URL
    @Column(name = "inference_result_image_url")
    private String inferenceResultImageUrl;

    // AI 분석 모델 결과 일시
    @Column(name = "inference_result_at")
    private String inferenceResultAt;
    
    // 분석 실패 사유 내용
    @Column(name = "error_message")
    private String errorMessage;

    @Transient
    private Image image;

    public static InferenceImageInfo fromImage(Image image) {
        InferenceImageInfo v = new InferenceImageInfo();
        v.image = image;
        return v;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getInferenceId() {
        return inferenceId;
    }

    public void setInferenceId(Integer inferenceId) {
        this.inferenceId = inferenceId;
    }

    public InferenceStatus getStatus() {
        return status;
    }

    public void setStatus(InferenceStatus status) {
        this.status = status;
    }

    public int getImageOrder() {
        return imageOrder;
    }

    public void setImageOrder(int imageOrder) {
        this.imageOrder = imageOrder;
    }

    public String getOrgImageUrl() {
        return orgImageUrl;
    }

    public void setOrgImageUrl(String orgImageUrl) {
        this.orgImageUrl = orgImageUrl;
    }

    public String getOrgImageWidth() {
        return orgImageWidth;
    }

    public void setOrgImageWidth(String orgImageWidth) {
        this.orgImageWidth = orgImageWidth;
    }

    public String getOrgImageHeigth() {
        return orgImageHeigth;
    }

    public void setOrgImageHeigth(String orgImageHeigth) {
        this.orgImageHeigth = orgImageHeigth;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public String getSendResultData() {
        return sendResultData;
    }

    public void setSendResultData(String sendResultData) {
        this.sendResultData = sendResultData;
    }

    public String getInferenceTransactionId() {
        return inferenceTransactionId;
    }

    public void setInferenceTransactionId(String inferenceTransactionId) {
        this.inferenceTransactionId = inferenceTransactionId;
    }

    public void setSendResultData(AiResponse resp) {
        this.inferenceTransactionId = resp.getTransactionId();
        this.sendResultData = resp.toString();
    }

    public String getSendAt() {
        return sendAt;
    }

    public void setSendAt(String sendAt) {
        this.sendAt = sendAt;
    }

    public String getInferenceResultData() {
        return inferenceResultData;
    }

    public void setInferenceResultData(String inferenceResultData) {
        this.inferenceResultData = inferenceResultData;
    }

    public String getInferenceResultImageUrl() {
        return inferenceResultImageUrl;
    }

    public void setInferenceResultImageUrl(String inferenceResultImageUrl) {
        this.inferenceResultImageUrl = inferenceResultImageUrl;
    }

    public String getInferenceResultAt() {
        return inferenceResultAt;
    }

    public void setInferenceResultAt(String inferenceResultAt) {
        this.inferenceResultAt = inferenceResultAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setException(Exception e) {
        this.status = InferenceStatus.FAIL;
        this.errorMessage = e.getMessage();
    }

    public boolean isError() {
        if (image.getException() == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getFileName() {
        return UUID.randomUUID().toString() + ".jpg";
    }

    public byte[] getBytes() throws Exception {
        return image.getBytes();
    }
}
