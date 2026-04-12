package com.example.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TicketRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-áéíóúñ]+$", message = "El título contiene caracteres inválidos")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-áéíóúñ.,!?]+$", message = "La descripción contiene caracteres inválidos")
    private String description;
}
/*
**Hola
*/