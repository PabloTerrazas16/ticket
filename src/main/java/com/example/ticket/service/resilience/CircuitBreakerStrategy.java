package com.example.ticket.service.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;

/**
 * Estrategia de resiliencia con Circuit Breaker.
 * Evita llamadas a servicios fallidos temporalmente.
 */
@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerStrategy implements ResilienceStrategy {

    private final int failureThreshold;
    private final long timeoutMs;

    private final AtomicInteger failureCount = new AtomicInteger(0);
    private volatile CircuitState state = CircuitState.CLOSED;
    private volatile LocalDateTime lastFailureTime;

    enum CircuitState {
        CLOSED, OPEN, HALF_OPEN
    }

    @Override
    public <T> T execute(Operation<T> operation) {
        synchronized (this) {
            if (state == CircuitState.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.getNano() > timeoutMs) {
                    state = CircuitState.HALF_OPEN;
                    log.info("Circuit Breaker en estado HALF_OPEN, intentando operación");
                } else {
                    log.error("Circuit Breaker ABIERTO - Operación rechazada");
                    throw new CircuitBreakerOpenException("El servicio no está disponible en este momento");
                }
            }
        }

        try {
            T result = operation.execute();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw new RuntimeException("Operación fallida: " + e.getMessage(), e);
        }
    }

    private synchronized void onSuccess() {
        failureCount.set(0);
        state = CircuitState.CLOSED;
        log.info("Circuit Breaker cerrado - servicio disponible");
    }

    private synchronized void onFailure() {
        lastFailureTime = LocalDateTime.now();
        int failures = failureCount.incrementAndGet();
        log.warn("Fallos registrados: {}/{}", failures, failureThreshold);

        if (failures >= failureThreshold) {
            state = CircuitState.OPEN;
            log.error("Circuit Breaker ABIERTO después de {} fallos", failures);
        }
    }

    @Override
    public String getName() {
        return "CircuitBreakerStrategy";
    }

    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
