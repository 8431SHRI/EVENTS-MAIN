package com.example.projects.Repositories;

import com.example.projects.entities.Interests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestsRepo extends JpaRepository<Interests, Long> {
}