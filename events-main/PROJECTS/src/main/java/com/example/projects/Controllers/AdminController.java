package com.example.projects.Controllers;

import com.example.projects.Dtos.AdminStatsDTO;
import com.example.projects.Services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }

    @DeleteMapping("/cancel-event/{eventId}")
    public ResponseEntity<String> cancelEvent(@PathVariable Long eventId) {
        adminService.cancelEvent(eventId);
        return ResponseEntity.ok("Event cancelled and users notified successfully.");
    }

    @PutMapping("/promote/{userId}")
    public ResponseEntity<String> promoteUser(@PathVariable Long userId) {
        adminService.promoteToAdmin(userId);
        return ResponseEntity.ok("User promoted to ADMIN successfully.");
    }
}