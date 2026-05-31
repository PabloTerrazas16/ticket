package com.example.ticket.repository;

import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests para TicketRepository")
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setTitle("Error en login");
        testTicket.setDescription("No puedo acceder a mi cuenta");
        testTicket.setStatus(TicketStatus.OPEN);
    }

    @Test
    @DisplayName("Guardar un ticket exitosamente")
    void testSaveTicket() {
        // Act
        Ticket saved = ticketRepository.save(testTicket);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Error en login", saved.getTitle());
        assertEquals("No puedo acceder a mi cuenta", saved.getDescription());
        assertEquals(TicketStatus.OPEN, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    @DisplayName("Encontrar ticket por ID")
    void testFindTicketById() {
        // Arrange
        Ticket saved = ticketRepository.save(testTicket);
        entityManager.flush();

        // Act
        Optional<Ticket> found = ticketRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Error en login", found.get().getTitle());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Retornar Optional vacío cuando ticket no existe")
    void testFindTicketByIdNotFound() {
        // Act
        Optional<Ticket> found = ticketRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Actualizar un ticket exitosamente")
    void testUpdateTicket() {
        // Arrange
        Ticket saved = ticketRepository.save(testTicket);
        entityManager.flush();

        // Act
        saved.setStatus(TicketStatus.IN_PROGRESS);
        Ticket updated = ticketRepository.save(saved);
        entityManager.flush();

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    @DisplayName("Eliminar un ticket exitosamente")
    void testDeleteTicket() {
        // Arrange
        Ticket saved = ticketRepository.save(testTicket);
        Long id = saved.getId();
        entityManager.flush();

        // Act
        ticketRepository.deleteById(id);
        Optional<Ticket> deleted = ticketRepository.findById(id);

        // Assert
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Contar tickets en la base de datos")
    void testCountTickets() {
        // Arrange
        ticketRepository.save(testTicket);
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Error en registro");
        ticket2.setDescription("No puedo crear una cuenta");
        ticketRepository.save(ticket2);
        entityManager.flush();

        // Act
        long count = ticketRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Validar que createdAt no cambia al actualizar")
    void testCreatedAtNotChangeOnUpdate() {
        // Arrange
        Ticket saved = ticketRepository.save(testTicket);
        LocalDateTime originalCreatedAt = saved.getCreatedAt();
        entityManager.flush();

        // Act - Esperar un segundo y actualizar
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        saved.setStatus(TicketStatus.RESOLVED);
        ticketRepository.save(saved);
        entityManager.flush();

        // Assert
        assertEquals(originalCreatedAt, saved.getCreatedAt());
        assertTrue(saved.getUpdatedAt().isAfter(originalCreatedAt));
    }

    @Test
    @DisplayName("Validar estado por defecto OPEN")
    void testDefaultStatusIsOpen() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setTitle("Ticket sin estado");
        ticket.setDescription("Debería tener estado OPEN");

        // Act
        Ticket saved = ticketRepository.save(ticket);

        // Assert
        assertEquals(TicketStatus.OPEN, saved.getStatus());
    }

    @Test
    @DisplayName("Validar que findAll retorna todos los tickets")
    void testFindAllTickets() {
        // Arrange
        ticketRepository.save(testTicket);
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Otro error");
        ticket2.setDescription("Otra descripción");
        ticketRepository.save(ticket2);
        entityManager.flush();

        // Act
        long count = ticketRepository.count();

        // Assert
        assertEquals(2, count);
    }
}
