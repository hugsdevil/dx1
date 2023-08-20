package com.career.dx1.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.career.dx1.domain.entity.InferenceImageInfo;

public interface InferenceImageDataRepository extends JpaRepository<InferenceImageInfo, Integer> {
    
}
