package com.example.ticket.service.resilience;

import lombok.extern.slf4j.Slf4j;

/**
 * Estrategia sin-op (no operation).
 * Ejecuta la operación sin ningún mecanismo de resiliencia.
 * Útil para desarrollo o cuando no se requiere resiliencia.
 */
@Slf4j
public class NoOpStrategy implements ResilienceStrategy {

    @Override
    public <T> T execute(Operation<T> operation) {
        log.debug("Ejecutando operación sin estrategia de resiliencia");
        try {
            return operation.execute();
        } catch (Exception e) {
            throw new RuntimeException("Operación fallida: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "NoOpStrategy";
    }
}
