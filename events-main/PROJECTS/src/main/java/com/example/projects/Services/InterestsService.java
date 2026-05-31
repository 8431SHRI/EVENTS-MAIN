package com.example.projects.Services;

import com.example.projects.Repositories.InterestsRepo;
import com.example.projects.entities.Interests;
import com.example.projects.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestsService {

    private final InterestsRepo repo;

    public InterestsService(InterestsRepo repo) {
        this.repo = repo;
    }

    public Interests addInterest(Interests interest) {
        return repo.save(interest);
    }

    public List<Interests> getAll() {
        return repo.findAll();
    }

    public Interests getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interest not found with id: " + id));
    }
}