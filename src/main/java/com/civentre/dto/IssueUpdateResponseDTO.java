package com.civentre.dto;

import java.time.LocalDateTime;

public class IssueUpdateResponseDTO {

    private Long id;
    private String status;
    private String remarks;
    private LocalDateTime updatedAt;

    public IssueUpdateResponseDTO() {
    }

    public IssueUpdateResponseDTO(
            Long id,
            String status,
            String remarks,
            LocalDateTime updatedAt) {

        this.id = id;
        this.status = status;
        this.remarks = remarks;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}