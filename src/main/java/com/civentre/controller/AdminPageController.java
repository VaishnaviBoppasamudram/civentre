package com.civentre.controller;

import com.civentre.entity.Issue;
import com.civentre.entity.IssueUpdate;
import com.civentre.repository.IssueUpdateRepository;
import com.civentre.service.IssueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminPageController {

    private final IssueService issueService;
    private final IssueUpdateRepository issueUpdateRepository;

    public AdminPageController(
            IssueService issueService,
            IssueUpdateRepository issueUpdateRepository) {

        this.issueService = issueService;
        this.issueUpdateRepository = issueUpdateRepository;
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/admin/issues/{issueId}")
    public String issueDetails(
            @PathVariable Long issueId,
            Model model) {

        Issue issue = issueService.getIssueById(issueId);

        List<IssueUpdate> history =
                issueUpdateRepository
                        .findByIssueOrderByUpdatedAtAsc(issue);

        model.addAttribute("issue", issue);
        model.addAttribute("history", history);

        return "admin-issue-details";
    }
}