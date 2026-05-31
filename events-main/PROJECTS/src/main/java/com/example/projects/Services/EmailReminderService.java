package com.example.projects.Services;

import com.example.projects.Repositories.RegistrationRepo;
import com.example.projects.entities.Registration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailReminderService {

    private final RegistrationRepo registrationRepo;
    private final JavaMailSender mailSender; // הזרקת שירות המיילים האמיתי

    // הבנאי כעת מקבל גם את שירות המייל
    public EmailReminderService(RegistrationRepo registrationRepo, JavaMailSender mailSender) {
        this.registrationRepo = registrationRepo;
        this.mailSender = mailSender;
    }

    // רץ כל יום ב-8 בבוקר ומחפש אירועים של מחר
    @Scheduled(cron = "0 0 8 * * *")
    public void sendReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Registration> registrations = registrationRepo.findAll().stream()
                .filter(reg -> reg.getEvent() != null && tomorrow.equals(reg.getEvent().getDate()))
                .toList();

        for (Registration reg : registrations) {
            if (reg.getUser() != null && reg.getUser().getCredentials() != null) {
                sendEmail(reg.getUser().getCredentials().getEmail(), reg.getEvent().getTitle());
            }
        }
    }

    // מתודה משופרת השולחת מייל אמיתי לתיבה של המשתמש!
    public void sendEmail(String to, String eventTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@events-system.com"); // כתובת השולח
            message.setTo(to); // כתובת היעד
            message.setSubject("תזכורת: אירוע מחר! - " + eventTitle); // נושא המייל
            message.setText("שלום, \n\nנרשמת לאירוע '" + eventTitle + "' שמתקיים מחר. נתראה!\n\nבברכה,\nצוות מערכת האירועים"); // תוכן

            mailSender.send(message); // השליחה הפיזית
            System.out.println("Mailing system: Reminder email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send reminder email to " + to + ". Error: " + e.getMessage());
        }
    }
}