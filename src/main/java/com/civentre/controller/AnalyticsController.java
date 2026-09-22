package com.civentre.controller;

import com.civentre.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    // ==========================================
    // ISSUES BY CATEGORY
    // ==========================================

    @GetMapping("/category")
    public Map<String, Long> getIssuesByCategory() {

        return analyticsService.getIssuesByCategory();
    }

    // ==========================================
    // ISSUES BY PRIORITY
    // ==========================================

    @GetMapping("/priority")
    public Map<String, Long> getIssuesByPriority() {

        return analyticsService.getIssuesByPriority();
    }

    // ==========================================
    // ISSUES BY STATUS
    // ==========================================

    @GetMapping("/status")
    public Map<String, Long> getIssuesByStatus() {

        return analyticsService.getIssuesByStatus();
    }

    // ==========================================
    // ISSUES BY DEPARTMENT
    // ==========================================

    @GetMapping("/department")
    public Map<String, Long> getIssuesByDepartment() {

        return analyticsService.getIssuesByDepartment();
    }
}