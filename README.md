# Ticket de Soporte — Microservicio REST

**Proyecto:** Evaluación Parcial N°1 — Ingeniería DevOps (DOY0101)  
**Repositorio GitHub:** [github.com/PabloTerrazas16/ticket](https://github.com/PabloTerrazas16/ticket)

---

## 📌 Introducción

Este repositorio contiene el **Microservicio de Ticket de Soporte**, una API REST construida con **Spring Boot 3** y **Java 17** que permite registrar y consultar tickets de soporte técnico en tiempo real.

El microservicio fue seleccionado como base para este encargo DevOps por dos razones principales:

1. **Claridad funcional:** Expone dos endpoints REST simples pero realistas que demuestran manejo de CRUD, validaciones y excepciones.
2. **Arquitectura representativa:** Implementa una estructura en capas (Controller → Service → Repository) que es estándar en microservicios de producción.

Este proyecto demuestra cómo implementar **buenas prácticas DevOps** desde el diseño, incluyendo estrategia de ramificación, automatización CI/CD, y documentación clara para trabajo colaborativo.

---

## 🔧 Requisitos Técnicos

| Componente | Versión |
|-----------|---------|
| **Java** | 17 o superior |
| **Spring Boot** | 3.5.11 |
| **Maven** | Incluido (mvnw) |
| **Base de Datos** | H2 en memoria (desarrollo/tests) |

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/PabloTerrazas16/ticket.git
cd ticket
```

### 2. Compilar y levantar el servidor

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**Linux / Mac:**
```bash
./mvnw spring-boot:run
```

La aplicación arranca en: **http://localhost:8080**

### 3. (Opcional) Acceder a la consola H2

URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

| Campo | Valor |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:ticketdb` |
| Usuario | `sa` |
| Contraseña | *(vacío)* |

---

## 📡 APIs Disponibles

### API 1 — Registrar Ticket

**Endpoint:** `POST /api/tickets`

Crea un nuevo ticket de soporte con validaciones de campos obligatorios.

**Request:**
```json
{
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales",
  "status": "OPEN",
  "createdAt": "2026-04-12T10:30:00",
  "updatedAt": null
}
```

### API 2 — Consultar Estado del Ticket

**Endpoint:** `GET /api/tickets/{id}/status`

Obtiene el estado actual de un ticket por su ID.

**Request:**
```
GET /api/tickets/1/status
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Error al iniciar sesión",
  "description": "El usuario no puede acceder con sus credenciales",
  "status": "OPEN",
  "createdAt": "2026-04-12T10:30:00",
  "updatedAt": null
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-12T10:35:00",
  "status": 404,
  "error": "Ticket no encontrado con ID: 99"
}
```

---

## 🏗️ Arquitectura Interna

El microservicio sigue una **arquitectura en capas** bien definida:

```
┌─────────────────────────────────────────┐
│        REST Controller                   │ Recibe y valida peticiones HTTP
├─────────────────────────────────────────┤
│        Business Logic (Service)          │ Implementa reglas de negocio
│        + TicketCreatedEvent              │ Publica eventos de creación
├─────────────────────────────────────────┤
│        Repository (Spring Data JPA)      │ Persiste en Base de Datos H2
├─────────────────────────────────────────┤
│        Global Exception Handler          │ Manejo centralizado de errores
└─────────────────────────────────────────┘
```

**Capas del Proyecto:**

- **controller/** — Endpoints REST. Recibe y valida peticiones HTTP.
- **service/** — Lógica de negocio. Implementa reglas y publica eventos.
- **repository/** — Acceso a datos usando Spring Data JPA.
- **model/** — Entidad `Ticket` y enum `TicketStatus`.
- **dto/** — `TicketRequest` (entrada) y `TicketResponse` (salida).
- **event/** — `TicketCreatedEvent` y listener. Punto de extensión para Kafka, RabbitMQ, etc.
- **exception/** — Excepciones personalizadas y manejador global de errores.

**Estados del Ticket:**

| Estado | Descripción |
|--------|-------------|
| `OPEN` | Recién creado, pendiente de atención |
| `IN_PROGRESS` | En proceso de resolución |
| `RESOLVED` | Problema resuelto |
| `CLOSED` | Cerrado definitivamente |

---

## 📋 Estrategia de Ramificación — GitFlow

### ¿Por qué GitFlow?

Se implementó **GitFlow** en lugar de Trunk-Based Development considerando las características específicas del equipo y el proyecto:

- ✅ **Control explícito sobre releases:** La rama `main` siempre refleja el estado de producción estable.
- ✅ **Trabajo colaborativo en paralelo:** Cada integrante puede trabajar de forma aislada en distintas funcionalidades sin interferencias.
- ✅ **Soporte para correcciones urgentes:** Las ramas `hotfix` permiten parches directos sobre `main` sin mezclar código no probado.
- ✅ **Trazabilidad clara:** Cada tipo de cambio tiene su propio ciclo de vida bien definido, facilitando auditorías y revisiones.

**Alternativa rechazada:** Trunk-Based Development sería más apropiado en equipos grandes con integración continua muy madura. Para el alcance de este proyecto académico con dos integrantes, GitFlow ofrece mayor estructura y control.

### Estructura de Ramas

```
main (producción) ←── hotfix/fix-ticket-status
  │
  ├── develop (integración) ←── feature/add-ticket-validations
  │                          ←── feature/agregar-endpoint-tickets
  │                          ←── feature/validacion-formulario
  │
  └── Merges mediante Pull Requests aprobados
```

| Rama | Propósito | Origen | Merge a |
|------|-----------|--------|---------|
| `main` | Código en producción (estable) | - | - |
| `develop` | Integración continua de features | `main` | `main` (releases) |
| `feature/*` | Nuevas funcionalidades aisladas | `develop` | `develop` (PR) |
| `hotfix/*` | Correcciones críticas de producción | `main` | `main` + `develop` |

---

## 🔄 Flujo Colaborativo con Git

### Crear una Feature

```bash
# 1. Actualizar develop local
git checkout develop
git pull origin develop

# 2. Crear rama feature
git checkout -b feature/nombre-descriptivo

# 3. Hacer cambios y commits siguiendo Conventional Commits
git add .
git commit -m "feat: descripción clara del cambio"

# 4. Subir rama al repositorio remoto
git push -u origin feature/nombre-descriptivo

# 5. Abrir Pull Request en GitHub hacia develop
#    - Descripción clara del cambio
#    - Esperar aprobación del compañero
#    - Resolver comentarios del revisor
```

**Ejemplo realizado en este proyecto:**
```bash
git checkout -b feature/add-ticket-validations develop
# Agregué validaciones de longitud mínima y patrones de caracteres
git commit -m "feat: agregar validaciones mejoradas para tickets"
git push origin feature/add-ticket-validations
# → Creado PR #1, aprobado y mergeado a develop
```

### Crear un Hotfix

```bash
# 1. Crear rama directamente desde main (producción)
git checkout main
git pull origin main
git checkout -b hotfix/nombre-bug-critico

# 2. Aplicar la corrección mínima necesaria
git commit -m "fix: descripción clara de la corrección"

# 3. Subir y abrir PR urgente hacia main
git push -u origin hotfix/nombre-bug-critico

# 4. Después del merge a main, integrar también en develop
git checkout develop
git pull origin develop
git merge hotfix/nombre-bug-critico

# 5. Etiquetar versión y limpiar
git tag v1.1.1 && git push origin v1.1.1
git push origin --delete hotfix/nombre-bug-critico
```

**Ejemplo realizado en este proyecto:**
```bash
git checkout -b hotfix/fix-ticket-status main
# Corregí NullPointerException al consultar ticket inexistente
git commit -m "fix: corregir inicialización de status de ticket"
git push origin hotfix/fix-ticket-status
# → Creado PR #4, aprobado y mergeado a main
```

---

## 📝 Convenciones de Commits

El equipo adoptó **Conventional Commits** para todos los commits del repositorio. Este estándar facilita la legibilidad del historial, genera changelogs automáticos e identifica claramente el tipo de cada cambio.

### Prefijos Estándar

| Prefijo | Tipo | Ejemplo |
|---------|------|---------|
| `feat:` | Nueva funcionalidad | `feat: agregar endpoint para listar tickets` |
| `fix:` | Corrección de bug | `fix: corregir timestamp null en excepción` |
| `docs:` | Documentación | `docs: actualizar README con GitFlow` |
| `style:` | Formato de código | `style: formatear imports en TicketController` |
| `refactor:` | Reorganización de código | `refactor: extraer validación a método` |
| `test:` | Tests unitarios/integración | `test: agregar tests para validaciones` |
| `chore:` | Build, dependencias, config | `chore: actualizar pom.xml con H2` |
| `ci:` | Cambios en GitHub Actions | `ci: ajustar workflow para tests` |

### Reglas del Equipo

- ✅ Mensajes en **español** (acorde al contexto académico)
- ✅ Descripción corta: máximo **50 caracteres**
- ✅ Usar **modo imperativo**: "agregar" no "agregué"
- ✅ **Sin punto** al final del asunto
- ✅ Opcionalmente, descripción extendida separada por línea en blanco

### Ejemplos Reales del Proyecto

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

## 📢 Flujo de Pull Requests y Merge

### Ciclo Completo de una Feature

1. **Crear rama y desarrollar** (como se muestra arriba)

2. **Abrir Pull Request en GitHub**
   - Título descriptivo (formato Conventional Commits)
   - Descripción: qué cambios, por qué, cómo testear

3. **Verificaciones automáticas** (GitHub Actions)
   - ✅ Build Maven exitoso
   - ✅ Tests unitarios pasados
   - ✅ Sin errores de compilación

4. **Revisión de código del compañero**
   - Revisar cambios línea por línea
   - Verificar: nombre de variables, tests, documentación
   - Comentarios constructivos en código
   - Solicitar cambios si es necesario

5. **Aprobar y Merge**
   - Mínimo 1 aprobación requerida
   - **Strategy:** Squash and merge (historial limpio en develop)
   - Eliminar rama remota después del merge

### Ciclo Completo de un Hotfix

1. **Crear desde main, hacer fix mínimo** (como se muestra arriba)

2. **Abrir PR urgente hacia main**
   - Prioridad alta en revisión
   - Proceso más rápido

3. **Merge a main + integración en develop**
   - Merge commit (preservar contexto del parche)
   - Cherry-pick o merge posterior en develop

4. **Crear tag de versión** (ej: v1.1.1)

### Reglas Estrictas del Equipo

- ❌ **Nunca** push directo a `main` ni `develop`
- ✅ **Todo** entra vía Pull Request
- ✅ **Aprobación** obligatoria de al menos 1 compañero
- ✅ **GitHub Actions** debe pasar 100%
- ✅ **Squash** para features, **merge commit** para hotfixes

### Checklist de Revisión de Código

Antes de aprobar un PR, el revisor verifica:

- [ ] El código cumple con lo descrito en el título/descripción del PR
- [ ] No hay credenciales, tokens ni contraseñas hardcodeadas
- [ ] Nombres de clases, métodos y variables son descriptivos y en inglés
- [ ] Existe al menos 1 test para la funcionalidad nueva
- [ ] El build de GitHub Actions pasa completamente
- [ ] Los commits siguen Conventional Commits
- [ ] Los endpoints respetan el contrato de API (códigos HTTP correctos)
- [ ] Sin code smells o duplicación innecesaria

---

## 🚀 GitHub Actions — CI/CD Automatizado

### Descripción del Pipeline

Se configuró un **workflow automático de GitHub Actions** que implementa la etapa de **Integración Continua (CI)**. El pipeline valida que cada cambio sea compilable, testeable y respete estándares de código antes de permitir un merge.

### Triggers del Workflow

El workflow `ci.yml` se ejecuta automáticamente en:

- ✅ **Push a `develop`** — Validación de código en desarrollo
- ✅ **Push a `main`** — Validación de código en producción
- ✅ **Pull Request a main/develop** — Validación antes de merge

### Pasos del Pipeline

```
┌─────────────────────────────┐
│  Code Push / PR Created     │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│  1. Checkout Código         │
│     (actions/checkout@v4)   │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│  2. Configurar Java 17      │
│     (setup-java@v4)         │
│     + Maven Cache           │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│  3. Build + Tests           │
│     (mvn clean verify)      │
│     + Compilación           │
│     + Tests Unitarios       │
│     + Tests Integración     │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│  4. Verificar Artefactos    │
│     (buscar JARs generados) │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│  5. Resumen del Pipeline    │
│     Rama, Actor, Evento     │
└────────────┬────────────────┘
             │
      ┌──────┴──────┐
      ▼             ▼
   ✅ PASS      ❌ FAIL
   (Merge OK) (Bloquear Merge)
```

### Rol del Pipeline en DevOps

**CI — Integración Continua:**
- Cada push a `develop` o `main` dispara automáticamente compilación y tests
- Garantiza que ningún cambio llegue a ramas principales sin validación
- Si el build falla, GitHub bloquea el merge del PR hasta su corrección

**CD — Entrega Continua (Proyección futura):**
- En fases posteriores del semestre, se puede extender el pipeline con:
  - Build de imagen Docker
  - Push a Docker Registry
  - Deploy automático a staging o producción
  - Tests de integración en ambientes
  - Notificaciones Slack/email

---

## 📊 Resumen de Buenas Prácticas Implementadas

| Práctica | Implementación | Evaluador |
|----------|-----------------|-----------|
| **Branching Strategy** | GitFlow con justificación | IE1 (15%) |
| **Trabajo Colaborativo** | 3 PRs (2 features + 1 hotfix) | IE2 (15%) |
| **Automatización DevOps** | GitHub Actions CI pipeline | IE3 (25%) |
| **GitHub Actions Config** | Workflow completo en ci.yml | IE4 (25%) |
| **Documentación/Buenas Prácticas** | README + Convenciones + Checklist | IE5 (20%) |

---

## 📁 Estructura del Repositorio

```
.github/
  └── workflows/
      └── ci.yml                    CI/CD pipeline automático

src/
  ├── main/
  │   ├── java/com/example/ticket/
  │   │   ├── controller/            REST endpoints
  │   │   ├── service/               Business logic
  │   │   ├── repository/            Data access
  │   │   ├── model/                 Entidades
  │   │   ├── dto/                   Request/Response
  │   │   ├── event/                 Event listeners
  │   │   └── exception/             Error handling
  │   └── resources/
  │       ├── application.properties  Configuración prod
  │       └── ...
  │
  └── test/
      ├── java/com/example/ticket/   Unit tests
      └── resources/
          └── application-test.properties  Configuración tests

pom.xml                              Maven dependencies
mvnw / mvnw.cmd                      Maven Wrapper
README.md                            Este archivo
```

---

## ✅ Trabajo Colaborativo Realizado

### Features Implementadas (Equipo)

**Feature 1: Validaciones Mejoradas**
```
Rama: feature/add-ticket-validations
Cambios: Validaciones de longitud mínima y patrones de caracteres
PR: Merged a develop ✅
```

**Feature 2: Validación de Formulario**
```
Rama: feature/validacion-formulario
Cambios: Validación de campos obligatorios en DTO
PR: Merged a develop ✅
```

**Feature 3: Endpoint de Listar Tickets**
```
Rama: feature/agregar-endpoint-tickets
Cambios: GET /api/tickets con filtro por estado
PR: Merged a develop ✅
```

### Hotfix Realizado (Crítico)

**Hotfix: Corrección NullPointerException**
```
Rama: hotfix/corregir-null-pointer
Cambios: Manejo correcto de excepciones en consultas
PR: Merged a main ✅
Release: v1.1.1
```

---

## 📚 Recursos Adicionales

- **Documentación Spring Boot:** https://spring.io/projects/spring-boot
- **Guía GitFlow:** https://nvie.com/posts/a-successful-git-branching-model/
- **Conventional Commits:** https://www.conventionalcommits.org/
- **GitHub Actions:** https://docs.github.com/es/actions

---

**Última actualización:** Abril 12, 2026  
**Integrantes:** Pablo Terrazas + Compañero  
**Evaluación:** DevOps DOY0101
