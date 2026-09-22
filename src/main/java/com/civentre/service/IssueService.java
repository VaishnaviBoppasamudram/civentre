package com.civentre.service;

import com.civentre.entity.Department;
import com.civentre.entity.Issue;
import com.civentre.entity.IssueUpdate;
import com.civentre.entity.Status;
import com.civentre.entity.User;
import com.civentre.repository.IssueRepository;
import com.civentre.repository.IssueUpdateRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.civentre.dto.IssueResponseDTO;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueUpdateRepository issueUpdateRepository;
    private final NotificationService notificationService;
    private final PriorityService priorityService;

    public IssueService(
            IssueRepository issueRepository,
            IssueUpdateRepository issueUpdateRepository,
            NotificationService notificationService,
            PriorityService priorityService) {

        this.issueRepository = issueRepository;
        this.issueUpdateRepository = issueUpdateRepository;
        this.notificationService = notificationService;
        this.priorityService = priorityService;
    }

    public Issue reportIssue(Issue issue, User user) {

        issue.setUser(user);

        String calculatedPriority =
                priorityService.calculatePriority(issue);

        issue.setPriority(calculatedPriority);

        Issue savedIssue =
                issueRepository.save(issue);

        IssueUpdate update = new IssueUpdate(
                savedIssue,
                savedIssue.getStatus(),
                "Issue reported by citizen"
        );

        issueUpdateRepository.save(update);

        return savedIssue;
    }

    //==========================
    // PUBLIC ISSUES
    // =========================

    public List<Issue> getAllIssues() {

        return issueRepository.findAll();
    }

    // =========================
    // CITIZEN METHODS
    // =========================

    public List<Issue> getIssuesByUser(User user) {

        return issueRepository.findByUser(user);
    }

    public long getTotalIssues(User user) {

        return issueRepository.countByUser(user);
    }

    public long getInProgressIssues(User user) {

        return issueRepository.countByUserAndStatus(
                user,
                Status.IN_PROGRESS
        );
    }

    public long getResolvedIssues(User user) {

        return issueRepository.countByUserAndStatus(
                user,
                Status.RESOLVED
        );
    }

    // =========================
    // GENERAL ISSUE METHODS
    // =========================

    public Issue getIssueById(Long issueId) {

        return issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));
    }

    public Issue updateIssueStatus(
            Long issueId,
            Status newStatus,
            String remarks) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        issue.setStatus(newStatus);

        Issue savedIssue =
                issueRepository.save(issue);

        IssueUpdate update =
                new IssueUpdate(
                        savedIssue,
                        newStatus,
                        remarks
                );

        issueUpdateRepository.save(update);

        // Create notification for the citizen
        notificationService.createNotification(
                savedIssue.getUser(),
                savedIssue,
                "Your issue #" + savedIssue.getId()
                        + " status has been updated to "
                        + newStatus,
                "STATUS_UPDATE"
        );

        return savedIssue;
    }

    // =========================
    // ADMIN METHODS
    // =========================

    public Issue assignDepartment(Long issueId, Department department) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException("Issue not found"));

        // Clear the current officer if they do not belong
        // to the newly selected department.
        if (issue.getOfficer() != null) {

            User currentOfficer = issue.getOfficer();

            if (currentOfficer.getDepartment() == null ||
                    !currentOfficer.getDepartment()
                            .getId()
                            .equals(department.getId())) {

                issue.setOfficer(null);
            }
        }

        issue.setDepartment(department);
        issue.setStatus(Status.ASSIGNED);

        Issue savedIssue =
                issueRepository.save(issue);

        IssueUpdate update =
                new IssueUpdate(
                        savedIssue,
                        Status.ASSIGNED,
                        "Issue assigned to "
                                + department.getName()
                                + " department"
                );

        issueUpdateRepository.save(update);

        notificationService.createNotification(
                savedIssue.getUser(),
                savedIssue,
                "Your issue #"
                        + savedIssue.getId()
                        + " has been assigned to "
                        + department.getName()
                        + " department.",
                "DEPARTMENT_ASSIGNED"
        );

        return savedIssue;
    }

    public Issue assignOfficer(
            Long issueId,
            User officer) {

        Issue issue =
                issueRepository.findById(issueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Issue not found"));

        issue.setOfficer(officer);

        Issue savedIssue =
                issueRepository.save(issue);

        IssueUpdate update =
                new IssueUpdate(
                        savedIssue,
                        savedIssue.getStatus(),
                        "Issue assigned to field officer "
                                + officer.getName()
                );

        issueUpdateRepository.save(update);

        notificationService.createNotification(
                savedIssue.getUser(),
                savedIssue,
                "A field officer, "
                        + officer.getName()
                        + ", has been assigned to your issue #"
                        + savedIssue.getId()
                        + ".",
                "OFFICER_ASSIGNED"
        );

        return savedIssue;
    }

    // =========================
    // OFFICER METHODS
    // =========================

    public List<Issue> getIssuesByOfficer(User officer) {

        return issueRepository.findByOfficer(officer);
    }

    public long getTotalIssuesByOfficer(User officer) {

        return issueRepository.countByOfficer(officer);
    }

    public long getPendingIssuesByOfficer(User officer) {

        return issueRepository.countByOfficerAndStatus(
                officer,
                Status.ASSIGNED
        );
    }

    public long getInProgressIssuesByOfficer(User officer) {

        return issueRepository.countByOfficerAndStatus(
                officer,
                Status.IN_PROGRESS
        );
    }

    public long getResolvedIssuesByOfficer(User officer) {

        return issueRepository.countByOfficerAndStatus(
                officer,
                Status.RESOLVED
        );
    }

    public Issue updateOfficerIssueStatus(
            Long issueId, Status newStatus, String remarks, User officer) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if (issue.getOfficer() == null ||
                !issue.getOfficer().getId().equals(officer.getId())) {
            throw new RuntimeException("You are not assigned to this issue");
        }

        if (newStatus != Status.IN_PROGRESS &&
                newStatus != Status.RESOLVED) {
            throw new RuntimeException("Invalid status for officer");
        }

        // Prevent invalid workflow transitions
        if (newStatus == Status.IN_PROGRESS &&
                issue.getStatus() != Status.ASSIGNED &&
                issue.getStatus() != Status.IN_PROGRESS) {

            throw new RuntimeException(
                    "Issue must be assigned before it can be marked as in progress"
            );
        }

        if (newStatus == Status.RESOLVED &&
                issue.getStatus() != Status.IN_PROGRESS) {

            throw new RuntimeException(
                    "Issue must be in progress before it can be marked as resolved"
            );
        }

        // Resolution photo is mandatory
        if (newStatus == Status.RESOLVED &&
                (issue.getResolutionImagePath() == null ||
                        issue.getResolutionImagePath().isBlank())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please upload a resolution photo before marking the issue as resolved"
            );
        }

        issue.setStatus(newStatus);

        Issue savedIssue = issueRepository.save(issue);

        IssueUpdate update = new IssueUpdate(
                savedIssue,
                newStatus,
                remarks
        );

        issueUpdateRepository.save(update);

        notificationService.createNotification(
                savedIssue.getUser(),
                savedIssue,
                "Your issue #" + savedIssue.getId()
                        + " status has been updated to " + newStatus,
                "STATUS_UPDATE"
        );

        return savedIssue;
    }

    // =========================
// SAVE ISSUE
// =========================

    public Issue saveIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    public Issue confirmResolution(Long issueId, User citizen) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        // Make sure this issue belongs to the logged-in citizen
        if (issue.getUser() == null ||
                !issue.getUser().getId().equals(citizen.getId())) {

            throw new RuntimeException(
                    "You are not allowed to confirm this issue");
        }

        // Citizen can confirm only a resolved issue
        if (issue.getStatus() != Status.RESOLVED) {
            throw new RuntimeException(
                    "Only resolved issues can be confirmed");
        }

        // Prevent confirming twice
        if (issue.isCitizenConfirmed()) {
            throw new RuntimeException(
                    "Resolution is already confirmed");
        }

        issue.setCitizenConfirmed(true);
        issue.setConfirmedAt(java.time.LocalDateTime.now());
        issue.setStatus(Status.CLOSED);

        Issue savedIssue = issueRepository.save(issue);

        IssueUpdate update = new IssueUpdate(
                savedIssue,
                Status.CLOSED,
                "Citizen confirmed that the issue has been resolved"
        );

        issueUpdateRepository.save(update);

        notificationService.createNotification(
                savedIssue.getUser(),
                savedIssue,
                "Your issue #" + savedIssue.getId()
                        + " has been successfully closed.",
                "ISSUE_CLOSED"
        );

        return savedIssue;
    }

    // ADMIN STATISTICS

    public long getTotalIssues() {
        return issueRepository.count();
    }

    public long getReportedIssues() {
        return issueRepository.countByStatus(Status.REPORTED);
    }

    public long getInProgressIssues() {
        return issueRepository.countByStatus(Status.IN_PROGRESS);
    }

    public long getResolvedIssues() {
        return issueRepository.countByStatus(Status.RESOLVED);
    }

    public long getClosedIssues() {
        return issueRepository.countByStatus(Status.CLOSED);
    }

    public long getRejectedIssues() {
        return issueRepository.countByStatus(Status.REJECTED);
    }

    public IssueResponseDTO toIssueResponseDTO(Issue issue) {

        return new IssueResponseDTO(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getCategory(),
                issue.getLocation(),
                issue.getLatitude(),
                issue.getLongitude(),
                issue.getImagePath(),
                issue.getResolutionImagePath(),
                issue.getStatus() != null ? issue.getStatus().name() : null,
                issue.getPriority(),
                issue.getSupportCount(),

                issue.getDepartment() != null
                        ? issue.getDepartment().getId()
                        : null,

                issue.getDepartment() != null
                        ? issue.getDepartment().getName()
                        : null,

                issue.getOfficer() != null
                        ? issue.getOfficer().getId()
                        : null,

                issue.getOfficer() != null
                        ? issue.getOfficer().getName()
                        : null,

                issue.getCreatedAt()
        );
    }
}