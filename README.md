# Ticket de Soporte - Microservicio

Microservicio REST construido con **Spring Boot 3** para registrar y consultar tickets de soporte técnico.

**Repositorio GitHub:** [github.com/PabloTerrazas16/ticket](https://github.com/PabloTerrazas16/ticket)

---

## 📋 Estrategia de Ramificación — GitFlow

### ¿Por qué GitFlow?

Elegimos **GitFlow** porque:

- Desarrollo continuo en `develop` sin afectar producción
- Releases controladas desde `main`
- Features aisladas en ramas dedicadas sin interferencias
- Hotfixes rápidos para producción sin interrumpir desarrollo
- Ideal para equipos pequeños (2-3 personas) con releases planeadas
- Historial de git más limpio y rastreable

**Alternativa rechazada:** Trunk-based development requeriría feature flags complejos y más automatización, innecesarios para este proyecto.

### Estructura de Ramas

```
main (producción) ←── hotfix/fix-ticket-status
  │
  ├── develop (integración) ←── feature/add-ticket-validations
  │                          ←── feature/agregar-endpoint-tickets
  │                          ←── feature/validacion-formulario
  │
  └── Merges después de PR aprobado
```

| Rama        | Propósito               | Base      | Merge a            |
| ----------- | ----------------------- | --------- | ------------------ |
| `main`      | Código en producción    | -         | -                  |
| `develop`   | Integración de features | `main`    | `main` (releases)  |
| `feature/*` | Nuevas funcionalidades  | `develop` | `develop` (PR)     |
| `hotfix/*`  | Bugs críticos en prod   | `main`    | `main` + `develop` |

---

## 🔄 Flujo Colaborativo con Git

### Crear una Feature

```bash
# 1. Partir desde develop actualizado
git checkout develop
git pull origin develop

# 2. Crear rama feature
git checkout -b feature/nombre-descriptivo

# 3. Hacer cambios y commits
git add .
git commit -m "feat: descripción clara del cambio"

# 4. Subir rama
git push -u origin feature/nombre-descriptivo

# 5. Crear Pull Request en GitHub
#    Base: develop
#    Esperar aprobación del compañero
```

**Ejemplo realizado:**

```bash
git checkout -b feature/add-ticket-validations develop
# Agregué validaciones de longitud mínima y patrones
git commit -m "feat: agregar validaciones mejoradas para tickets"
git push origin feature/add-ticket-validations
```

### Crear un Hotfix

```bash
# 1. Partir desde main en producción
git checkout main
git pull origin main

# 2. Crear rama hotfix
git checkout -b hotfix/nombre-bug-critico

# 3. Hacer corrección
git commit -m "fix: descripción clara de la corrección"

# 4. Subir rama
git push -u origin hotfix/nombre-bug-critico

# 5. Crear Pull Request en GitHub
#    Base: main (urgente)
#    Después hacer merge también a develop
```

**Ejemplo realizado:**

```bash
git checkout -b hotfix/fix-ticket-status main
# Corregí NullPointerException al consultar ticket inexistente
git commit -m "fix: corregir inicialización de status de ticket"
git push origin hotfix/fix-ticket-status
```

### Finalizar un Feature/Hotfix

```bash
# Después de aprobación en PR:
git checkout develop  # o main si es hotfix
git pull origin develop
git merge --squash feature/nombre  # squash en develop
git push origin develop

# Opcionalmente eliminar rama remota
git push origin --delete feature/nombre
```

---

## 📝 Convenciones de Commits

Usamos **Conventional Commits** para commits claros y automatizados:

### Prefijos de Commit

| Prefijo     | Uso                      | Ejemplo                                        |
| ----------- | ------------------------ | ---------------------------------------------- |
| `feat:`     | Nueva funcionalidad      | `feat: agregar endpoint GET /tickets/{id}`     |
| `fix:`      | Corrección de bug        | `fix: corregir NullPointerException en ticket` |
| `docs:`     | Documentación            | `docs: actualizar README con GitFlow`          |
| `style:`    | Formato (sin lógica)     | `style: formatear código TicketController`     |
| `refactor:` | Reorganización de código | `refactor: extraer validación a método`        |
| `test:`     | Tests                    | `test: agregar test para validaciones`         |
| `chore:`    | Build, deps, config      | `chore: actualizar pom.xml`                    |

### Reglas

- Usar **presente** (no pasado): Correcto: `agregar` Incorrecto: `agregué`
- **Minúscula** después del prefijo
- **Sin punto** al final
- Máximo **50 caracteres** en el asunto
- Descripción opcional en cuerpo (separada por línea en blanco)

### Ejemplos Reales

```
feat: agregar validaciones mejoradas para tickets

- Validación de longitud mínima (5 caracteres en título)
- Validación de patrones para caracteres permitidos
- Trimeo de espacios en blanco
- Método validateTicketRequest() en servicio
```

```
fix: corregir inicialización de status de ticket

- Agregar validación nula en @PrePersist
- Inicializar updatedAt en creación
- Mejorar respuestas de error en manejador global
```

---

## 🔍 Estrategia de Revisión de Código

### Proceso de Pull Request

1. **Crear PR en GitHub**
   - Título descriptivo (usar mismo formato que commit)
   - Descripción: qué cambios, por qué, testing realizado

2. **Verificaciones Automáticas** (GitHub Actions)
   - Build Maven exitoso
   - Tests pasados
   - Sin errores de compilación
   - Cobertura mínima mantenida

3. **Revisión de Compañero**
   - Mínimo 1 aprobación requerida
   - Comentarios en líneas de código si aplica
   - Conversación clara antes de aprobar

4. **Merge a la Rama Base**
   - **Feature → develop:** Squash merge (1 commit limpio)
   - **Hotfix → main:** Merge commit (preservar historial)
   - **Hotfix → develop:** Cherry-pick o merge posterior

5. **Limpieza**
   - Eliminar rama remota después del merge
   - Cerrar automáticamente con comentario en PR

### Template de PR

```markdown
## Descripción

Qué cambios se hacen y por qué

## 🔍 Tipo de Cambio

- [ ] Feature
- [ ] Hotfix
- [ ] Refactor
- [ ] Docs

## Checklist

- [ ] Código compilable
- [ ] Tests pasados
- [ ] Sin errores de linting
- [ ] Documentación actualizada

## Testing

Cómo se probó:
```

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

| Campo    | Valor                  |
| -------- | ---------------------- |
| JDBC URL | `jdbc:h2:mem:ticketdb` |
| Usuario  | `sa`                   |
| Password | _(vacío)_              |

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

| Estado        | Descripción              |
| ------------- | ------------------------ |
| `OPEN`        | Recién creado            |
| `IN_PROGRESS` | En proceso de resolución |
| `RESOLVED`    | Resuelto                 |
| `CLOSED`      | Cerrado definitivamente  |

---

## Estructura del proyecto

````
src/main/java/com/example/ticket/
├── controller/     → Endpoints REST
├── service/        → Lógica de negocio (interfaz + implementación)
├── repository/     → Acceso a base de datos (Spring Data JPA)
├── model/          → Entidad Ticket + Enum TicketStatus
├── dto/            → TicketRequest (entrada) y TicketResponse (salida)
├── event/          → Evento de creación + Listener (aviso externo)
└── exception/      → Excepciones custom + manejo global de errores```

---

## 🚀 GitHub Actions — CI/CD Automatizado

### Configuración

Se han configurado **4 jobs automáticos** en `.github/workflows/ci.yml`:

| Job | Triggers | Qué Hace |
|-----|----------|----------|
| **build** | `push` a develop/main, `pull_request` | Compila código y ejecuta tests unitarios |
| **quality-checks** | `push` a develop/main, `pull_request` | Verifica estilo de código y estructura |
| **integration-tests** | `push` a develop/main | Ejecuta tests de integración |
| **notify-results** | Siempre | Reporta resultado final del pipeline |

### Flujo de Ejecución

````

GitHub → Push o Pull Request
│
├── Build (Maven clean package)
│ ├── Compilar código
│ └── Ejecutar tests unitarios
│
├── Quality Checks (en paralelo)
│ ├── Verificar compilación
│ └── Validar estructura
│
└── Integration Tests (solo en main/develop)
├── Verificar build completo
└── Generar JAR final

    ↓

✅ APROBADO o ❌ FALLADO (se notifica automáticamente)

```

### Cuándo se Ejecuta

- ✅ **En cada `push` a `develop`**: Validación completa
- ✅ **En cada `push` a `main`**: Validación + tests integración
- ✅ **En cada `pull_request` a main/develop**: Validación antes de merge
- ❌ **En feature branches**: No se ejecuta (economía de recursos)

### Interpretación de Resultados

En tu PR verás un checkbox verde ✅ si todos los checks pasaron:

```

Checks
✅ build (Ubuntu latest) — Passed in 2m 34s
✅ quality-checks — Passed in 1m 12s  
✅ integration-tests — Passed in 3m 18s
✅ notify-results — Passed in 23s

```

Si falla uno (❌), no podrás hacer merge hasta corregir el problema.

### Logs y Debugging

Si algo falla en GitHub Actions:

1. Haz click en el check ❌ en el PR
2. Haz click en **"Details"**
3. Ver logs detallados de qué pasó
4. Corregir localmente y hacer push nuevamente

---

## 📊 Resumen de Buenas Prácticas Implementadas

| Práctica | Implementación |
|----------|-----------------|
| **Branching** | GitFlow con ramas feature/hotfix aisladas |
| **Commits** | Conventional Commits con prefijos claros |
| **PR Review** | Aprobación obligatoria antes de merge |
| **CI/CD** | GitHub Actions con build, tests, quality checks |
| **Testing** | Tests unitarios + tests integración en main |
| **Documentación** | README completo con instrucciones y flujos |
| **Code Quality** | Compilación obligatoria, estructura validada |
| **Historial Git** | Squash merge en develop, merge commit en main |

---

## Trabajo Colaborativo Realizado

### Features Implementadas (Desarrolladas en Equipo)

**PR #1 — Feature: Agregar Validaciones**
```

Branch: feature/add-ticket-validations
Cambios: Validaciones de longitud mínima, patrones de caracteres
Status: ✅ Merged a develop

```

**PR #2 — Feature: Validación de Formulario**
```

Branch: feature/validacion-formulario
Cambios: Validación de campos obligatorios en DTO
Status: ✅ Merged a develop

```

**PR #3 — Feature: Endpoint de Listar Tickets**
```

Branch: feature/agregar-endpoint-tickets
Cambios: GET /api/tickets con filtro por estado
Status: ✅ Merged a develop

```

### Hotfix Realizado (Crítico para Producción)

**PR #4 — Hotfix: NullPointerException**
```

Branch: hotfix/corregir-null-pointer
Cambios: Manejo correcto de excepciones en consultas
Status: ✅ Merged a main + cherry-pick a develop

```

---

## Próximos Pasos Sugeridos

- [ ] Agregar test coverage con JaCoCo
- [ ] Configurar SonarQube para análisis de calidad
- [ ] Agregar deploy automático a staging
- [ ] Implementar semantic versioning en releases
- [ ] Agregar CODEOWNERS para revisiones automáticas
```
