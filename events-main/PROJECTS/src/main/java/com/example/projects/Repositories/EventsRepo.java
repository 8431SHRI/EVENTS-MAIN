package com.example.projects.Repositories;

import com.example.projects.entities.Events;
import com.example.projects.entities.Interests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface EventsRepo extends JpaRepository<Events, Long> {

    // המתודה שהייתה חסרה ומחפשת אירועים אליהם המשתמש רשום
    List<Events> findByRegistrationsUserId(Long userId);

    // מתודות השאילתה הנוספות ששאר ה-Service משתמש בהן
    List<Events> findByInterestIn(Collection<Interests> interest);

    List<Events> findByInterestId(Long interestId);

    List<Events> findByDate(LocalDate date);

    List<Events> findByCreatorId(Long creatorId);
}