package com.civentre.controller;

import com.civentre.dto.DuplicateIssueDTO;
import com.civentre.dto.PublicIssueDTO;
import com.civentre.entity.Issue;
import com.civentre.entity.IssueUpdate;
import com.civentre.entity.User;
import com.civentre.repository.IssueUpdateRepository;
import com.civentre.repository.UserRepository;
import com.civentre.service.DuplicateIssueService;
import com.civentre.service.IssueService;
import com.civentre.service.IssueVoteService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import com.civentre.dto.IssueResponseDTO;
import com.civentre.dto.IssueUpdateResponseDTO;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserRepository userRepository;
    private final IssueUpdateRepository issueUpdateRepository;
    private final IssueVoteService issueVoteService;
    private final DuplicateIssueService duplicateIssueService;

    @Value("${civentre.upload-dir}")
    private String uploadDir;

    public IssueController(
            IssueService issueService,
            UserRepository userRepository,
            IssueUpdateRepository issueUpdateRepository,
            IssueVoteService issueVoteService,
            DuplicateIssueService duplicateIssueService) {

        this.issueService = issueService;
        this.userRepository = userRepository;
        this.issueUpdateRepository = issueUpdateRepository;
        this.issueVoteService = issueVoteService;
        this.duplicateIssueService = duplicateIssueService;
    }

    // ==========================================
    // REPORT ISSUE
    // ==========================================

    @PostMapping
    public Issue reportIssue(

            @RequestParam
            @NotBlank(message = "Issue title is required")
            @Size(
                    min = 5,
                    max = 150,
                    message = "Issue title must be between 5 and 150 characters"
            )
            String title,

            @RequestParam
            @NotBlank(message = "Description is required")
            @Size(
                    min = 10,
                    max = 1000,
                    message = "Description must be between 10 and 1000 characters"
            )
            String description,

            @RequestParam
            @NotBlank(message = "Category is required")
            String category,

            @RequestParam
            @NotBlank(message = "Location is required")
            String location,
            @RequestParam(required = false)
            Double latitude,
            @RequestParam(required = false)
            Double longitude,
            @RequestParam(required = false)
            MultipartFile image,
            Authentication authentication) throws IOException {
        User user = getAuthenticatedUser(authentication);
        // ==========================================
        // ROLE CHECK
        // ==========================================

        if (user.getRole() != User.Role.CITIZEN) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only citizens can report issues"
            );
        }

        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setCategory(category);
        issue.setLocation(location);
        issue.setLatitude(latitude);
        issue.setLongitude(longitude);

        // ==========================================
        // IMAGE UPLOAD VALIDATION
        // ==========================================

        if (image != null && !image.isEmpty()) {

            // Check file size
            if (image.getSize() > 10 * 1024 * 1024) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Image size must not exceed 10 MB"
                );
            }

            // Check MIME type
            String contentType = image.getContentType();

            if (contentType == null ||
                    !contentType.startsWith("image/")) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only image files are allowed"
                );
            }

            // Check original filename
            String originalName =
                    image.getOriginalFilename();

            if (originalName == null ||
                    !originalName.contains(".")) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid image file"
                );
            }

            // Get extension
            String extension =
                    originalName.substring(
                            originalName.lastIndexOf(".")
                    ).toLowerCase();

            // Allow only safe image extensions
            if (!extension.equals(".jpg")
                    && !extension.equals(".jpeg")
                    && !extension.equals(".png")
                    && !extension.equals(".webp")) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only JPG, JPEG, PNG, and WEBP images are allowed"
                );
            }

            // ==========================================
            // VERIFY ACTUAL IMAGE SIGNATURE
            // ==========================================

            if (!isValidImageSignature(image)) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The uploaded file is not a valid JPG, PNG, or WEBP image"
                );
            }

            // ==========================================
            // SAFE RANDOM FILE NAME
            // ==========================================

            String fileName =
                    UUID.randomUUID() + extension;

            Path uploadPath =
                    Paths.get(uploadDir).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);

            Path filePath =
                    uploadPath.resolve(fileName).normalize();

            /*
             * Additional path-safety check.
             *
             * The filename is generated by UUID, but this
             * check ensures the final path still remains
             * inside the upload directory.
             */

            if (!filePath.getParent().equals(uploadPath)) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid upload path"
                );
            }

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            issue.setImagePath(fileName);
        }

        return issueService.reportIssue(issue, user);
    }

    // ==========================================
    // DUPLICATE ISSUE CHECK
    // ==========================================

    @GetMapping("/duplicates")
    public List<DuplicateIssueDTO> findPossibleDuplicates(

            @RequestParam String title,

            @RequestParam String category,

            @RequestParam String location,

            @RequestParam(required = false)
            Double latitude,

            @RequestParam(required = false)
            Double longitude) {

        Issue newIssue = new Issue();

        newIssue.setTitle(title);
        newIssue.setCategory(category);
        newIssue.setLocation(location);
        newIssue.setLatitude(latitude);
        newIssue.setLongitude(longitude);

        List<Issue> duplicates =
                duplicateIssueService
                        .findPossibleDuplicates(newIssue);

        return duplicates.stream()
                .map(issue -> new DuplicateIssueDTO(
                        issue.getId(),
                        issue.getTitle(),
                        issue.getCategory(),
                        issue.getLocation(),
                        issue.getImagePath(),
                        issue.getStatus(),
                        issue.getPriority(),
                        issue.getCreatedAt(),
                        issueVoteService.getSupportCount(issue)
                ))
                .toList();
    }

    // ==========================================
    // PUBLIC ISSUES
    // ==========================================

    @GetMapping("/public")
    public List<PublicIssueDTO> getPublicIssues() {

        List<Issue> issues =
                issueService.getAllIssues();

        return issues.stream()
                .map(issue -> new PublicIssueDTO(
                        issue.getId(),
                        issue.getTitle(),
                        issue.getDescription(),
                        issue.getCategory(),
                        issue.getLocation(),
                        issue.getLatitude(),
                        issue.getLongitude(),
                        issue.getImagePath(),
                        issue.getResolutionImagePath(),
                        issue.getStatus(),
                        issue.getPriority(),
                        issue.getCreatedAt(),
                        issueVoteService.getSupportCount(issue)
                ))
                .toList();
    }

    // ==========================================
    // MY ISSUES
    // =========================================

    @GetMapping("/my")
    public List<IssueResponseDTO> getMyIssues(
            Authentication authentication) {

        User user =
                getAuthenticatedUser(authentication);

        List<Issue> issues =
                issueService.getIssuesByUser(user);

        for (Issue issue : issues) {

            issue.setSupportCount(
                    issueVoteService.getSupportCount(issue)
            );
        }

        return issues.stream()
                .map(issueService::toIssueResponseDTO)
                .toList();
    }


    // ==========================================
    // MY ISSUE STATISTICS
    // ==========================================

    @GetMapping("/stats")
    public Map<String, Long> getMyIssueStats(
            Authentication authentication) {

        User user =
                getAuthenticatedUser(authentication);

        Map<String, Long> stats =
                new HashMap<>();

        stats.put(
                "total",
                issueService.getTotalIssues(user)
        );

        stats.put(
                "inProgress",
                issueService.getInProgressIssues(user)
        );

        stats.put(
                "resolved",
                issueService.getResolvedIssues(user)
        );

        return stats;
    }

    // ==========================================
    // SINGLE PUBLIC ISSUE
    // ==========================================

    @GetMapping("/{issueId}")
    public PublicIssueDTO getIssueById(
            @PathVariable Long issueId) {

        Issue issue =
                issueService.getIssueById(issueId);

        return new PublicIssueDTO(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getCategory(),
                issue.getLocation(),
                issue.getLatitude(),
                issue.getLongitude(),
                issue.getImagePath(),
                issue.getResolutionImagePath(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getCreatedAt(),
                issueVoteService.getSupportCount(issue)
        );
    }

    // ==========================================
// ISSUE HISTORY
// ==========================================

    @GetMapping("/{issueId}/history")
    public List<IssueUpdateResponseDTO> getIssueHistory(
            @PathVariable Long issueId) {

        Issue issue =
                issueService.getIssueById(issueId);

        return issueUpdateRepository
                .findByIssueOrderByUpdatedAtAsc(issue)
                .stream()
                .map(update -> new IssueUpdateResponseDTO(
                        update.getId(),
                        update.getStatus() != null
                                ? update.getStatus().name()
                                : null,
                        update.getRemarks(),
                        update.getUpdatedAt()
                ))
                .toList();
    }


    // ==========================================
    // CONFIRM RESOLUTION
    // ==========================================

    @PutMapping("/{issueId}/confirm")
    public Issue confirmResolution(
            @PathVariable Long issueId,
            Authentication authentication) {

        User citizen =
                getAuthenticatedUser(authentication);

        if (citizen.getRole() != User.Role.CITIZEN) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only citizens can confirm issue resolution"
            );
        }

        return issueService.confirmResolution(
                issueId,
                citizen
        );
    }

    // ==========================================
    // SUPPORT ISSUE
    // ==========================================

    @PutMapping("/{issueId}/support")
    public Map<String, Object> supportIssue(
            @PathVariable Long issueId,
            Authentication authentication) {

        User user =
                getAuthenticatedUser(authentication);

        Issue issue =
                issueService.getIssueById(issueId);

        boolean supported =
                issueVoteService.supportIssue(
                        issue,
                        user
                );

        long supportCount =
                issueVoteService.getSupportCount(issue);

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "supported",
                supported
        );

        response.put(
                "supportCount",
                supportCount
        );

        return response;
    }

    // ==========================================
    // GET AUTHENTICATED USER
    // ==========================================

    private User getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null ||
                authentication.getName() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user was not found"
                        )
                );
    }

    // ==========================================
    // IMAGE SIGNATURE VALIDATION
    // ==========================================

    private boolean isValidImageSignature(
            MultipartFile image) throws IOException {

        try (InputStream inputStream =
                     image.getInputStream()) {

            byte[] header = new byte[12];

            int bytesRead =
                    inputStream.read(header);

            if (bytesRead < 12) {
                return false;
            }

            // ==========================================
            // JPEG: FF D8 FF
            // ==========================================

            boolean jpeg =
                    (header[0] & 0xFF) == 0xFF &&
                            (header[1] & 0xFF) == 0xD8 &&
                            (header[2] & 0xFF) == 0xFF;

            // ==========================================
            // PNG: 89 50 4E 47 0D 0A 1A 0A
            // ==========================================

            boolean png =
                    (header[0] & 0xFF) == 0x89 &&
                            (header[1] & 0xFF) == 0x50 &&
                            (header[2] & 0xFF) == 0x4E &&
                            (header[3] & 0xFF) == 0x47 &&
                            (header[4] & 0xFF) == 0x0D &&
                            (header[5] & 0xFF) == 0x0A &&
                            (header[6] & 0xFF) == 0x1A &&
                            (header[7] & 0xFF) == 0x0A;

            // ==========================================
            // WEBP: RIFF....WEBP
            // ==========================================

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
}