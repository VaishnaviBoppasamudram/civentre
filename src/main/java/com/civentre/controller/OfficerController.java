package com.civentre.controller;

import com.civentre.entity.Issue;
import com.civentre.entity.Status;
import com.civentre.entity.User;
import com.civentre.repository.UserRepository;
import com.civentre.service.IssueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.civentre.dto.IssueResponseDTO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/officer")
@PreAuthorize("hasRole('OFFICER')")
public class OfficerController {

    private final IssueService issueService;
    private final UserRepository userRepository;

    @Value("${civentre.upload-dir}")
    private String uploadDir;

    public OfficerController(
            IssueService issueService,
            UserRepository userRepository) {

        this.issueService = issueService;
        this.userRepository = userRepository;
    }

    // =====================================================
    // GET ASSIGNED ISSUES
    // =====================================================

    @GetMapping("/issues")
    public List<IssueResponseDTO> getAssignedIssues(
            Authentication authentication) {
        User officer =
                getLoggedInOfficer(authentication);
        return issueService.getIssuesByOfficer(officer)
                .stream()
                .map(issueService::toIssueResponseDTO)
                .toList();
    }
    // =====================================================
    // GET OFFICER STATISTICS
    // =====================================================
    @GetMapping("/stats")
    public Map<String, Long> getOfficerStats(
            Authentication authentication) {
        User officer =
                getLoggedInOfficer(authentication);
        Map<String, Long> stats =
                new HashMap<>();
        stats.put(
                "total",
                issueService.getTotalIssuesByOfficer(officer)
        );
        stats.put(
                "pending",
                issueService.getPendingIssuesByOfficer(officer)
        );
        stats.put(
                "inProgress",
                issueService.getInProgressIssuesByOfficer(officer)
        );
        stats.put(
                "resolved",
                issueService.getResolvedIssuesByOfficer(officer)
        );
        return stats;
    }
    // =====================================================
    // UPDATE ISSUE STATUS
    // =====================================================
    @PutMapping("/issues/{issueId}/status")
    public ResponseEntity<?> updateIssueStatus(
            @PathVariable Long issueId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            User officer =
                    getLoggedInOfficer(authentication);
            String statusText =
                    request.get("status");
            String remarks =
                    request.get("remarks");
            if (statusText == null ||
                    statusText.trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Status is required."
                                ));
            }
            Status status;
            try {
                status =
                        Status.valueOf(
                                statusText
                                        .trim()
                                        .toUpperCase()
                        );
            } catch (IllegalArgumentException e) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid status: "
                                                + statusText
                                )
                        );
            }
            if (remarks == null) {
                remarks = "";
            }
            Issue updatedIssue =
                    issueService.updateOfficerIssueStatus(
                            issueId,
                            status,
                            remarks,
                            officer
                    );
            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Issue status updated successfully.",
                            "issueId",
                            updatedIssue.getId(),
                            "status",
                            updatedIssue.getStatus().name()
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to update issue status."
                            )
                    );
        }
    }
    // =====================================================
    // UPLOAD RESOLUTION IMAGE
    // =====================================================
    @PostMapping("/issues/{issueId}/resolution-image")
    public Issue uploadResolutionImage(
            @PathVariable Long issueId,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) throws IOException {
        User officer =
                getLoggedInOfficer(authentication);
        Issue issue =
                issueService.getIssueById(issueId);
        // Make sure this officer owns the assignment
        if (issue.getOfficer() == null ||
                !issue.getOfficer()
                        .getId()
                        .equals(officer.getId())) {
            throw new RuntimeException(
                    "You are not assigned to this issue"
            );
        }

        // Check image exists
        if (image == null || image.isEmpty()) {

            throw new RuntimeException(
                    "Please select a resolution image"
            );
        }

        // Maximum 10 MB
        if (image.getSize() > 10 * 1024 * 1024) {

            throw new RuntimeException(
                    "Image size must not exceed 10 MB"
            );
        }

        // Check MIME type
        String contentType =
                image.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new RuntimeException(
                    "Only image files are allowed"
            );
        }

        // Check original filename
        String originalName =
                image.getOriginalFilename();

        if (originalName == null ||
                !originalName.contains(".")) {

            throw new RuntimeException(
                    "Invalid image file"
            );
        }

        // Get extension
        String extension =
                originalName
                        .substring(
                                originalName.lastIndexOf(".")
                        )
                        .toLowerCase();

        // Allow only safe image extensions
        if (!extension.equals(".jpg")
                && !extension.equals(".jpeg")
                && !extension.equals(".png")
                && !extension.equals(".webp")) {

            throw new RuntimeException(
                    "Only JPG, JPEG, PNG, and WEBP images are allowed"
            );
        }

        // Verify actual file signature
        if (!isValidImageSignature(image)) {

            throw new RuntimeException(
                    "The uploaded file is not a valid JPG, PNG, or WEBP image"
            );
        }

        // Generate random filename
        String fileName =
                UUID.randomUUID() + extension;

        // Use external upload directory
        Path uploadPath =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        Files.createDirectories(uploadPath);

        Path filePath =
                uploadPath
                        .resolve(fileName)
                        .normalize();

        // Prevent path traversal
        if (!filePath.getParent()
                .equals(uploadPath)) {

            throw new RuntimeException(
                    "Invalid file path"
            );
        }

        // Save image
        Files.copy(
                image.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // Save filename in database
        issue.setResolutionImagePath(fileName);

        return issueService.saveIssue(issue);
    }

    // =====================================================
    // IMAGE SIGNATURE VALIDATION
    // =====================================================

    private boolean isValidImageSignature(
            MultipartFile image)
            throws IOException {

        try (InputStream inputStream =
                     image.getInputStream()) {

            byte[] header =
                    new byte[12];

            int bytesRead =
                    inputStream.read(header);

            if (bytesRead < 12) {
                return false;
            }

            // JPEG: FF D8 FF
            boolean jpeg =
                    (header[0] & 0xFF) == 0xFF &&
                            (header[1] & 0xFF) == 0xD8 &&
                            (header[2] & 0xFF) == 0xFF;

            // PNG: 89 50 4E 47 0D 0A 1A 0A
            boolean png =
                    (header[0] & 0xFF) == 0x89 &&
                            (header[1] & 0xFF) == 0x50 &&
                            (header[2] & 0xFF) == 0x4E &&
                            (header[3] & 0xFF) == 0x47 &&
                            (header[4] & 0xFF) == 0x0D &&
                            (header[5] & 0xFF) == 0x0A &&
                            (header[6] & 0xFF) == 0x1A &&
                            (header[7] & 0xFF) == 0x0A;

            // WEBP: RIFF....WEBP
            boolean webp =
                    header[0] == 'R' &&
                            header[1] == 'I' &&
                            header[2] == 'F' &&
                            header[3] == 'F' &&
                            header[8] == 'W' &&
                            header[9] == 'E' &&
                            header[10] == 'B' &&
                            header[11] == 'P';

            return jpeg || png || webp;
        }
    }

    // =====================================================
    // GET LOGGED-IN OFFICER
    // =====================================================

    private User getLoggedInOfficer(
            Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException(
                    "Authentication required"
            );
        }
        String email =
                authentication.getName();
        User officer =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Officer not found"
                                ));
        if (officer.getRole()
                != User.Role.OFFICER) {

            throw new RuntimeException(
                    "Access denied: User is not an officer"
            );
        }
        return officer;
    }
}