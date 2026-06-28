# Ticket Service — Pipeline CI/CD & Observabilidad

---

## Integrantes

| Nombre           | GitHub                                                  |
| ---------------- | ------------------------------------------------------- |
| Pablo Terrazas   | [@PabloTerrazas16](https://github.com/PabloTerrazas16) |
| Jeremy Pincheira | [@JereMago](https://github.com/JereMago)                |

---

## Descripción del microservicio

API REST desarrollada en **Spring Boot 3.5 + Java 17** para la gestión de tickets de soporte. Expone dos endpoints principales:

| Método | Endpoint                    | Descripción                      |
| ------ | --------------------------- | -------------------------------- |
| `POST` | `/api/tickets`              | Registrar un nuevo ticket        |
| `GET`  | `/api/tickets/{id}/status`  | Consultar el estado de un ticket |

**Stack tecnológico:** Spring Boot · Spring Data JPA · MySQL 8 · H2 (tests) · Lombok · JaCoCo · SonarQube Cloud · Prometheus · Grafana

---

## Estructura del repositorio

```
ticket/
├── .github/
│   ├── workflows/
│   │   └── ci-cd.yml          # Pipeline GitHub Actions (CI/CD + SonarQube + Trivy)
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
├── docker-compose.yml         # Orquestación local (Staging, Métricas y Grafana)
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
- Imagen base mínima Alpine (menor superficie de ataque).
- Usuario no-root (`runAsUser: 1001`).
- Sin escalación de privilegios.
- Límites de memoria: 256Mi request / 512Mi limit.

**Construir y correr la imagen localmente:**

```bash
docker build -t ticket-service .
docker run -p 8080:8080 ticket-service
```

---

## Orquestación de Contenedores (Docker Compose)

Para el ambiente de staging local y la infraestructura de telemetría, se utiliza Docker Compose para levantar la pila completa de servicios interconectados en una red aislada.

```bash
docker compose up -d
```

**Servicios activos en la pila:**

| Servicio            | Imagen                  | Puerto      | Descripción                                              |
| ------------------- | ----------------------- | ----------- | -------------------------------------------------------- |
| mysql               | mysql:8.0               | 3307:3306   | Base de datos relacional principal.                      |
| app (ticket-service)| Build local             | 9080:8080   | Microservicio Spring Boot expuesto.                      |
| prometheus-ticket   | prom/prometheus:latest  | 9090:9090   | Servidor de scraping de métricas del Actuator.           |
| grafana-ticket      | grafana/grafana:latest  | 3000:3000   | Panel visual de analíticas y dashboards.                 |

> **Nota de arquitectura:** Todos los servicios se encuentran sincronizados y respondiendo saludablemente (Up), garantizando que la recolección de métricas no pierda continuidad.

---
<img width="1044" height="248" alt="6" src="https://github.com/user-attachments/assets/034b5e12-0b7c-4b74-bbfa-17c3fadcb574" />
<img width="1916" height="1033" alt="5" src="https://github.com/user-attachments/assets/f67b172a-7345-47c5-a4fe-e8eba7775e41" />


## 📊 Observabilidad y Monitoreo (Prometheus & Grafana)

Implementamos una solución de monitoreo en tiempo real recolectando las métricas nativas expuestas por Spring Boot Actuator y normalizadas mediante Micrometer.

### 📡 Indexación de Prometheus en Grafana
<img width="1867" height="994" alt="7" src="https://github.com/user-attachments/assets/58856074-1494-448f-a773-41cae4bd6ebe" />

La comunicación entre componentes se realiza de forma interna. Prometheus está configurado como el origen de datos (Data Source) principal por defecto dentro de la interfaz de Grafana.

### 📈 Panel de Disponibilidad (Uptime Panel)
<img width="1865" height="905" alt="9" src="https://github.com/user-attachments/assets/fb416eda-d913-4ddd-99a8-9e500697f407" />
<img width="1861" height="907" alt="8" src="https://github.com/user-attachments/assets/aedff441-b559-40ed-a337-f3c7e66f8672" />

Monitoreo continuo utilizando expresiones PromQL (`up`) para verificar de forma constante que las instancias de infraestructura responden con estados de activación normales.

### ☕ Métricas de la Máquina Virtual de Java (JVM Core)
<img width="1375" height="721" alt="10" src="https://github.com/user-attachments/assets/7aaa2a7c-eb42-4bf5-84fd-338532228b5e" />

Visualización avanzada mediante un Dashboard dedicado que permite inspeccionar la asignación dinámica de memoria Heap y Non-Heap, el comportamiento de los hilos de ejecución y las pausas del Garbage Collector bajo cargas transaccionales.

---

## 🧪 Pruebas Automatizadas e Integración Continua

Las pruebas se ejecutan automáticamente en cada push y Pull Request mediante el pipeline de GitHub Actions. Se usa H2 en memoria para el perfil de test, garantizando el aislamiento de la base de datos MySQL de producción.

**Cobertura de pruebas (JUnit 5 + JaCoCo):**

| Suite                   | Tests | Descripción                               |
| ----------------------- | ----- | ----------------------------------------- |
| TicketRepositoryTest    | 9     | CRUD, búsquedas, conteo con H2            |
| TicketServiceImplTest   | 8     | Lógica de negocio con mocks               |
| TicketControllerTest    | 9     | Endpoints HTTP con MockMvc                |
| TicketTest              | 7     | Validaciones del modelo                   |
| TicketApplicationTests  | 1     | Carga del contexto Spring                 |
| **Total**               | **34**|                                           |

**Ejecutar pruebas localmente:**

```bash
./mvnw test -Dspring.profiles.active=test
```

###  Validación de Pruebas Unitarias (Fallas en CI)
<img width="1441" height="612" alt="4" src="https://github.com/user-attachments/assets/00a0c29c-af84-45c2-80ee-8100ae0b573c" />
<img width="1408" height="763" alt="3" src="https://github.com/user-attachments/assets/bb916da6-ca68-43f1-8430-9176a26521a0" />
![Uploading 10.png…]()


El pipeline cuenta con un mecanismo de protección que detiene el flujo de construcción inmediatamente si algún test de arquitectura o de negocio falla, notificando el error exacto en la terminal.

En este escenario de prueba, el pipeline bloqueó de forma correcta el flujo debido a una aserción errónea controlada en `TicketController`.

### 🟢 Quality Gate Exitoso (Pipeline en Verde)
<img width="716" height="175" alt="2" src="https://github.com/user-attachments/assets/83b6b062-4674-4538-b61c-701586ac8bfb" />



Una vez resueltos los conflictos de código y pruebas, los flujos automatizados de GitHub Actions y los umbrales de calidad se marcan en verde, autorizando el empaquetado seguro.

---

## 🛡️ Seguridad y Calidad de Código (Análisis Estático)

### 📊 Análisis con SonarQube Cloud

El código fuente es evaluado bajo los estándares de SonarQube Cloud para prevenir vulnerabilidades prematuras, bugs y mitigar la deuda técnica (Code Smells).

**Panel de Control del Proyecto (Quality Gate Aprobado):** el proyecto cuenta con calificaciones máximas (Clase A) en todos los vectores críticos de desarrollo seguro y un 0% de duplicación.
![Uploading 3.png…]()

<img width="1872" height="903" alt="1" src="https://github.com/user-attachments/assets/814b646c-3de2-4c2f-a9dd-63d3afb4d6bc" />

### Restricciones de Seguridad de la Organización (Políticas de PR)

>  **Nota de cumplimiento:** Por motivos de políticas estrictas de seguridad de datos de la organización, las peticiones Pull Request o ramas de desarrollo temporales que apunten a entornos secundarios cuentan con directivas nativas de restricción de lectura de datos cruzados para mitigar la fuga de información de la estructura del repositorio.
<img width="1848" height="890" alt="11" src="https://github.com/user-attachments/assets/3031bfdc-5823-478a-be9d-a96ab84dad1b" />


### 🔍 Capas de Escaneo Adicionales en el Pipeline

El flujo automatizado implementa dos capas extra que bloquean el despliegue si detectan fallas críticas:

**1. Snyk — Escaneo de dependencias Maven**

```yaml
snyk test --severity-threshold=high --all-projects
# continue-on-error: false → bloquea el pipeline si encuentra vulnerabilidades HIGH o CRITICAL
```

**2. Trivy — Escaneo de la imagen Docker**

```yaml
uses: aquasecurity/trivy-action@master
with:
  image-ref: 'ghcr.io/pabloterrazas16/ticket/ticket-service:latest'
  severity: CRITICAL,HIGH
  exit-code: 1 # Bloquea si encuentra vulnerabilidades críticas en el runtime Alpine
```

**3. Dependabot — Actualizaciones automáticas**

Revisa semanalmente (lunes) tres ecosistemas del repositorio:

- **Maven:** Dependencias de Java (Máx. 5 PRs).
- **GitHub Actions:** Versiones de los componentes del pipeline (Máx. 3 PRs).
- **Docker:** Versión de las imágenes base del multi-stage (Máx. 2 PRs).

---

## Escalabilidad y Despliegue en Kubernetes

### Escalabilidad Automática

El archivo `k8s/hpa.yml` (HorizontalPodAutoscaler) escala automáticamente entre 2 y 5 réplicas según las métricas de uso de los Pods:

- `minReplicas: 2`
- `maxReplicas: 5`
- **Métricas Objetivo:** Target del 70% de utilización de CPU y 80% de utilización de Memoria.

### Características de la Configuración K8s (Producción)

- **Alta disponibilidad:** Mínimo 2 réplicas obligatorias distribuidas.
- **RollingUpdate:** Despliegues con `maxUnavailable: 0` garantizando zero-downtime.
- **Readiness y Liveness probes:** Monitoreo de salud para reinicios automatizados de pods corruptos.
- **Seguridad de credenciales:** Uso nativo de objetos `Secret` para aislar las variables de entorno de la base de datos.

---

## Trazabilidad del Despliegue

Cada ejecución del pipeline genera un bloque de metadatos auditable dentro de los logs del flujo para asegurar el rastreo de extremo a extremo:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔍 Trazabilidad del despliegue
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Commit SHA    : 47b0dbb665f67773b6846882be41c3adda6eb790
Branch        : main
Autor         : PabloTerrazas16, JereMago
Timestamp     : 2026-06-28T09:15:17Z
Workflow Run  : 123456789
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Configuración de Secrets necesarios en GitHub

Para el correcto funcionamiento de este flujo automatizado, se deben registrar las siguientes variables en **Settings → Secrets and variables → Actions:**

| Secret         | Descripción                               | Origen / Cómo obtenerlo                              |
| -------------- | ----------------------------------------- | ---------------------------------------------------- |
| `SNYK_TOKEN`   | Token de autenticación de Snyk            | app.snyk.io → Account Settings                       |
| `SONAR_TOKEN`  | Token de vinculación con SonarCloud       | Generado en el perfil de SonarQube Cloud             |
| `GITHUB_TOKEN` | Token provisto nativamente por GitHub     | Autogenerado por el sistema, no requiere configuración manual |

---

## Estrategia de Branching

Se utilizó rigurosamente **GitHub Flow:**

- **`main`:** Rama protegida, siempre en estado estable y con pipeline en verde. Representa el código en producción.
- **`feature/*`:** Ramas independientes y temporales abiertas por cada componente o integración desarrollada.
- **`develop/*`:** Entorno de integración donde convergen las nuevas características antes del despliegue final.
- **`feature/add-prometheus-monitoring y feature/observability*`:** Ramas dedicadas exclusivamente al diseño, pruebas y estabilización del stack de telemetría (Prometheus/Grafana).
- **`dependabot/*`:** Ramas creadas automáticamente por el ecosistema de seguridad de GitHub para mantener actualizadas las dependencias críticas de Maven y Docker de forma segura.

Cada nueva característica técnica o corrección de bugs fue integrada mediante **Pull Requests (PR)**, asegurando la revisión de código por pares y la ejecución obligatoria del pipeline antes del merge.



## Reflexión Final y Conclusión
Pablo Terrazas: Bueno... durante todo el proceso de las 3 entregas, mi foco principal estuvo orientado a la construcción, automatización y blindaje del pipeline del CI/CD, así como a la resolución de fallas complejas durante la etapa de compilación en GitHub Actions (rabié mucho con esto, jaja). Logré entender que herramientas como Snyk, Trivy, SonarQube Cloud, Grafana y Prometheus (sin contar todos los servicios de AWS) no solo analizaban el código, sino que actuaban como "compuertas de calidad" estrictas, y vaya que cambió por completo mi perspectiva sobre el desarrollo de software.
En su momento pensé que sólo era código, pero poco a poco comprendí que era necesario aprender estas herramientas, y sobretodo tener buenas prácticas en el desarrollo de software, tales como la estrategia de Branching que, aunque no la entendía al principio, supe que era algo necesario y hasta da gusto (y algo de pereza también... jeje) crear nuevas ramas y luego mergearlas a la rama develop, que en mi caso, fue donde mergeaba todo.
Aprendí que el concepto de Fail-Fast no era solo una frase teórica, porque cuando un test falla en el controlador o el escáner detecta un problema de seguridad crítico, el pipeline protege el entorno productivo de forma autónoma. Muchas veces me quedé hasta tarde intentando solucionar un problema con el pipeline cuando la mayoria de veces era un problema de identación en los YML... o incluso un conflicto de versiones... y tuve que volver a poner "docker compose down" y "docker compose up" un montón de veces, pero en fin. Fue una experiencia compleja y todo esto es muy necesario para el desarrollo de software.




