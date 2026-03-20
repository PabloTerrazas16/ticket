package com.example.ticket.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long id) {
        super("Ticket no encontrado con ID: " + id);
    }
}
