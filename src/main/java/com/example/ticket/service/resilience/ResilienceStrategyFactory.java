package com.example.ticket.service.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory que implementa el patrón Factory Method para crear
 * estrategias de resiliencia configurables.
 */
@Slf4j
@Component
public class ResilienceStrategyFactory {

    @Value("${resilience.strategy:RETRY}")
    private String strategyType;

    @Value("${resilience.retry.maxAttempts:3}")
    private int maxAttempts;

    @Value("${resilience.retry.initialDelayMs:100}")
    private long initialDelayMs;

    @Value("${resilience.retry.backoffMultiplier:2.0}")
    private double backoffMultiplier;

    @Value("${resilience.circuitbreaker.failureThreshold:5}")
    private int failureThreshold;

    @Value("${resilience.circuitbreaker.timeoutMs:30000}")
    private long timeoutMs;

    /**
     * Crea una estrategia de resiliencia basada en la configuración.
     * Implementa el patrón Factory Method.
     *
     * @return Una instancia de ResilienceStrategy
     */
    public ResilienceStrategy createStrategy() {
        log.info("Creando estrategia de resiliencia: {}", strategyType);

        return switch (strategyType.toUpperCase()) {
            case "RETRY" -> new RetryStrategy(maxAttempts, initialDelayMs, backoffMultiplier);
            case "CIRCUIT_BREAKER" -> new CircuitBreakerStrategy(failureThreshold, timeoutMs);
            case "NOOP" -> new NoOpStrategy();
            default -> {
                log.warn("Tipo de estrategia desconocido: {}. Usando NOOP", strategyType);
                yield new NoOpStrategy();
            }
        };
    }

    /**
     * Crea una estrategia específica de reintentos.
     *
     * @param maxAttempts       Número máximo de intentos
     * @param initialDelayMs    Retraso inicial en milisegundos
     * @param backoffMultiplier Multiplicador de backoff exponencial
     * @return Una instancia de RetryStrategy
     */
    public ResilienceStrategy createRetryStrategy(int maxAttempts, long initialDelayMs, double backoffMultiplier) {
        return new RetryStrategy(maxAttempts, initialDelayMs, backoffMultiplier);
    }

    /**
     * Crea una estrategia específica de Circuit Breaker.
     *
     * @param failureThreshold Umbral de fallos para abrir el circuito
     * @param timeoutMs        Tiempo de espera antes de intentar recuperación
     * @return Una instancia de CircuitBreakerStrategy
     */
    public ResilienceStrategy createCircuitBreakerStrategy(int failureThreshold, long timeoutMs) {
        return new CircuitBreakerStrategy(failureThreshold, timeoutMs);
    }

    /**
     * Crea una estrategia sin operaciones.
     *
     * @return Una instancia de NoOpStrategy
     */
    public ResilienceStrategy createNoOpStrategy() {
        return new NoOpStrategy();
    }
}
