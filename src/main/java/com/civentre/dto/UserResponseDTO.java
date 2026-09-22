package com.civentre.dto;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String role;
    private String departmentName;

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String name, String role, String departmentName) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.departmentName = departmentName;
    }
    public Long getId() {return id;}
    public String getName() {return name;}
    public String getRole() {return role;}
    public String getDepartmentName() {return departmentName;}
}