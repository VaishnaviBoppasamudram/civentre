package com.civentre.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "issue_votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"issue_id", "user_id"}
                )
        }
)
public class IssueVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public IssueVote() {
    }

    public IssueVote(Issue issue, User user) {
        this.issue = issue;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}