package com.example.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TicketRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    private String title;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    private String description;
}
