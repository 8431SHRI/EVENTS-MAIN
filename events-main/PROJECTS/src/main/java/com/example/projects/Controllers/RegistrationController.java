package com.example.projects.Controllers;

import com.example.projects.Dtos.RegistrationDTO;
import com.example.projects.Services.RegistrationService;
import com.example.projects.entities.Registration;
import com.example.projects.entities.Events;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:5173")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // 1. נקודת הרישום המעודכנת והנקייה (מקבלת רק יוזר ואירוע)
    @PostMapping("/register")
    public ResponseEntity<Registration> register(@RequestParam Long userId, @RequestParam Long eventId) {
        return ResponseEntity.ok(registrationService.register(userId, eventId));
    }

    // 2. נקודת ביטול הרישום
    @DeleteMapping("/unregister")
    public ResponseEntity<String> unregister(@RequestParam Long userId, @RequestParam Long eventId) {
        registrationService.unregister(userId, eventId);
        return ResponseEntity.ok("Unregistered successfully");
    }

    // 3. נקודת הקצה החדשה - אישור ההצעה מרשימת ההמתנה (בעקבות המייל)
    @PostMapping("/confirm-offer")
    public ResponseEntity<Registration> confirmOffer(@RequestParam Long userId, @RequestParam Long eventId) {
        return ResponseEntity.ok(registrationService.confirmOffer(userId, eventId));
    }

    // 4. שליפת כל הרישומים של משתמש מסוים כ-DTO
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<com.example.projects.Dtos.RegistrationDTO>> getRegistrationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(userId));
    }
    // 5. שליפת כל האירועים של משתמש מסוים
    @GetMapping("/my-events/{userId}")
    public ResponseEntity<List<Events>> getMyEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getMyEvents(userId));
    }

    // 6. שליפת ה-DTOs של הרישומים לצד לקוח
    @GetMapping("/user-dto/{userId}")
    public ResponseEntity<List<RegistrationDTO>> getUserRegistrations(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(userId));
    }
}