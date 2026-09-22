package com.civentre.repository;

import com.civentre.entity.Issue;
import com.civentre.entity.Status;
import com.civentre.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.civentre.entity.Department;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByUser(User user);

    long countByUser(User user);

    long countByUserAndStatus(User user, Status status);

    List<Issue> findByOfficer(User officer);

    long countByOfficer(User officer);

    long countByOfficerAndStatus(User officer, Status status);

    long countByStatus(Status status);

    long countByDepartment(Department department);

}