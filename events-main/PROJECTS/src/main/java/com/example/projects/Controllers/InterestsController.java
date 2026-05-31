package com.example.projects.Controllers;

import com.example.projects.Services.InterestsService;
import com.example.projects.entities.Interests;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
public class InterestsController {

    private final InterestsService service;

    public InterestsController(InterestsService service) {
        this.service = service;
    }

    @PostMapping
    public Interests add(@Valid @RequestBody Interests interest) {
        return service.addInterest(interest);
    }

    @GetMapping()
    public List<Interests> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Interests getById(@PathVariable Long id) {
        return service.getById(id);
    }
}