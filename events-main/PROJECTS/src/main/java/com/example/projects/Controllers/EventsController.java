package com.example.projects.Controllers;

import com.example.projects.Dtos.SeatDTO;
import com.example.projects.Services.EventsService;
import com.example.projects.entities.Events;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventsController {

    private final EventsService service;

    public EventsController(EventsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Events> add(@Valid @RequestBody Events event) {
        return ResponseEntity.ok(service.addEvent(event));
    }

    @GetMapping
    public List<Events> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Events> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Events> update(@PathVariable Long id, @Valid @RequestBody Events event) {
        return ResponseEntity.ok(service.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Events>> getEventsForUser(@PathVariable Long userId) {
        List<Events> events = service.getEventsForUser(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<List<Events>> getSuggestions(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getSuggestions(userId));
    }

    @GetMapping("/interest/{interestId}")
    public ResponseEntity<List<Events>> getByInterest(@PathVariable Long interestId) {
        return ResponseEntity.ok(service.getByInterest(interestId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Events>> getByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getByDate(date));
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<Events>> getByCreator(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByCreator(userId));
    }

    @GetMapping("/{eventId}/seats")
    public ResponseEntity<List<SeatDTO>> getEventSeats(@PathVariable Long eventId) {
        List<SeatDTO> seatDtos = service.getSeatsForEvent(eventId);
        return ResponseEntity.ok(seatDtos);
    }

    @PostMapping("/{eventId}/book")
    public ResponseEntity<List<SeatDTO>> bookSeats(
            @PathVariable Long eventId,
            @RequestParam Long userId,
            @RequestBody List<Long> seatIds) {
        List<SeatDTO> updatedSeats = service.bookSeats(eventId, userId, seatIds);
        return ResponseEntity.ok(updatedSeats);
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long eventId,
            @RequestParam Long userId) {
        service.cancelBooking(eventId, userId);
        return ResponseEntity.ok("Booking canceled, seats released, and queue promoted successfully.");
    }
}