package com.civentre.dto;

import java.time.LocalDateTime;

public class IssueResponseDTO {

    private Long id;
    private Long departmentId;
    private String departmentName;
    private Long officerId;
    private String officerName;
    private String title;
    private String description;
    private String category;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imagePath;
    private String resolutionImagePath;
    private String status;
    private String priority;
    private long supportCount;
    private LocalDateTime createdAt;

    public IssueResponseDTO() {
    }

    public IssueResponseDTO(Long id, String title, String description,
                            String category, String location,
                            Double latitude, Double longitude,
                            String imagePath, String resolutionImagePath,
                            String status, String priority,
                            long supportCount,
                            Long departmentId, String departmentName,
                            Long officerId, String officerName,
                            LocalDateTime createdAt) {

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
        this.supportCount = supportCount;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.officerId = officerId;
        this.officerName = officerName;
        this.createdAt = createdAt;
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

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public long getSupportCount() {
        return supportCount;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Long getOfficerId() {
        return officerId;
    }

    public String getOfficerName() {
        return officerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}