package com.example.ticket.service.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Estrategia de resiliencia con reintentos exponenciales.
 * Implementa lógica de reintento con backoff exponencial.
 */
@Slf4j
@RequiredArgsConstructor
public class RetryStrategy implements ResilienceStrategy {

    private final int maxAttempts;
    private final long initialDelayMs;
    private final double backoffMultiplier;

    @Override
    public <T> T execute(Operation<T> operation) {
        long delay = initialDelayMs;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Intento {} de {}", attempt, maxAttempts);
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                log.warn("Intento {} falló: {}", attempt, e.getMessage());

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delay);
                        delay = (long) (delay * backoffMultiplier);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupción durante el reintento", ie);
                        throw new RuntimeException(ie);
                    }
                }
            }
        }

        log.error("Operación falló después de {} intentos", maxAttempts);
        throw new RuntimeException("Operación falló después de " + maxAttempts + " intentos", lastException);
    }

    @Override
    public String getName() {
        return "RetryStrategy";
    }
}
