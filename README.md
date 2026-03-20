# Ticket de Soporte - Microservicio

Microservicio REST construido con **Spring Boot 3** para registrar y consultar tickets de soporte técnico.

---

## Requisitos

- Java 17+
- Maven (incluido via `mvnw`)

---

## Cómo ejecutarlo

### 1. Clonar o abrir el proyecto

```bash
cd ticket
```

### 2. Compilar y levantar el servidor

**Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**Linux / Mac:**
```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

### 3. (Opcional) Abrir la consola de base de datos H2

URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

| Campo    | Valor                        |
|----------|------------------------------|
| JDBC URL | `jdbc:h2:mem:ticketdb`       |
| Usuario  | `sa`                         |
| Password | *(vacío)*                    |

---

## APIs disponibles

### API 1 — Registrar Ticket

```
POST /api/tickets
Content-Type: application/json
```

**Body:**
```json
{
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales",
  "status": "OPEN",
  "createdAt": "2026-03-11T10:00:00",
  "updatedAt": null
}
```

---

### API 2 — Consultar Estado del Ticket

```
GET /api/tickets/{id}/status
```

**Ejemplo:**
```
GET /api/tickets/1/status
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales",
  "status": "OPEN",
  "createdAt": "2026-03-11T10:00:00",
  "updatedAt": null
}
```

**Ticket no encontrado (404):**
```json
{
  "timestamp": "2026-03-11T10:00:00",
  "status": 404,
  "error": "Ticket no encontrado con ID: 99"
}
```

---

## Cómo funciona

```
Request HTTP
     │
     ▼
┌─────────────────┐
│   Controller    │  Recibe y valida la petición HTTP
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Service      │  Lógica de negocio. Al crear un ticket,
│                 │  publica un TicketCreatedEvent
└────────┬────────┘
         │
    ┌────┴───────────────────────┐
    │                            │
    ▼                            ▼
┌──────────┐          ┌─────────────────────┐
│Repository│          │  EventListener      │
│ (H2 DB)  │          │  Avisa al proceso   │
└──────────┘          │  externo (*)        │
                      └─────────────────────┘
```

**(\*) Punto de extensión** — en `TicketCreatedEventListener` se integra la notificación al sistema externo. Por defecto solo loguea el evento. Se puede reemplazar por:

- **Kafka:** `kafkaTemplate.send(...)`
- **RabbitMQ:** `rabbitTemplate.convertAndSend(...)`
- **Otro microservicio:** `restTemplate.postForEntity(...)`
- **Email:** `javaMailSender.send(...)`

---

## Estados del Ticket

| Estado      | Descripción                     |
|-------------|---------------------------------|
| `OPEN`      | Recién creado                   |
| `IN_PROGRESS` | En proceso de resolución      |
| `RESOLVED`  | Resuelto                        |
| `CLOSED`    | Cerrado definitivamente         |

---

## Estructura del proyecto

```
src/main/java/com/example/ticket/
├── controller/     → Endpoints REST
├── service/        → Lógica de negocio (interfaz + implementación)
├── repository/     → Acceso a base de datos (Spring Data JPA)
├── model/          → Entidad Ticket + Enum TicketStatus
├── dto/            → TicketRequest (entrada) y TicketResponse (salida)
├── event/          → Evento de creación + Listener (aviso externo)
└── exception/      → Excepciones custom + manejo global de errores
```
