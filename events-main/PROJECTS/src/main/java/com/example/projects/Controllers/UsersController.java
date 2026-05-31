package com.example.projects.Controllers;

import com.example.projects.Services.UsersService;
import com.example.projects.entities.Users;
import com.example.projects.Dtos.UserDTO; // יבוא של ה-DTO שלך
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 1. הכתובת הכללית - עכשיו מחזירה רשימה נקייה של DTOs ללא סיסמה!
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<Users> users = usersService.getAll();

        // הפיכת כל יוזר מה-Database ל-UserDTO באמצעות ה-Service שלך
        List<UserDTO> userDtos = users.stream()
                .map(usersService::convertToDTO)
                .toList();

        return ResponseEntity.ok(userDtos);
    }

    @PostMapping
    public ResponseEntity<Users> addUser(@Valid @RequestBody Users user) {
        return ResponseEntity.ok(usersService.addUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Users user = usersService.getUserById(id);
        return ResponseEntity.ok(usersService.convertToDTO(user));
    }

    @GetMapping("/by-email")
    public ResponseEntity<Users> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(usersService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @Valid @RequestBody Users user) {
        return ResponseEntity.ok(usersService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}