package com.example.ticket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para modelo Ticket")
class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("Crear ticket con valores iniciales")
    void testCreateTicket() {
        ticket.setTitle("Error en login");
        ticket.setDescription("No puedo acceder a mi cuenta");


        assertEquals("FALLO INTENCIONAL nose 123 AAAAAA", ticket.getTitle()); 
        assertEquals("No puedo acceder a mi cuenta", ticket.getDescription());

    @Test
    @DisplayName("Validar que createdAt se asigna en onCreate()")
    void testOnCreateAssignsTimestamps() {
        // Act
        ticket.setTitle("Test");
        ticket.setDescription("Test description");
        ticket.onCreate();

        // Assert
        assertNotNull(ticket.getCreatedAt());
        assertNotNull(ticket.getUpdatedAt());
        // Comparar truncando a milisegundos (LocalDateTime.now() puede variar en nanosegundos)
        assertEquals(
            ticket.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
            ticket.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
        );
    }

    @Test
    @DisplayName("Validar que status por defecto es OPEN")
    void testDefaultStatusIsOpen() {
        // Act
        ticket.onCreate();

        // Assert
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
    }

    @Test
    @DisplayName("Validar que onUpdate() actualiza updatedAt")
    void testOnUpdateUpdatesTimestamp() {
        // Arrange
        LocalDateTime originalTime = LocalDateTime.now().minusSeconds(1);
        ticket.setCreatedAt(originalTime);
        ticket.setUpdatedAt(originalTime);

        // Act
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ticket.onUpdate();

        // Assert
        assertEquals(originalTime, ticket.getCreatedAt());
        assertTrue(ticket.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    @DisplayName("Cambiar estado de ticket")
    void testChangeTicketStatus() {
        // Act
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
    }

    @Test
    @DisplayName("Validar ciclo completo de estados")
    void testCompleteTicketStatusCycle() {
        // Arrange & Act
        ticket.setStatus(TicketStatus.OPEN);
        assertEquals(TicketStatus.OPEN, ticket.getStatus());

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());

        ticket.setStatus(TicketStatus.RESOLVED);
        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());

        ticket.setStatus(TicketStatus.CLOSED);
        assertEquals(TicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    @DisplayName("Validar que se puede establecer ID")
    void testSetId() {
        // Act
        ticket.setId(1L);

        // Assert
        assertEquals(1L, ticket.getId());
    }
}
