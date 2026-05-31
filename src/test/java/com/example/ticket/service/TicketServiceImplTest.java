package com.example.ticket.service;

import com.example.ticket.dto.TicketRequest;
import com.example.ticket.dto.TicketResponse;
import com.example.ticket.exception.TicketNotFoundException;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.repository.TicketRepository;
import com.example.ticket.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TicketServiceImpl")
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private TicketRequest validRequest;
    private Ticket ticket;
    private TicketResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        validRequest = new TicketRequest();
        validRequest.setTitle("Error en login");
        validRequest.setDescription("No puedo acceder a mi cuenta");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Error en login");
        ticket.setDescription("No puedo acceder a mi cuenta");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        expectedResponse = TicketResponse.builder()
                .id(1L)
                .title("Error en login")
                .description("No puedo acceder a mi cuenta")
                .status(TicketStatus.OPEN)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("Registrar un ticket válido exitosamente")
    void testRegisterTicketSuccess() {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Act
        TicketResponse response = ticketService.registerTicket(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Error en login", response.getTitle());
        assertEquals("No puedo acceder a mi cuenta", response.getDescription());
        assertEquals(TicketStatus.OPEN, response.getStatus());
        assertNotNull(response.getMessage());
        assertTrue(response.getMessage().contains("confirmación"));
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Rechazar ticket con título vacío")
    void testRegisterTicketWithEmptyTitle() {
        // Arrange
        validRequest.setTitle("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.registerTicket(validRequest);
        });
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Rechazar ticket con título null")
    void testRegisterTicketWithNullTitle() {
        // Arrange
        validRequest.setTitle(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.registerTicket(validRequest);
        });
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Rechazar ticket con descripción vacía")
    void testRegisterTicketWithEmptyDescription() {
        // Arrange
        validRequest.setDescription("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.registerTicket(validRequest);
        });
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Rechazar ticket con descripción null")
    void testRegisterTicketWithNullDescription() {
        // Arrange
        validRequest.setDescription(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.registerTicket(validRequest);
        });
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Obtener estado de ticket exitosamente")
    void testGetTicketStatusSuccess() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act
        TicketResponse response = ticketService.getTicketStatus(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Error en login", response.getTitle());
        assertEquals(TicketStatus.OPEN, response.getStatus());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Lanzar excepción cuando ticket no existe")
    void testGetTicketStatusNotFound() {
        // Arrange
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () -> {
            ticketService.getTicketStatus(999L);
        });
        verify(ticketRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Trimear espacios en blanco de título y descripción")
    void testTrimWhitespaces() {
        // Arrange
        validRequest.setTitle("  Error en login  ");
        validRequest.setDescription("  No puedo acceder a mi cuenta  ");
        Ticket ticketWithTrimmed = new Ticket();
        ticketWithTrimmed.setId(1L);
        ticketWithTrimmed.setTitle("Error en login");
        ticketWithTrimmed.setDescription("No puedo acceder a mi cuenta");
        ticketWithTrimmed.setStatus(TicketStatus.OPEN);
        ticketWithTrimmed.setCreatedAt(LocalDateTime.now());
        ticketWithTrimmed.setUpdatedAt(LocalDateTime.now());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticketWithTrimmed);

        // Act
        TicketResponse response = ticketService.registerTicket(validRequest);

        // Assert
        assertEquals("Error en login", response.getTitle());
        assertEquals("No puedo acceder a mi cuenta", response.getDescription());
    }
}
