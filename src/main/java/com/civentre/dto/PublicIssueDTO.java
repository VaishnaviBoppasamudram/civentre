package com.civentre.dto;

import com.civentre.entity.Status;

import java.time.LocalDateTime;

public class PublicIssueDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imagePath;
    private String resolutionImagePath;
    private Status status;
    private String priority;
    private LocalDateTime createdAt;
    private long supportCount;


    public PublicIssueDTO(
            Long id,
            String title,
            String description,
            String category,
            String location,
            Double latitude,
            Double longitude,
            String imagePath,
            String resolutionImagePath,
            Status status,
            String priority,
            LocalDateTime createdAt,
            long supportCount) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
        this.resolutionImagePath = resolutionImagePath;
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

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getResolutionImagePath() {
        return resolutionImagePath;
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