package com.civentre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OfficerPageController {

    @GetMapping("/officer")
    public String officerPage() {
        return "officer";
    }
}