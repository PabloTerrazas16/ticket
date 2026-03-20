package com.example.ticket.controller;

import com.example.ticket.dto.TicketRequest;
import com.example.ticket.dto.TicketResponse;
import com.example.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

  
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    @PostMapping
    public ResponseEntity<TicketResponse> registerTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.registerTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}/status")
    public ResponseEntity<TicketResponse> getTicketStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketStatus(id));
    }
}
