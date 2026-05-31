package com.example.projects.Services;

import com.example.projects.Dtos.RegistrationDTO;
import com.example.projects.Repositories.EventsRepo;
import com.example.projects.Repositories.RegistrationRepo;
import com.example.projects.Repositories.UsersRepo;
import com.example.projects.Repositories.SeatRepo;
import com.example.projects.entities.*;
import com.example.projects.exceptions.BadRequestException;
import com.example.projects.exceptions.ResourceNotFoundException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepo registrationRepo;
    private final UsersRepo usersRepo;
    private final EventsRepo eventsRepo;
    private final SeatRepo seatRepo;
    private final JavaMailSender mailSender; // הוספת ה-final והסוג הנכון

    // עדכון הבנאי כך שיקבל גם את ה-mailSender מ-Spring
    public RegistrationService(RegistrationRepo registrationRepo,
                               UsersRepo usersRepo,
                               EventsRepo eventsRepo,
                               SeatRepo seatRepo,
                               JavaMailSender mailSender) { // <-- הוספה כאן
        this.registrationRepo = registrationRepo;
        this.usersRepo = usersRepo;
        this.eventsRepo = eventsRepo;
        this.seatRepo = seatRepo;
        this.mailSender = mailSender; // <-- אתחול כאן
    }

    public boolean hasAvailableSpots(Long eventId) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        int currentConfirmedCount = registrationRepo.countByEventIdAndStatus(eventId, Registration.RegistrationStatus.CONFIRMED);
        return currentConfirmedCount < event.getMaxParticipants();
    }

    @Transactional
    public Registration register(Long userId, Long eventId) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Registration reg = new Registration();
        reg.setUser(user);
        reg.setEvent(event);
        reg.setCreatedAt(LocalDateTime.now());

        if (!hasAvailableSpots(eventId)) {
            reg.setStatus(Registration.RegistrationStatus.WAITING_LIST);
        } else {
            reg.setStatus(Registration.RegistrationStatus.CONFIRMED);

            Interests eventInterest = event.getInterest();
            if (eventInterest != null && user.getInterests() != null) {
                if (!user.getInterests().contains(eventInterest)) {
                    user.getInterests().add(eventInterest);
                    usersRepo.save(user);
                }
            }
        }

        return registrationRepo.save(reg);
    }

    @Transactional
    public void unregister(Long userId, Long eventId) {
        Registration reg = registrationRepo.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        Events event = reg.getEvent();

        if (event.getLocation() == Events.LocationType.LECTURE_HALL) {
            List<Seat> eventSeats = seatRepo.findByEventId(eventId);
            eventSeats.stream()
                    .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
                    .forEach(s -> {
                        s.setBooked(false);
                        s.setUser(null);
                        seatRepo.save(s);
                    });
        }

        boolean wasConfirmed = reg.getStatus() == Registration.RegistrationStatus.CONFIRMED;

        registrationRepo.delete(reg);

        if (wasConfirmed) {
            promoteNextFromQueue(eventId);
        }
    }

    private void promoteNextFromQueue(Long eventId) {
        List<Registration> waitingList = registrationRepo.findByEventIdAndStatusOrderByCreatedAtAsc(
                eventId,
                Registration.RegistrationStatus.WAITING_LIST
        );

        if (waitingList.isEmpty()) return;

        Registration nextInLine = waitingList.get(0);
        nextInLine.setStatus(Registration.RegistrationStatus.OFFERED);
        registrationRepo.save(nextInLine);

        sendOfferEmail(nextInLine.getUser(), nextInLine.getEvent());
    }

    private void sendOfferEmail(Users user, Events event) {
        if (user.getCredentials() == null || user.getCredentials().getEmail() == null) {
            System.out.println("Cannot send email: User has no email credentials");
            return;
        }

        String toEmail = user.getCredentials().getEmail();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@events-system.com");
            message.setTo(toEmail);
            message.setSubject("התפנה מקום עבורך באירוע: " + event.getTitle());
            message.setText("שלום " + user.getUsername() + ",\n\n" +
                    "שמחים לעדכן כי התפנה מקום באירוע: '" + event.getTitle() + "'.\n" +
                    "יש לך אפשרות להיכנס כעת למערכת ולאשר את הגעתך!\n\n" +
                    "בברכה,\nצוות מערכת האירועים");

            mailSender.send(message);
            System.out.println("Mailing system: Offer email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send offer email to " + toEmail + ". Error: " + e.getMessage());
        }
    }

    @Transactional
    public Registration confirmOffer(Long userId, Long eventId) {
        Registration reg = registrationRepo.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (reg.getStatus() == null || !reg.getStatus().name().equals("OFFERED")) {
            throw new BadRequestException("You do not have an active offer for this event");
        }

        reg.setStatus(Registration.RegistrationStatus.CONFIRMED);

        if (reg.getEvent() != null && reg.getEvent().getLocation() == Events.LocationType.LECTURE_HALL) {
            System.out.println("Seat booked successfully for Lecture Hall");
        } else {
            System.out.println("Registration confirmed for a regular room (No seats required)");
        }

        return registrationRepo.save(reg);
    }

    public List<Events> getMyEvents(Long userId) {
        if (!usersRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return registrationRepo.findByUserId(userId).stream()
                .map(Registration::getEvent)
                .toList();
    }

    public List<RegistrationDTO> getUserRegistrations(Long userId) {
        if (!usersRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<Registration> registrations = registrationRepo.findByUserId(userId);

        return registrations.stream().map(reg -> new RegistrationDTO(
                reg.getId(),
                reg.getEvent() != null ? reg.getEvent().getId() : null,
                reg.getEvent() != null ? reg.getEvent().getTitle() : "Unknown Event",
                reg.getEvent() != null ? reg.getEvent().getDate() : null,
                reg.getStatus() != null ? reg.getStatus().name() : "PENDING",
                reg.getCreatedAt()
        )).toList();
    }
}