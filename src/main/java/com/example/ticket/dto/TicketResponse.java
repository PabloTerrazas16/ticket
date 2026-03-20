package com.example.ticket.dto;

import com.example.ticket.model.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}
