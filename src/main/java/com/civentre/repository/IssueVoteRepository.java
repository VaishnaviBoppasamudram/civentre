package com.civentre.repository;

import com.civentre.entity.Issue;
import com.civentre.entity.IssueVote;
import com.civentre.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueVoteRepository
        extends JpaRepository<IssueVote, Long> {

    boolean existsByIssueAndUser(Issue issue, User user);

    long countByIssue(Issue issue);

    void deleteByIssueAndUser(Issue issue, User user);
}