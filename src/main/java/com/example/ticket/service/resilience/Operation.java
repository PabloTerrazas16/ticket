package com.example.ticket.service.resilience;

/**
 * Interfaz funcional que representa una operación que puede ejecutarse
 * con una estrategia de resiliencia.
 *
 * @param <T> El tipo de valor retornado por la operación
 */
@FunctionalInterface
public interface Operation<T> {

    /**
     * Ejecuta la operación.
     *
     * @return El resultado de la operación
     * @throws Exception Si ocurre un error durante la ejecución
     */
    T execute() throws Exception;
}
