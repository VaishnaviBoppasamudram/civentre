package com.civentre.service;

import com.civentre.entity.Issue;
import com.civentre.entity.IssueVote;
import com.civentre.entity.User;
import com.civentre.repository.IssueVoteRepository;
import org.springframework.stereotype.Service;

@Service
public class IssueVoteService {

    private final IssueVoteRepository issueVoteRepository;

    public IssueVoteService(
            IssueVoteRepository issueVoteRepository) {
        this.issueVoteRepository = issueVoteRepository;
    }

    public boolean supportIssue(Issue issue, User user) {

        if (issueVoteRepository.existsByIssueAndUser(issue, user)) {
            return false;
        }

        IssueVote vote =
                new IssueVote(issue, user);

        issueVoteRepository.save(vote);

        return true;
    }

    public boolean removeSupport(Issue issue, User user) {

        if (!issueVoteRepository.existsByIssueAndUser(issue, user)) {
            return false;
        }

        issueVoteRepository.deleteByIssueAndUser(
                issue,
                user
        );

        return true;
    }

    public long getSupportCount(Issue issue) {

        return issueVoteRepository.countByIssue(issue);
    }

    public boolean hasSupported(
            Issue issue,
            User user) {

        return issueVoteRepository.existsByIssueAndUser(
                issue,
                user
        );
    }
}