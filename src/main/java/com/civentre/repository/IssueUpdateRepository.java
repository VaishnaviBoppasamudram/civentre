package com.civentre.repository;

import com.civentre.entity.Issue;
import com.civentre.entity.IssueUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueUpdateRepository extends JpaRepository<IssueUpdate, Long> {

    List<IssueUpdate> findByIssueOrderByUpdatedAtAsc(Issue issue);
}