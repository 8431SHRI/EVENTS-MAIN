package com.example.projects.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameOfInterest;

    @OneToMany(mappedBy = "interest")
    @JsonIgnore
    private List<Events> events;

    @ManyToMany(mappedBy = "interests")
    @JsonIgnoreProperties("interests")
    private List<Users> users;
}