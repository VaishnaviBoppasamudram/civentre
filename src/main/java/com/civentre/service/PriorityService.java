package com.civentre.service;

import com.civentre.entity.Issue;
import org.springframework.stereotype.Service;

@Service
public class PriorityService {

    public String calculatePriority(Issue issue) {

        String category =
                issue.getCategory() == null
                        ? ""
                        : issue.getCategory().toLowerCase();

        String title =
                issue.getTitle() == null
                        ? ""
                        : issue.getTitle().toLowerCase();

        String description =
                issue.getDescription() == null
                        ? ""
                        : issue.getDescription().toLowerCase();

        String issueText =
                title + " " + description;


        // ==============================
        // HIGH PRIORITY - Emergency Words
        // ==============================

        if (containsUrgentKeyword(issueText)) {
            return "HIGH";
        }


        // ==============================
        // HIGH PRIORITY - Critical Categories
        // ==============================

        switch (category) {

            case "traffic":
                return "HIGH";

            case "water supply":
                return "HIGH";

            case "roads & infrastructure":
                return "HIGH";

            default:
                break;
        }


        // ==============================
        // MEDIUM PRIORITY
        // ==============================

        switch (category) {

            case "streetlight":
                return "MEDIUM";

            case "sanitation":
                return "MEDIUM";

            default:
                break;
        }


        // ==============================
        // LOW PRIORITY
        // ==============================

        if (category.equals("parks & environment")) {
            return "LOW";
        }


        // ==============================
        // DEFAULT
        // ==============================

        return "MEDIUM";
    }


    private boolean containsUrgentKeyword(String text) {

        String[] urgentKeywords = {

                "emergency",
                "dangerous",
                "accident",
                "fire",
                "flood",
                "injury",
                "injured",
                "life threatening",
                "life-threatening",
                "electric shock",
                "collapsed",
                "major damage",
                "severe"
        };


        for (String keyword : urgentKeywords) {

            if (text.contains(keyword)) {
                return true;
            }
        }


        return false;
    }
}