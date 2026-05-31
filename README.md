# Ticket Service — Pipeline CI/CD

---

## Integrantes

| Nombre             | GitHub                                                 |
| ------------------ | ------------------------------------------------------ |
| Pablo Terrazas     | [@PabloTerrazas16](https://github.com/PabloTerrazas16) |
| (Jeremy Pincheira) | [@JereMago](https://github.com/JereMago)               |

---

## Descripción del microservicio

API REST desarrollada en **Spring Boot 3.5 + Java 17** para la gestión de tickets de soporte. Expone dos endpoints principales:

| Método | Endpoint                   | Descripción                      |
| ------ | -------------------------- | -------------------------------- |
| `POST` | `/api/tickets`             | Registrar un nuevo ticket        |
| `GET`  | `/api/tickets/{id}/status` | Consultar el estado de un ticket |

**Stack tecnológico:** Spring Boot · Spring Data JPA · MySQL 8 · H2 (tests) · Lombok · JaCoCo

---

## Estructura del repositorio

```
ticket/
├── .github/
│   ├── workflows/
│   │   └── ci-cd.yml          # Pipeline GitHub Actions
│   └── dependabot.yml         # Escaneo automático de dependencias
├── k8s/
│   ├── deployment.yml         # Deployment con 2 réplicas y RollingUpdate
│   ├── service.yml            # ClusterIP service
│   ├── hpa.yml                # HorizontalPodAutoscaler (2-5 pods)
│   └── secrets.yml            # Credenciales de base de datos
├── src/
│   ├── main/
│   │   ├── java/com/example/ticket/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
│       ├── java/com/example/ticket/
│       └── resources/application-test.properties
├── Dockerfile                 # Multi-stage build
├── docker-compose.yml         # Orquestación local (staging)
└── pom.xml
```

---

## Contenedores

El microservicio está contenerizado usando un **Dockerfile multi-stage** que separa la etapa de compilación de la imagen final, reduciendo el tamaño y la superficie de ataque:

```
Etapa 1 (builder): maven:3.9.8-eclipse-temurin-17
  └── Compila el proyecto con Maven

Etapa 2 (runtime): eclipse-temurin:17-jre-alpine
  └── Solo contiene el JAR compilado
  └── Usuario no-root (ticket:ticket)
  └── HEALTHCHECK integrado
```

**Características de seguridad del contenedor:**

- Imagen base mínima Alpine (menor superficie de ataque)
- Usuario no-root (`runAsUser: 1001`)
- Sin escalación de privilegios
- Límites de memoria: 256Mi request / 512Mi limit

**Construir y correr la imagen localmente:**

```bash
docker build -t ticket-service .
docker run -p 8080:8080 ticket-service
```

---

## Pruebas Automatizadas

Las pruebas se ejecutan automáticamente en cada push y Pull Request mediante el job `test` del pipeline. Se usa **H2 en memoria** para el perfil de test, sin necesidad de MySQL.

**Cobertura de pruebas (JUnit 5 + JaCoCo):**

| Suite                    | Tests  | Descripción                    |
| ------------------------ | ------ | ------------------------------ |
| `TicketRepositoryTest`   | 9      | CRUD, búsquedas, conteo con H2 |
| `TicketServiceImplTest`  | 8      | Lógica de negocio con mocks    |
| `TicketControllerTest`   | 9      | Endpoints HTTP con MockMvc     |
| `TicketTest`             | 7      | Validaciones del modelo        |
| `TicketApplicationTests` | 1      | Carga del contexto Spring      |
| **Total**                | **34** |                                |

**Ejecutar pruebas localmente:**

```bash
./mvnw test -Dspring.profiles.active=test
```

Los resultados se publican como artefacto en GitHub Actions (`test-results/`) para revisión posterior.

---

## Seguridad y Escalabilidad

### Análisis de seguridad en el pipeline

El pipeline implementa dos capas de escaneo que **bloquean el despliegue** si detectan vulnerabilidades:

**1. Snyk — Escaneo de dependencias Maven**

```yaml
snyk test --severity-threshold=high --all-projects
# continue-on-error: false → bloquea el pipeline si encuentra HIGH o CRITICAL
```

**2. Trivy — Escaneo de imagen Docker**

```yaml
# Escanea la imagen construida antes de publicarla
severity: CRITICAL,HIGH
exit-code: 1 # bloquea si encuentra vulnerabilidades
```

**3. Dependabot — Actualizaciones automáticas**

Configurado para revisar semanalmente (lunes) tres ecosistemas:

| Ecosistema                | Hora  | PRs máx |
| ------------------------- | ----- | ------- |
| Maven (dependencias Java) | 02:00 | 5       |
| GitHub Actions            | 02:30 | 3       |
| Docker                    | 03:00 | 2       |

### Escalabilidad en Kubernetes

El HPA escala automáticamente entre 2 y 5 réplicas según métricas de uso:

```yaml
minReplicas: 2
maxReplicas: 5
cpu: target 70% de utilización
memory: target 80% de utilización
```

---

## Despliegue Automático y Trazabilidad

### Pipeline completo (GitHub Actions)

El pipeline se ejecuta en cada push a `main` o `develop` y en cada Pull Request a `main`. Los jobs se ejecutan en secuencia con dependencias explícitas:

```
test → security → build → deploy
```

| Job        | Qué hace                                                                    |
| ---------- | --------------------------------------------------------------------------- |
| `test`     | Ejecuta 34 pruebas JUnit con perfil H2                                      |
| `security` | Snyk sobre pom.xml, bloquea si hay vulnerabilidades HIGH                    |
| `build`    | Empaqueta con Maven, construye imagen Docker, push a GHCR, Trivy scan       |
| `deploy`   | Docker Compose staging, validación de health, kubectl dry-run, trazabilidad |

### Trazabilidad del despliegue

Cada ejecución del pipeline imprime un resumen de trazabilidad completo:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Trazabilidad del despliegue
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Commit SHA   : abc1234...
Branch       : main
Autor        : PabloTerrazas16, JereMago
Timestamp    : 2025-06-01T12:00:00Z
Workflow Run : 123456789
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

Esto garantiza que cada despliegue pueda rastrearse desde el commit original hasta la imagen publicada en GHCR.

### Configuración de secrets necesarios

Para ejecutar el pipeline es necesario configurar los siguientes secrets en GitHub (Settings → Secrets → Actions):

| Secret         | Descripción                             | Cómo obtenerlo                                        |
| -------------- | --------------------------------------- | ----------------------------------------------------- |
| `SNYK_TOKEN`   | Token de autenticación Snyk             | [app.snyk.io](https://app.snyk.io) → Account Settings |
| `GITHUB_TOKEN` | Automático, provisto por GitHub Actions | No requiere configuración                             |

---

## Orquestación de Contenedores

### Docker Compose (staging local)

Levanta el entorno completo con un solo comando:

```bash
docker compose up -d
```

**Servicios:**

| Servicio         | Imagen                   | Puerto    | Descripción                    |
| ---------------- | ------------------------ | --------- | ------------------------------ |
| `mysql`          | mysql:8.0                | 3307:3306 | Base de datos principal        |
| `ticket-service` | build local              | 9080:8080 | Microservicio                  |
| `phpmyadmin`     | phpmyadmin:latest-alpine | 8081:80   | Admin DB (solo perfil `debug`) |

**Verificar que el servicio está saludable:**

```bash
curl http://localhost:9080/api/actuator/health
```

**Detener el entorno:**

```bash
docker compose down
```

### Kubernetes (producción)

La carpeta `k8s/` contiene los manifiestos para desplegar en cualquier cluster Kubernetes:

```bash
# Aplicar todos los manifiestos
kubectl apply -f k8s/

# Verificar el estado
kubectl get pods
kubectl get hpa
```

**Características de la configuración K8s:**

- **2 réplicas mínimas** para alta disponibilidad
- **RollingUpdate** con `maxUnavailable: 0` — zero-downtime deployment
- **topologySpreadConstraints** — distribuye pods en distintos nodos
- **Readiness y Liveness probes** — Kubernetes reinicia pods no saludables automáticamente
- **Secrets** — credenciales de BD no expuestas en el Deployment
- **Resource limits** — evita que un pod consuma recursos de otros

---

## Cómo ejecutar el proyecto localmente

**Prerrequisitos:** Docker Desktop, Java 17, Maven

```bash
# 1. Clonar el repositorio
git clone https://github.com/PabloTerrazas16/ticket.git
cd ticket

# 2. Levantar con Docker Compose
docker compose up -d

# 3. Verificar salud del servicio
curl http://localhost:9080/api/actuator/health

# 4. Crear un ticket de prueba
curl -X POST http://localhost:9080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"title": "Error en login", "description": "No puedo acceder", "status": "OPEN"}'

# 5. Consultar estado del ticket
curl http://localhost:9080/api/tickets/1/status
```

## Estrategia de Branching

Se utilizó **GitHub Flow**:

- `main` — rama protegida, siempre estable y con pipeline en verde
- `feature/*` — una rama por cada componente desarrollado

Cada feature fue integrada mediante **Pull Request**, lo que permite ver el historial completo de trazabilidad del desarrollo en GitHub.
