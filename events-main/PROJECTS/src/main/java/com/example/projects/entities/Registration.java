package com.example.projects.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class Registration {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonBackReference(value = "user-registration")
        private Users user;

        @ManyToOne
        @JoinColumn(name = "event_id")
        @JsonBackReference(value = "event-registration")
        private Events event;

        private LocalDateTime createdAt;

        @Enumerated(EnumType.STRING)
        private RegistrationStatus status;

        public enum RegistrationStatus {
                CONFIRMED,
                WAITING_LIST,
                OFFERED,
                CANCELLED
        }
}