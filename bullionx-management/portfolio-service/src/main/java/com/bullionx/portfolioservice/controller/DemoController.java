package com.bullionx.portfolioservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @PostMapping("/send-notifications")
    public String sendNotifications() {
        try {
            System.out.println("Starting notification pipeline...");

            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "../etl-pipeline/notification_pipeline.py"
            );
            pb.inheritIO(); // This shows Python output in Spring Boot console

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "Notifications sent successfully! Check your email.";
            } else {
                return " Failed to send notifications. Check logs.";
            }
        } catch (Exception e) {
            return " Error: " + e.getMessage();
        }
    }
}