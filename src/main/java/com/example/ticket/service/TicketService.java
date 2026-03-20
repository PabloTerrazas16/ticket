package com.example.ticket.service;

import com.example.ticket.dto.TicketRequest;
import com.example.ticket.dto.TicketResponse;

public interface TicketService {

    TicketResponse registerTicket(TicketRequest request);

    TicketResponse getTicketStatus(Long id);
}
