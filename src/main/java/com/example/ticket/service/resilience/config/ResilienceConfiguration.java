package com.example.ticket.service.resilience.config;

import com.example.ticket.service.resilience.ResilienceStrategy;
import com.example.ticket.service.resilience.ResilienceStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para el patrón Factory Method
 * de estrategias de resiliencia.
 */
@Configuration
public class ResilienceConfiguration {

    /**
     * Bean que proporciona la estrategia de resiliencia configurada.
     * Usa el Factory Method para crear la estrategia apropiada.
     *
     * @param factory La factory de estrategias de resiliencia
     * @return Una instancia de ResilienceStrategy configurada
     */
    @Bean
    public ResilienceStrategy resilienceStrategy(ResilienceStrategyFactory factory) {
        return factory.createStrategy();
    }
}
