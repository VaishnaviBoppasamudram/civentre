package com.civentre.controller;

import com.civentre.entity.Department;
import com.civentre.entity.Issue;
import com.civentre.entity.Status;
import com.civentre.entity.User;
import com.civentre.repository.DepartmentRepository;
import com.civentre.repository.IssueRepository;
import com.civentre.repository.UserRepository;
import com.civentre.service.IssueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.civentre.dto.IssueResponseDTO;
import com.civentre.dto.UserResponseDTO;
import com.civentre.dto.DepartmentResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final IssueRepository issueRepository;
    private final IssueService issueService;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    public AdminController(
            IssueRepository issueRepository,
            IssueService issueService,
            DepartmentRepository departmentRepository,
            UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.issueService = issueService;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }
    // ==========================================
    // GET ALL ISSUES
    // ==========================================
    @GetMapping("/issues")
    public List<IssueResponseDTO> getAllIssues() {
        return issueRepository.findAll()
                .stream()
                .map(issueService::toIssueResponseDTO)
                .toList();
    }
    // ==========================================
    // GET ALL DEPARTMENTS
    // ==========================================
    @GetMapping("/departments")
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> new DepartmentResponseDTO(
                        department.getId(),
                        department.getName(),
                        department.getDescription(),
                        department.isActive()
                ))
                .toList();
    }
    // ==========================================
    // GET ALL OFFICERS
    // ==========================================
    @GetMapping("/officers")
    public List<UserResponseDTO> getAllOfficers() {
        return userRepository
                .findByRole(User.Role.OFFICER)
                .stream()
                .map(this::toUserResponseDTO)
                .toList();
    }
    // ==========================================
    // GET OFFICERS BY DEPARTMENT
    // ==========================================
    @GetMapping("/officers-by-department")
    public List<UserResponseDTO> getOfficersByDepartment(
            @RequestParam Long departmentId) {

        return userRepository
                .findByRole(User.Role.OFFICER)
                .stream()
                .filter(officer ->
                        officer.getDepartment() != null &&
                                officer.getDepartment().getId().equals(departmentId)
                )
                .map(this::toUserResponseDTO)
                .toList();
    }
    // ==========================================
    // ASSIGN DEPARTMENT
    // ==========================================
    @PutMapping("/issues/{issueId}/department")
    public IssueResponseDTO assignDepartment(
            @PathVariable Long issueId,
            @RequestParam Long departmentId) {
        Department department =
                departmentRepository.findById(departmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                )
                        );
        Issue updatedIssue = issueService.assignDepartment(issueId, department);
        return issueService.toIssueResponseDTO(updatedIssue);
    }
    // ==========================================
    // ASSIGN OFFICER
    // ==========================================
    @PutMapping("/issues/{issueId}/officer")
    public IssueResponseDTO assignOfficer(
            @PathVariable Long issueId,
            @RequestParam Long officerId) {
        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"
                                )
                        );

        User officer =
                userRepository.findById(officerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Officer not found"
                                )
                        );
        // Make sure selected user is actually an officer
        if (officer.getRole() != User.Role.OFFICER) {
            throw new RuntimeException(
                    "Selected user is not an officer"
            );
        }
        // Department must be assigned first
        if (issue.getDepartment() == null) {

            throw new RuntimeException(
                    "Please assign a department to the issue first"
            );
        }
        // Officer must belong to a department
        if (officer.getDepartment() == null) {
            throw new RuntimeException(
                    "Selected officer is not assigned to any department"
            );
        }
        // Officer must belong to the same department
        if (!officer.getDepartment()
                .getId()
                .equals(issue.getDepartment().getId())) {

            throw new RuntimeException(
                    "Officer does not belong to the issue department"
            );
        }
        Issue updatedIssue =
                issueService.assignOfficer(issueId, officer);

        return issueService.toIssueResponseDTO(updatedIssue);
    }
    // ==========================================
    // UPDATE ISSUE STATUS
    // ==========================================
    @PutMapping("/issues/{issueId}/status")
    public IssueResponseDTO updateIssueStatus(
            @PathVariable("issueId") Long issueId,
            @RequestParam("status")
            Status status,
            @RequestParam(
                    value = "remarks",
                    defaultValue = ""
            )
            String remarks) {
        Issue updatedIssue =
                issueService.updateIssueStatus(
                        issueId,
                        status,
                        remarks
                );

        return issueService.toIssueResponseDTO(updatedIssue);
    }
    // ==========================================
    // ADMIN STATISTICS
    // ==========================================
    @GetMapping("/stats")
    public Map<String, Long> getAdminStats() {
        Map<String, Long> stats =
                new HashMap<>();
        stats.put(
                "total",
                issueService.getTotalIssues()
        );
        stats.put(
                "reported",
                issueService.getReportedIssues()
        );
        stats.put(
                "inProgress",
                issueService.getInProgressIssues()
        );
        stats.put(
                "resolved",
                issueService.getResolvedIssues()
        );
        stats.put(
                "closed",
                issueService.getClosedIssues()
        );
        stats.put(
                "rejected",
                issueService.getRejectedIssues()
        );
        return stats;
    }
    // ==========================================
    // OFFICER WORKLOAD
    // ==========================================
    @GetMapping("/officer-workload")
    public List<Map<String, Object>> getOfficerWorkload() {
        List<User> officers =
                userRepository.findByRole(User.Role.OFFICER);
        return officers.stream()
                .map(officer -> {
                    Map<String, Object> data =
                            new HashMap<>();
                    data.put(
                            "id",
                            officer.getId()
                    );
                    data.put(
                            "name",
                            officer.getName()
                    );
                    data.put(
                            "total",
                            issueRepository.countByOfficer(
                                    officer
                            )
                    );
                    data.put(
                            "assigned",
                            issueRepository.countByOfficerAndStatus(
                                    officer,
                                    Status.ASSIGNED
                            ));
                    data.put(
                            "inProgress",
                            issueRepository.countByOfficerAndStatus(
                                    officer,
                                    Status.IN_PROGRESS
                            ));
                    data.put(
                            "resolved",
                            issueRepository.countByOfficerAndStatus(
                                    officer,
                                    Status.RESOLVED
                            ));
                    return data;
                })
                .toList();
    }
    // ==========================================
    // OFFICERS WITH WORKLOAD
    // ==========================================

    @GetMapping("/officers-with-workload")
    public List<Map<String, Object>> getOfficersWithWorkload() {
        List<User> officers =
                userRepository.findByRole(User.Role.OFFICER);
        return officers.stream()
                .map(officer -> {
                    Map<String, Object> data =
                            new HashMap<>();
                    data.put(
                            "id",
                            officer.getId()
                    );
                    data.put(
                            "name",
                            officer.getName()
                    );
                    data.put(
                            "total",
                            issueRepository.countByOfficer(
                                    officer
                            )
                    );
                    long active =
                            issueRepository.countByOfficerAndStatus(
                                    officer,
                                    Status.ASSIGNED
                            )
                                    +
                                    issueRepository.countByOfficerAndStatus(
                                            officer,
                                            Status.IN_PROGRESS
                                    );
                    data.put(
                            "active",
                            active
                    );
                    data.put(
                            "resolved",
                            issueRepository.countByOfficerAndStatus(
                                    officer,
                                    Status.RESOLVED
                            ));
                    return data;
                })
                .toList();
    }
    private UserResponseDTO toUserResponseDTO(User user) {
        String departmentName = user.getDepartment() != null
                ? user.getDepartment().getName()
                : null;
        String role = user.getRole() != null
                ? user.getRole().name()
                : null;
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                role,
                departmentName
        );
    }
}