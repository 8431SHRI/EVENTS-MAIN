package com.example.projects.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    private String title;

    private String description;

    private String imageUrl;

    @Min(value = 1, message = "Max participants must be at least 1")
    private int maxParticipants;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must be today or in the future")
    private LocalDate date;

    private DayOfWeek dayOfWeek;

    @NotNull(message = "Event time is required")
    private LocalTime time;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private int durationMinutes;

    @Min(value = 0, message = "Base price cannot be negative")
    private double basePrice;

    @ManyToOne
    @JsonIgnoreProperties("events")
    private Interests interest;

    @NotNull(message = "Location type is required")
    @Enumerated(EnumType.STRING)
    private LocationType location;

    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private boolean recurring;

    @Enumerated(EnumType.STRING)
    private RecurringType recurringType;

    private LocalDate recurringUntil;
    private String recurringGroupId;

    @ManyToOne
    @JsonIgnoreProperties({"registrations", "interests", "credentials"})
    private Users creator;

    @ManyToOne
    private Users instructor;

    @OneToMany(mappedBy = "event")
    @JsonManagedReference(value = "event-registration")
    private List<Registration> registrations;

    public enum LocationType {
        LECTURE_HALL, COMPUTER_ROOM, GYM, STUDIO
    }

    public enum EventType {
        LECTURE, SPORT, WORKSHOP, SHOW
    }

    public enum RecurringType {
        NONE, WEEKLY, MONTHLY
    }
}