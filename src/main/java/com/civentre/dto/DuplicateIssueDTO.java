package com.civentre.dto;

import com.civentre.entity.Status;

import java.time.LocalDateTime;

public class DuplicateIssueDTO {

    private Long id;
    private String title;
    private String category;
    private String location;
    private String imagePath;
    private Status status;
    private String priority;
    private LocalDateTime createdAt;
    private long supportCount;

    public DuplicateIssueDTO(
            Long id,
            String title,
            String category,
            String location,
            String imagePath,
            Status status,
            String priority,
            LocalDateTime createdAt,
            long supportCount) {

        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.imagePath = imagePath;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.supportCount = supportCount;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Status getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getSupportCount() {
        return supportCount;
    }
}