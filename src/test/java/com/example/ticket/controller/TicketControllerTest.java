package com.example.ticket.controller;

import com.example.ticket.dto.TicketRequest;
import com.example.ticket.dto.TicketResponse;
import com.example.ticket.exception.TicketNotFoundException;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@ActiveProfiles("test")
@DisplayName("Tests para TicketController")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    private TicketRequest validRequest;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        validRequest = new TicketRequest();
        validRequest.setTitle("Error en login");
        validRequest.setDescription("No puedo acceder a mi cuenta");

        ticketResponse = TicketResponse.builder()
                .id(1L)
                .title("Error en login")
                .description("No puedo acceder a mi cuenta")
                .status(TicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .message("Correo de confirmación enviado exitosamente")
                .build();
    }

    @Test
    @DisplayName("POST /api/tickets - Registrar ticket exitosamente")
    void testRegisterTicketSuccess() throws Exception {
        // Arrange
        when(ticketService.registerTicket(any(TicketRequest.class))).thenReturn(ticketResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Error en login")))
                .andExpect(jsonPath("$.description", is("No puedo acceder a mi cuenta")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.message", containsString("confirmación")));

        verify(ticketService, times(1)).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("POST /api/tickets - Rechazar request sin título")
    void testRegisterTicketWithoutTitle() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle(null);
        invalidRequest.setDescription("No puedo acceder a mi cuenta");

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("POST /api/tickets - Rechazar request sin descripción")
    void testRegisterTicketWithoutDescription() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle("Error en login");
        invalidRequest.setDescription(null);

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("POST /api/tickets - Rechazar título muy corto")
    void testRegisterTicketWithTitleTooShort() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle("abc");
        invalidRequest.setDescription("No puedo acceder a mi cuenta");

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("POST /api/tickets - Rechazar descripción muy corta")
    void testRegisterTicketWithDescriptionTooShort() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle("Error en login");
        invalidRequest.setDescription("corta");

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("GET /api/tickets/{id}/status - Obtener estado exitosamente")
    void testGetTicketStatusSuccess() throws Exception {
        // Arrange
        when(ticketService.getTicketStatus(1L)).thenReturn(ticketResponse);

        // Act & Assert
        mockMvc.perform(get("/api/tickets/1/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Error en login")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        verify(ticketService, times(1)).getTicketStatus(1L);
    }

    @Test
    @DisplayName("GET /api/tickets/{id}/status - Retornar 404 cuando ticket no existe")
    void testGetTicketStatusNotFound() throws Exception {
        // Arrange
        when(ticketService.getTicketStatus(999L))
                .thenThrow(new TicketNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/tickets/999/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(ticketService, times(1)).getTicketStatus(999L);
    }

    @Test
    @DisplayName("POST /api/tickets - Validar caracteres inválidos en título")
    void testRegisterTicketWithInvalidCharsInTitle() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle("Error @#$ en login");
        invalidRequest.setDescription("No puedo acceder a mi cuenta");

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }

    @Test
    @DisplayName("POST /api/tickets - Validar caracteres inválidos en descripción")
    void testRegisterTicketWithInvalidCharsInDescription() throws Exception {
        // Arrange
        TicketRequest invalidRequest = new TicketRequest();
        invalidRequest.setTitle("Error en login");
        invalidRequest.setDescription("No puedo acceder & a mi cuenta");

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ticketService, never()).registerTicket(any(TicketRequest.class));
    }
}
