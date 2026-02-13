# vg-ms-claims-incidents

Microservicio para la gestión de quejas (complaints) e incidentes de infraestructura en el sistema de gestión de agua.

## Descripción

Este microservicio forma parte del ecosistema JASS (Juntas Administradoras de Servicios de Saneamiento) y se encarga de:

- **Quejas (Complaints)**: Gestión de reclamos de clientes sobre el servicio de agua
- **Incidentes (Incidents)**: Gestión de incidentes técnicos de infraestructura
- **Tipos de Incidentes**: Catálogo de tipos de incidentes
- **Resoluciones**: Registro de resoluciones de incidentes

## Tecnologías

- Java 17
- Spring Boot 3.x (WebFlux - Reactivo)
- MongoDB (Reactive)
- RabbitMQ (Mensajería)
- Docker

## Arquitectura

El proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)**:

```
src/main/java/pe/edu/vallegrande/vgmsclaims/
├── domain/           → Lógica de negocio pura
│   ├── models/       → Entidades y Value Objects
│   ├── ports/        → Interfaces (in/out)
│   ├── services/     → Servicios de dominio
│   └── exceptions/   → Excepciones de dominio
├── application/      → Casos de uso
│   ├── usecases/     → Implementaciones
│   ├── dto/          → Request/Response DTOs
│   ├── mappers/      → Conversores
│   └── events/       → Eventos de dominio
└── infrastructure/   → Adaptadores externos
    ├── adapters/     → REST, Persistence, Messaging
    ├── persistence/  → Documents y Repositories
    ├── security/     → Seguridad (Headers Gateway)
    └── config/       → Configuraciones
```

## Endpoints

### Incidentes (`/api/admin/incidents`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todos los incidentes |
| GET | `/{id}` | Obtener incidente por ID |
| POST | `/` | Crear nuevo incidente |
| PUT | `/{id}` | Actualizar incidente |
| DELETE | `/{id}` | Eliminar incidente (lógico) |
| PATCH | `/{id}/restore` | Restaurar incidente |
| GET | `/active` | Listar incidentes activos |
| GET | `/inactive` | Listar incidentes inactivos |
| POST | `/{id}/assign` | Asignar incidente a técnico |
| POST | `/{id}/resolve` | Resolver incidente |
| POST | `/{id}/close` | Cerrar incidente |
| GET | `/organization/{orgId}` | Por organización |
| GET | `/zone/{zoneId}` | Por zona |
| GET | `/severity/{severity}` | Por severidad |
| GET | `/status/{status}` | Por estado |

### Tipos de Incidentes (`/api/admin/incident-types`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todos los tipos |
| GET | `/{id}` | Obtener tipo por ID |
| POST | `/` | Crear nuevo tipo |
| PUT | `/{id}` | Actualizar tipo |
| DELETE | `/{id}` | Eliminar tipo (lógico) |
| PATCH | `/{id}/restore` | Restaurar tipo |
| GET | `/active` | Listar tipos activos |
| GET | `/inactive` | Listar tipos inactivos |

### Resoluciones de Incidentes (`/api/admin/incident-resolutions`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todas las resoluciones |
| GET | `/{id}` | Obtener resolución por ID |
| GET | `/incident/{incidentId}` | Por incidente |
| POST | `/` | Crear nueva resolución |
| PUT | `/{id}` | Actualizar resolución |
| DELETE | `/{id}` | Eliminar resolución |

### Quejas (`/api/complaints`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Listar todas las quejas |
| GET | `/{id}` | Obtener queja por ID |
| POST | `/` | Crear nueva queja |
| PUT | `/{id}` | Actualizar queja |
| POST | `/{id}/close` | Cerrar queja |
| GET | `/organization/{orgId}` | Por organización |
| GET | `/user/{userId}` | Por usuario |
| GET | `/status/{status}` | Por estado |

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Puerto del servidor | `8089` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` |
| `SPRING_DATA_MONGODB_URL` | URI de MongoDB | - |
| `SPRING_DATA_MONGODB_DATABASE` | Base de datos | `claims-incidents` |

## Ejecución Local

### Requisitos
- Java 17+
- Maven 3.8+
- MongoDB

### Comandos

```bash
# Compilar
./mvnw clean package

# Ejecutar
./mvnw spring-boot:run

# Ejecutar con perfil específico
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

## Docker

```bash
# Construir imagen
docker build -t vg-ms-claims-incidents .

# Ejecutar contenedor
docker run -p 8089:8089 vg-ms-claims-incidents
```

## Swagger/OpenAPI

Documentación disponible en:
- Swagger UI: `http://localhost:8089/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8089/v3/api-docs`

## Colecciones MongoDB

- `incidents` - Incidentes
- `incident_types` - Tipos de incidentes
- `incident_resolutions` - Resoluciones
- `complaints` - Quejas
- `complaint_categories` - Categorías de quejas
- `complaint_responses` - Respuestas a quejas

## Eventos (RabbitMQ)

El microservicio publica los siguientes eventos:

- `IncidentCreatedEvent`
- `IncidentAssignedEvent`
- `IncidentUpdatedEvent`
- `IncidentResolvedEvent`
- `IncidentClosedEvent`
- `UrgentIncidentAlertEvent`
- `ComplaintCreatedEvent`
- `ComplaintUpdatedEvent`
- `ComplaintClosedEvent`

## Licencia

Proyecto académico - Valle Grande
