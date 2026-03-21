package com.example.ticket.service.resilience;

/**
 * Interfaz que define una estrategia de resiliencia.
 * Implementa el patrón Factory Method para crear diferentes
 * políticas de manejo de fallos y reintentos.
 */
public interface ResilienceStrategy {

    /**
     * Ejecuta la operación con la estrategia de resiliencia definida.
     *
     * @param operation La operación a ejecutar
     * @param <T>       El tipo de retorno de la operación
     * @return El resultado de la operación
     */
    <T> T execute(Operation<T> operation);

    /**
     * Obtiene el nombre de la estrategia.
     *
     * @return El nombre de la estrategia
     */
    String getName();
}
