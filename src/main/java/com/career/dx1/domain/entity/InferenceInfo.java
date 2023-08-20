package com.career.dx1.domain.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.career.dx1.domain.type.InferenceStatus;

@Entity
@Table(name = "inference_info")
public class InferenceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 분석 ID
    @Column(name = "id")
    private Integer InferenceId;

    // 분석 상태 코드
    @Column(name = "status")
    private InferenceStatus status = InferenceStatus.RUNNING;
    
    // 전체 이미지 개수
    @Column(name = "image_total_count")
    private int imageTotalCount;

    // 분석 성공 이미지 개수
    @Column(name = "image_success_count")
    private int imageSuccessCount;

    // 분석 실패 이미지 개수
    @Column(name = "image_fail_count")
    private int imageFailCount;

    // 전체 분석 결과 일시
    @Column(name = "result_at")
    private String resultAt;

    @Transient
    private List<InferenceImageInfo> images;
}
