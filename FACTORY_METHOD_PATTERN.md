# Patrón Factory Method - Estrategias de Resiliencia

## Descripción General

Se ha implementado el **patrón Factory Method** para crear diferentes estrategias de resiliencia en el proyecto de tickets. Este patrón permite crear objetos sin especificar las clases concretas, proporcionando flexibilidad y facilidad de mantenimiento.

## Componentes Implementados

### 1. **ResilienceStrategy** (Interfaz)

Contrato que define cómo debe comportarse cualquier estrategia de resiliencia.

```java
public interface ResilienceStrategy {
    <T> T execute(Operation<T> operation);
    String getName();
}
```

### 2. **Operation<T>** (Interfaz Funcional)

Representa una operación que puede ejecutarse con una estrategia de resiliencia.

```java
@FunctionalInterface
public interface Operation<T> {
    T execute() throws Exception;
}
```

### 3. **RetryStrategy**

Implementación que reintenta la operación con backoff exponencial.

**Características:**

- Reintentos configurables
- Backoff exponencial para espaciar los intentos
- Logging de cada intento
- Configuración desde `application.properties`

**Parámetros:**

- `resilience.retry.maxAttempts=3` (máximo 3 intentos)
- `resilience.retry.initialDelayMs=100` (retraso inicial de 100ms)
- `resilience.retry.backoffMultiplier=2.0` (multiplica el retraso por 2 cada intento)

### 4. **CircuitBreakerStrategy**

Implementación que evita llamadas a servicios fallidos temporalmente.

**Estados:**

- **CLOSED**: Servicio disponible, las operaciones se ejecutan normalmente
- **OPEN**: Servicio indisponible, se rechazan operaciones para evitar carga
- **HALF_OPEN**: Intento de recuperación, se permite una operación de prueba

**Parámetros:**

- `resilience.circuitbreaker.failureThreshold=5` (abrir después de 5 fallos)
- `resilience.circuitbreaker.timeoutMs=30000` (reintentar tras 30 segundos)

### 5. **NoOpStrategy**

Estrategia "sin operación" que ejecuta sin mecanismos de resiliencia. Útil para desarrollo.

### 6. **ResilienceStrategyFactory** (Factory Method)

Implementa el patrón Factory Method para crear estrategias de forma dinámica.

```java
public ResilienceStrategy createStrategy() {
    switch (strategyType.toUpperCase()) {
        case "RETRY" -> new RetryStrategy(...);
        case "CIRCUIT_BREAKER" -> new CircuitBreakerStrategy(...);
        case "NOOP" -> new NoOpStrategy();
        default -> new NoOpStrategy();
    }
}
```

### 7. **ResilienceConfiguration**

Configuración de Spring que registra el Bean de `ResilienceStrategy`.

## Cómo Usar

### Cambiar la Estrategia de Resiliencia

En `application.properties`:

```properties
# Para usar Retry
resilience.strategy=RETRY

# Para usar Circuit Breaker
resilience.strategy=CIRCUIT_BREAKER

# Para usar sin resiliencia (desarrollo)
resilience.strategy=NOOP
```

### En el Código

La estrategia se inyecta automáticamente en `TicketServiceImpl`:

```java
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final ResilienceStrategy resilienceStrategy;

    @Override
    public TicketResponse registerTicket(TicketRequest request) {
        return resilienceStrategy.execute(() -> {
            // Lógica del negocio
            return result;
        });
    }
}
```

## Beneficios

1. **Flexibilidad**: Cambiar de estrategia sin modificar código
2. **Mantenibilidad**: Cada estrategia en su propia clase
3. **Testabilidad**: Fácil de mockear y testear
4. **Escalabilidad**: Agregar nuevas estrategias es simple
5. **Configurabilidad**: Control total desde `application.properties`

## Ejemplo de Flujo

### Retry Strategy

```
Intento 1: Falla
  ↓ Espera 100ms
Intento 2: Falla
  ↓ Espera 200ms (100 * 2)
Intento 3: Éxito ✓
```

### Circuit Breaker Strategy

```
Solicitudes 1-4: Falla
  ↓ Fallos registrados (4/5)
Solicitud 5: Falla
  ↓ Circuito ABIERTO
Solicitud 6: Rechazada (rápido, sin espera)
  ↓ Espera timeout
Solicitud 7: Intento en HALF_OPEN
  ↓ Éxito
  ↓ Circuito CERRADO (vuelve a funcionar)
```

## Configuración Recomendada por Entorno

### Desarrollo

```properties
resilience.strategy=NOOP
```

### Pruebas

```properties
resilience.strategy=RETRY
resilience.retry.maxAttempts=2
resilience.retry.initialDelayMs=50
```

### Producción

```properties
resilience.strategy=CIRCUIT_BREAKER
resilience.circuitbreaker.failureThreshold=3
resilience.circuitbreaker.timeoutMs=60000
```

## Extensión Futura

Para agregar nuevas estrategias:

1. Crear clase que implemente `ResilienceStrategy`
2. Agregar caso en `ResilienceStrategyFactory.createStrategy()`
3. Agregar configuraciones en `application.properties`

Ejemplo:

```java
public class TimeoutStrategy implements ResilienceStrategy {
    // Implementación
}
```

---

**Patrón Implementado**: Factory Method (Creational Pattern)
**Beneficio Principal**: Resiliencia configurable y extensible
