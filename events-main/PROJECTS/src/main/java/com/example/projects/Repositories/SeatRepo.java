package com.example.projects.Repositories;

import com.example.projects.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepo extends JpaRepository<Seat,Long> {

    List<Seat> findByEventId(Long eventId);
    List<Seat> findByEventIdAndUserId(Long eventId, Long userId);
    Optional<Seat> findByEventIdAndBlockNameAndRowNumberAndSeatNumber(Long eventId, String blockName, int rowNumber, int seatNumber);
}
