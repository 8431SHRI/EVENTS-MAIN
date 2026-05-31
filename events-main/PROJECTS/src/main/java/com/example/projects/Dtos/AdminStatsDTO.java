package com.example.projects.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private long totalEvents;
    private long totalUsers;
    private String mostRequestedEventTitle;
    private long maxWaitingListCount;
}