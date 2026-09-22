package com.civentre.service;

import com.civentre.entity.Department;
import com.civentre.entity.Issue;
import com.civentre.entity.Status;
import com.civentre.repository.DepartmentRepository;
import com.civentre.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final IssueRepository issueRepository;
    private final DepartmentRepository departmentRepository;

    public AnalyticsService(
            IssueRepository issueRepository,
            DepartmentRepository departmentRepository) {

        this.issueRepository = issueRepository;
        this.departmentRepository = departmentRepository;
    }

    public Map<String, Long> getIssuesByCategory() {

        List<Issue> issues =
                issueRepository.findAll();

        Map<String, Long> result =
                new LinkedHashMap<>();

        for (Issue issue : issues) {

            String category =
                    issue.getCategory();

            if (category == null ||
                    category.isBlank()) {

                category = "Other";
            }

            result.put(
                    category,
                    result.getOrDefault(category, 0L) + 1
            );
        }

        return result;
    }

    public Map<String, Long> getIssuesByPriority() {

        List<Issue> issues =
                issueRepository.findAll();

        Map<String, Long> result =
                new LinkedHashMap<>();

        result.put("HIGH", 0L);
        result.put("MEDIUM", 0L);
        result.put("LOW", 0L);

        for (Issue issue : issues) {

            String priority =
                    issue.getPriority();

            if (priority != null &&
                    result.containsKey(priority)) {

                result.put(
                        priority,
                        result.get(priority) + 1
                );
            }
        }

        return result;
    }

    public Map<String, Long> getIssuesByStatus() {

        Map<String, Long> result =
                new LinkedHashMap<>();

        for (Status status : Status.values()) {

            result.put(
                    status.name(),
                    issueRepository.countByStatus(status)
            );
        }

        return result;
    }

    public Map<String, Long> getIssuesByDepartment() {

        List<Department> departments =
                departmentRepository.findAll();

        Map<String, Long> result =
                new LinkedHashMap<>();

        for (Department department : departments) {

            result.put(
                    department.getName(),
                    issueRepository.countByDepartment(department)
            );
        }

        return result;
    }
}