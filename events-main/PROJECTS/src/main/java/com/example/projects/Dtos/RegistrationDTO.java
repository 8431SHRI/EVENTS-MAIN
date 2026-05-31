package com.example.projects.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long registrationId;
    private Long eventId;
    private String eventTitle;
    private LocalDate eventDate;
    private String status;
    private LocalDateTime createdAt;
}