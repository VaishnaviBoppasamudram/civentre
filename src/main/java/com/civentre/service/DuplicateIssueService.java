package com.civentre.service;

import com.civentre.entity.Issue;
import com.civentre.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DuplicateIssueService {

    private final IssueRepository issueRepository;

    public DuplicateIssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<Issue> findPossibleDuplicates(Issue newIssue) {

        List<Issue> allIssues =
                issueRepository.findAll();

        return allIssues.stream()
                .filter(issue -> {

                    if (newIssue.getId() != null &&
                            issue.getId().equals(newIssue.getId())) {
                        return false;
                    }

                    boolean nearby =
                            isNearby(
                                    issue.getLatitude(),
                                    issue.getLongitude(),
                                    newIssue.getLatitude(),
                                    newIssue.getLongitude()
                            );

                    boolean similarTitle =
                            isSimilarTitle(
                                    issue.getTitle(),
                                    newIssue.getTitle()
                            );

                    return nearby && similarTitle;
                })
                .toList();
    }

    private int calculateDuplicateScore(
            Issue existingIssue,
            Issue newIssue) {

        int score = 0;

        if (sameCategory(existingIssue, newIssue)) {
            score += 1;
        }

        if (isNearby(
                existingIssue.getLatitude(),
                existingIssue.getLongitude(),
                newIssue.getLatitude(),
                newIssue.getLongitude())) {

            score += 2;
        }

        if (isSimilarTitle(
                existingIssue.getTitle(),
                newIssue.getTitle())) {

            score += 2;
        }

        if (isSimilarDescription(
                existingIssue.getDescription(),
                newIssue.getDescription())) {

            score += 1;
        }

        return score;
    }

    private boolean sameCategory(
            Issue existingIssue,
            Issue newIssue) {

        return existingIssue.getCategory() != null &&
                newIssue.getCategory() != null &&
                existingIssue.getCategory()
                        .equalsIgnoreCase(
                                newIssue.getCategory()
                        );
    }

    private boolean isNearby(
            Double latitude1,
            Double longitude1,
            Double latitude2,
            Double longitude2) {

        if (latitude1 == null ||
                longitude1 == null ||
                latitude2 == null ||
                longitude2 == null) {

            return false;
        }

        double earthRadius = 6371000;

        double latDistance =
                Math.toRadians(latitude2 - latitude1);

        double lonDistance =
                Math.toRadians(longitude2 - longitude1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        +
                        Math.cos(Math.toRadians(latitude1))
                                * Math.cos(Math.toRadians(latitude2))
                                * Math.sin(lonDistance / 2)
                                * Math.sin(lonDistance / 2);

        double distance =
                2 * earthRadius
                        * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return distance <= 100;
    }

    private boolean isSimilarTitle(
            String title1,
            String title2) {

        if (title1 == null || title2 == null) {
            return false;
        }

        String[] words1 =
                title1.toLowerCase()
                        .replaceAll("[^a-z0-9 ]", "")
                        .split("\\s+");

        String[] words2 =
                title2.toLowerCase()
                        .replaceAll("[^a-z0-9 ]", "")
                        .split("\\s+");

        int matchingWords = 0;

        for (String word1 : words1) {

            if (word1.length() < 3) {
                continue;
            }

            for (String word2 : words2) {

                if (word1.equals(word2)) {
                    matchingWords++;
                    break;
                }
            }
        }

        return matchingWords >= 2;
    }

    private boolean isSimilarDescription(
            String description1,
            String description2) {

        if (description1 == null ||
                description2 == null) {

            return false;
        }

        String first =
                description1.toLowerCase().trim();

        String second =
                description2.toLowerCase().trim();

        return first.contains(second)
                || second.contains(first);
    }
}