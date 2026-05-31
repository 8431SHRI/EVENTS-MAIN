package com.example.projects.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private Long id;
    private String blockName;
    private int rowNumber;
    private int seatNumber;
    private double price;
    private boolean isBooked;
}