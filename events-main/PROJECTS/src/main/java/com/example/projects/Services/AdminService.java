package com.example.projects.Services;

import com.example.projects.Dtos.AdminStatsDTO;
import com.example.projects.Repositories.EventsRepo; // ודאי שזה השם המדויק של הרפוזיטוריז שלך
import com.example.projects.Repositories.UsersRepo;
import com.example.projects.Repositories.RegistrationRepo;
import com.example.projects.entities.Events;
import com.example.projects.entities.Registration;
import com.example.projects.entities.Users;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final EventsRepo eventsRepo;
    private final UsersRepo usersRepo;
    private final RegistrationRepo registrationRepo;
    private final JavaMailSender mailSender;

    public AdminService(EventsRepo eventsRepo, UsersRepo usersRepo,
                        RegistrationRepo registrationRepo, JavaMailSender mailSender) {
        this.eventsRepo = eventsRepo;
        this.usersRepo = usersRepo;
        this.registrationRepo = registrationRepo;
        this.mailSender = mailSender;
    }

    // א. חישוב סטטיסטיקות לדשבורד
    public AdminStatsDTO getSystemStats() {
        long totalEvents = eventsRepo.count();
        long totalUsers = usersRepo.count();

        // שליפת כל האירועים כדי למצוא את זה עם רשימת ההמתנה הכי ארוכה
        List<Events> allEvents = eventsRepo.findAll();
        String mostRequestedEvent = "None";
        long maxWaiting = 0;

        for (Events event : allEvents) {
            long waitingCount = registrationRepo.countByEventIdAndStatus(
                    event.getId(),
                    Registration.RegistrationStatus.WAITING_LIST
            );
            if (waitingCount > maxWaiting) {
                maxWaiting = waitingCount;
                mostRequestedEvent = event.getTitle();
            }
        }

        return new AdminStatsDTO(totalEvents, totalUsers, mostRequestedEvent, maxWaiting);
    }

    // ב. ביטול אירוע ושליחת מיילים לכל הרשומים
    @Transactional
    public void cancelEvent(Long eventId) {
        Events event = eventsRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // שליפת כל הרישומים של האירוע הזה (גם מאושרים וגם בהמתנה)
        List<Registration> registrations = event.getRegistrations();

        if (registrations != null && !registrations.isEmpty()) {
            for (Registration reg : registrations) {
                Users user = reg.getUser();
                // שליחת מייל ביטול לכל משתמש בלולאה
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(user.getCredentials().getEmail());
                    message.setSubject("Cancellation Notice: " + event.getTitle());
                    message.setText("Dear " + user.getUsername() + ",\n\n" +
                            "We regret to inform you that the event '" + event.getTitle() + "' has been cancelled by the administrator.\n" +
                            "We apologize for any inconvenience caused.");
                    mailSender.send(message);
                } catch (StringIndexOutOfBoundsException | org.springframework.mail.MailException e) {
                    System.out.println("Failed to send cancellation email to: " + user.getCredentials().getEmail());
                }
            }
            // מחיקת כל הרישומים של האירוע מבסיס הנתונים
            registrationRepo.deleteAll(registrations);
        }

        // מחיקת האירוע עצמו
        eventsRepo.delete(event);
    }

    // ג. שדרוג משתמש למנהל (מתודת הבונוס)
    public void promoteToAdmin(Long userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Users.Role.valueOf("ADMIN"));
        usersRepo.save(user);
    }
}