package com.example.projects.Services;

import com.example.projects.Dtos.SeatDTO;
import com.example.projects.Repositories.EventsRepo;
import com.example.projects.Repositories.SeatRepo;
import com.example.projects.Repositories.UsersRepo;
import com.example.projects.Repositories.RegistrationRepo;
import com.example.projects.entities.*;
import com.example.projects.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsService {

    private final EventsRepo repo;
    private final UsersRepo usersRepo;
    private final SeatRepo seatRepo;
    private final RegistrationRepo registrationRepo;
    private final RegistrationService registrationService; // הזרקת ה-RegistrationService לצורך קריאות ישירות

    public EventsService(EventsRepo repo, UsersRepo usersRepo, SeatRepo seatRepo,
                         RegistrationRepo registrationRepo, RegistrationService registrationService) {
        this.repo = repo;
        this.usersRepo = usersRepo;
        this.seatRepo = seatRepo;
        this.registrationRepo = registrationRepo;
        this.registrationService = registrationService;
    }

    @Transactional
    public Events addEvent(Events event) {
        if (event.getDate() != null) {
            event.setDayOfWeek(event.getDate().getDayOfWeek());
        }

        if (event.getImageUrl() == null || event.getImageUrl().isEmpty()) {
            event.setImageUrl("https://images.unsplash.com/photo-1511578314322-379afb476865?q=80&w=600");
        }

        Events savedEvent = repo.save(event);

        if (savedEvent.getLocation() == Events.LocationType.LECTURE_HALL) {
            generateComplexAuditorium(savedEvent);
        }

        return savedEvent;
    }

    public List<Events> getAll() {
        return repo.findAll();
    }

    public Events getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    public Events updateEvent(Long id, Events updatedEvent) {
        Events existing = getById(id);

        existing.setTitle(updatedEvent.getTitle());
        existing.setDescription(updatedEvent.getDescription());
        existing.setImageUrl(updatedEvent.getImageUrl());
        existing.setMaxParticipants(updatedEvent.getMaxParticipants());
        existing.setDate(updatedEvent.getDate());
        if (updatedEvent.getDate() != null) {
            existing.setDayOfWeek(updatedEvent.getDate().getDayOfWeek());
        }
        existing.setTime(updatedEvent.getTime());
        existing.setDurationMinutes(updatedEvent.getDurationMinutes());
        existing.setBasePrice(updatedEvent.getBasePrice());
        existing.setInterest(updatedEvent.getInterest());
        existing.setLocation(updatedEvent.getLocation());
        existing.setEventType(updatedEvent.getEventType());
        existing.setInstructor(updatedEvent.getInstructor());

        return repo.save(existing);
    }

    public void delete(Long id) {
        Events event = getById(id);
        repo.delete(event);
    }

    public List<Events> getEventsForUser(Long userId) {
        return repo.findByRegistrationsUserId(userId);
    }

    public List<Events> getSuggestions(Long userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return repo.findByInterestIn(user.getInterests());
    }

    public List<Events> getByInterest(Long interestId) {
        return repo.findByInterestId(interestId);
    }

    public List<Events> getByDate(LocalDate date) {
        return repo.findByDate(date);
    }

    public List<Events> getByCreator(Long userId) {
        return repo.findByCreatorId(userId);
    }

    public List<SeatDTO> getSeatsForEvent(Long eventId) {
        List<Seat> seats = seatRepo.findByEventId(eventId);
        return seats.stream().map(seat -> new SeatDTO(
                seat.getId(),
                seat.getBlockName(),
                seat.getRowNumber(),
                seat.getSeatNumber(),
                seat.getPrice(),
                seat.isBooked()
        )).collect(Collectors.toList());
    }

    private void generateComplexAuditorium(Events event) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= 5; row++) {
            for (int seatNum = 1; seatNum <= 10; seatNum++) {
                Seat seat = new Seat();
                seat.setBlockName("Main Block");
                seat.setRowNumber(row);
                seat.setSeatNumber(seatNum);
                seat.setPrice(event.getBasePrice());
                seat.setEvent(event);
                seat.setBooked(false);
                seats.add(seat);
            }
        }

        for (int row = 1; row <= 3; row++) {
            for (int seatNum = 1; seatNum <= 8; seatNum++) {
                Seat seat = new Seat();
                seat.setBlockName("Balcony");
                seat.setRowNumber(row);
                seat.setSeatNumber(seatNum);
                seat.setPrice(event.getBasePrice() * 0.85);
                seat.setEvent(event);
                seat.setBooked(false);
                seats.add(seat);
            }
        }

        seatRepo.saveAll(seats);
    }

    @Transactional
    public List<SeatDTO> bookSeats(Long eventId, Long userId, List<Long> seatIds) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Events event = repo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        List<Seat> bookedSeats = new ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

            if (!seat.getEvent().getId().equals(eventId)) {
                throw new RuntimeException("Seat " + seatId + " does not belong to event " + eventId);
            }

            if (seat.isBooked()) {
                throw new RuntimeException("Seat number " + seat.getSeatNumber() + " in " + seat.getBlockName() + " is already booked!");
            }

            seat.setBooked(true);
            seat.setUser(user);
            bookedSeats.add(seatRepo.save(seat));
        }

        // שימוש בפונקציית הרישום המסודרת של ה-RegistrationService
        registrationService.register(userId, eventId);

        return bookedSeats.stream().map(seat -> new SeatDTO(
                seat.getId(),
                seat.getBlockName(),
                seat.getRowNumber(),
                seat.getSeatNumber(),
                seat.getPrice(),
                seat.isBooked()
        )).collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long eventId, Long userId) {
        // קורא ישירות ל-unregister של ה-RegistrationService שמנקה גם כיסאות וגם מקפיץ את התור
        registrationService.unregister(userId, eventId);
    }
}