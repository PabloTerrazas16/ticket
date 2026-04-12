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
/*
**hola este es un ejemplo de un controlador de tickets en una aplicación Spring Boot. El controlador tiene dos endpoints: uno para registrar un nuevo ticket y otro para obtener el estado de un ticket existente. El método `registerTicket` recibe una solicitud de tipo `TicketRequest`, valida los datos y devuelve una respuesta de tipo `TicketResponse` con el estado del ticket creado. El método `getTicketStatus` recibe un ID de ticket como parámetro y devuelve el estado del ticket correspondiente.
*/