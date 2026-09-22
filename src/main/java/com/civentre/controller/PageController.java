package com.civentre.controller;

import com.civentre.entity.User;
import com.civentre.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    private final UserService userService;
    public PageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage() {
        return "dashboard";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);

        userService.registerUser(user);

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/report-issue")
    public String reportIssuePage() {
        return "report-issue";
    }

    @GetMapping("/my-issues")
    public String myIssuesPage() {
        return "my-issues";
    }

    @GetMapping("/public-issues")
    public String publicIssues() {
        return "public-issues";
    }

    @GetMapping("/issue-details/{issueId}")
    public String publicIssueDetails(@PathVariable Long issueId) {
        return "public-issue-details";
    }
}