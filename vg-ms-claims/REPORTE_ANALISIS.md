# REPORTE DE ANÁLISIS — vg-ms-claims-incidents (ms-prs1)

> **Fecha:** Junio 2025
> **Comparado contra:** `new-ms-claims-incidents/` READMEs + `00_ESTRUCTURA_DETALLADA.md`
> **Resultado general:** ❌ **NO CONFORME** — Requiere correcciones significativas en múltiples áreas

---

## RESUMEN EJECUTIVO

| Categoría | Estado | Hallazgos |
|---|---|---|
| pom.xml | ❌ CRÍTICO | Spring Boot 3.2.11/*Java 17*, dependencias incorrectas |
| Dockerfile | ❌ CRÍTICO | Java 17, JLink sobre-ingenierizado, 3 etapas |
| application.yml | ❌ MAYOR | Bloques no estándar (jackson, app, server extras) |
| application-dev.yml | ❌ CRÍTICO | MongoDB Atlas con credenciales hardcoded, JWT innecesario |
| application-prod.yml | ⚠️ MENOR | JWT innecesario, server.error extra |
| SecurityConfig | ❌ MAYOR | Sin perfiles dev/prod, métodos extra |
| WebClientConfig | ❌ MAYOR | Sin HttpClient Netty con timeouts |
| RabbitMQConfig | ❌ CRÍTICO | Exchange `claims-exchange` en vez de `jass.events` |
| Resilience4jConfig | ❌ MAYOR | Config Java en vez de YAML, usa Spring Cloud wrapper |
| Controllers (paths) | ❌ CRÍTICO | `/api/admin/` y `/api/complaints` en vez de `/api/v1/` |
| Controllers (hexagonal) | ❌ CRÍTICO | IncidentTypeRest e IncidentResolutionRest usan Documents/Repositories directamente |
| IncidentRest (hexagonal) | ⚠️ MAYOR | delete/restore/active/inactive usan IncidentMongoRepository directamente |
| Controller faltante | ❌ CRÍTICO | `ComplaintCategoryRest` NO EXISTE |
| GatewayHeadersExtractor | ⚠️ MENOR | Diferente patrón (Optional vs Mono<GatewayHeaders>) |
| docker-compose.yml | ⚠️ MENOR | CORS, JAVA_OPTS, target inexistente |
| Idioma (español) | ❌ MAYOR | Todos los logs, Swagger y comentarios en español |
| Puerto | ❌ MAYOR | 8089 (conflicto con infrastructure) → debe ser **8085** |

**Total de hallazgos: 43 problemas** (15 críticos, 16 mayores, 12 menores)

---

## 1. POM.XML — ❌ CRÍTICO

### 1.1 Spring Boot Version

```
ACTUAL:    3.2.11
ESPERADO:  3.5.0  (o la versión más reciente estable, el README dice 3.5.10)
```

### 1.2 Java Version

```
ACTUAL:    17
ESPERADO:  21
```

### 1.3 Dependencia INCORRECTA: spring-cloud-starter-gateway

```xml
<!-- ❌ ELIMINAR — Esta dependencia es para el API Gateway, NO para un microservicio normal -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
    ...
</dependency>
```

**Impacto:** Trae un WebFilter de Gateway que puede interferir con WebFlux. Es una dependencia que solo debe tener `vg-ms-gateway`.

### 1.4 Dependencias de Micrometer INNECESARIAS

```xml
<!-- ❌ ELIMINAR — No están en el estándar -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
```

### 1.5 Springdoc Version

```
ACTUAL:    2.3.0
ESPERADO:  2.8.4  (o versión actual estable)
```

### 1.6 Springdoc duplicado

```xml
<!-- ❌ ELIMINAR — Solo se necesita springdoc-openapi-starter-webflux-ui (ya incluye api) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-api</artifactId>
</dependency>
```

### 1.7 Lombok scope incorrecto

```
ACTUAL:    <scope>provided</scope> con <version>${lombok.version}</version>
ESPERADO:  <optional>true</optional> (sin version explícita, usar la del parent)
```

### 1.8 jakarta.validation-api REDUNDANTE

```xml
<!-- ❌ ELIMINAR — Ya viene incluido con spring-boot-starter-validation -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>
```

### 1.9 Resilience4j dependencia INCORRECTA

```
ACTUAL:    spring-cloud-starter-circuitbreaker-reactor-resilience4j (Spring Cloud wrapper)
ESPERADO:  resilience4j-spring-boot3 + resilience4j-reactor (directo)
```

### 1.10 Spring Cloud BOM INNECESARIO

```xml
<!-- ❌ ELIMINAR todo el bloque dependencyManagement de spring-cloud -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            ...
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 1.11 Lombok en annotationProcessorPaths

```xml
<!-- ❌ ELIMINAR maven-compiler-plugin con annotationProcessorPaths -->
<!-- Lombok ya se procesa automáticamente con scope optional -->
```

### 1.12 Descripción genérica

```
ACTUAL:    "Demo project for Spring Boot"
ESPERADO:  "Claims and incidents management microservice"
```

---

## 2. DOCKERFILE — ❌ CRÍTICO (Reescritura completa)

### Actual (108 líneas, 3 etapas, JLink, JVM flags extensivos)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS deps     # ❌ Java 17
# ... etapa de cache de dependencias ...
FROM eclipse-temurin:17-jdk-alpine AS builder   # ❌ Java 17
# ... JLink custom JRE con 20+ módulos ...
FROM alpine:latest                              # ❌ alpine base sin JRE
# ... instala gcompat, libstdc++, curl ...
# ... usuario spring, 30+ JVM flags en ENTRYPOINT ...
```

### Esperado (estándar, 9 líneas)

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Problemas específicos

| # | Problema | Impacto |
|---|---|---|
| 2.1 | Java 17 en vez de 21 | CRÍTICO |
| 2.2 | 3 etapas (deps, builder, runtime) en vez de 2 | Sobre-ingenierizado |
| 2.3 | JLink custom JRE | No estándar, frágil, difícil de mantener |
| 2.4 | alpine:latest como base (sin JRE incluido) | Requiere JRE copiado manualmente |
| 2.5 | Usuario no-root spring | No en estándar (buena práctica pero no requerido) |
| 2.6 | 30+ JVM flags en ENTRYPOINT | Sobre-ingenierizado, dificulta debugging |
| 2.7 | EXPOSE 8089 | Debe ser 8085 |
| 2.8 | JVM flags deshabilitan métricas y health | Conflicto con Actuator |

---

## 3. APPLICATION.YML — ❌ MAYOR

### 3.1 Bloque `jackson` — ELIMINAR

```yaml
# ❌ No está en el estándar — ELIMINAR TODO el bloque
jackson:
    date-format: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    time-zone: UTC
    serialization: ...
    deserialization: ...
    default-property-inclusion: non_null
```

### 3.2 Bloque `app:` — ELIMINAR

```yaml
# ❌ No está en el estándar — ELIMINAR TODO el bloque
app:
  name: Claims Incidents Microservice
  description: Microservicio para gestión de reclamos e incidentes
  version: 1.0.0
  data:
    initialize: false
```

### 3.3 Puerto incorrecto

```
ACTUAL:    8089 (conflicta con vg-ms-infrastructure)
ESPERADO:  8085
```

### 3.4 Server extras — ELIMINAR

```yaml
# ❌ No están en el estándar — ELIMINAR
server:
  address: 0.0.0.0         # ❌ ELIMINAR
  compression:              # ❌ ELIMINAR todo el bloque
    enabled: true
    mime-types: ...
  http2:                    # ❌ ELIMINAR todo el bloque
    enabled: true
```

### 3.5 Springdoc — Simplificar

```yaml
# ❌ Tiene 8 configuraciones extra que no están en el estándar
# ELIMINAR: tags-sorter, display-request-duration, disable-swagger-default-url,
#           doc-expansion, filter, default-produces-media-type, show-actuator, webjars
```

**ACTUAL (excesivo):**

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha                    # ❌ ELIMINAR
    display-request-duration: true        # ❌ ELIMINAR
    disable-swagger-default-url: true     # ❌ ELIMINAR
    doc-expansion: none                   # ❌ ELIMINAR
    filter: false                         # ❌ ELIMINAR
  default-produces-media-type: ...        # ❌ ELIMINAR
  paths-to-match: /admin/**, /client/**   # ❌ CAMBIAR a /api/v1/**
  show-actuator: false                    # ❌ ELIMINAR
  webjars:                                # ❌ ELIMINAR
    prefix: /webjars
```

**ESPERADO:**

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true
    operations-sorter: method
```

### 3.6 Management/Actuator — Simplificar

```yaml
# ❌ Tiene prometheus, metrics distribution, tags extra
# ESPERADO solo:
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 3.7 Falta `spring.lifecycle`

```yaml
# ❌ FALTA en base (solo está en prod)
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

---

## 4. APPLICATION-DEV.YML — ❌ CRÍTICO

### 4.1 MongoDB — Credenciales hardcoded en Atlas

```yaml
# ❌ CRÍTICO — Credenciales en texto plano, apunta a Atlas en vez de localhost
ACTUAL:   mongodb+srv://santiago:santiago19@cluster0.b7mgq.mongodb.net/...
ESPERADO: mongodb://localhost:27017/db_jass_claims
```

### 4.2 Database name incorrecto

```
ACTUAL:    claims-incidents
ESPERADO:  db_jass_claims
```

### 4.3 External services — Patrón incorrecto

```yaml
# ❌ Usa app.external.user-service (custom)
ACTUAL:
  app:
    external:
      user-service:
        base-url: https://lab.vallegrande.edu.pe/jass/ms-gateway

# ✅ Debe usar services.xxx.url (estándar)
ESPERADO:
  services:
    users:
      url: http://localhost:8081
    organizations:
      url: http://localhost:8083
```

### 4.4 JWT Config — ELIMINAR

```yaml
# ❌ ELIMINAR — La autenticación la maneja el Gateway, no cada microservicio
security:
  jwt:
    private-key: ...
    public-key: ...
    expiration: ...
```

### 4.5 Logging — Excesivo con file output

```yaml
# ❌ ELIMINAR pattern personalizado y file logging
# ❌ ELIMINAR logging.file completo
# Estándar solo usa niveles básicos
```

### 4.6 Falta RabbitMQ config

```yaml
# ❌ FALTA COMPLETAMENTE — Debe tener:
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 4.7 Falta Resilience4j YAML

```yaml
# ❌ FALTA COMPLETAMENTE — Debe estar en YAML, no en Java class
resilience4j:
  circuitbreaker:
    instances:
      usersService:
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
      organizationsService:
        failureRateThreshold: 50
        ...
  retry:
    instances:
      usersService:
        maxAttempts: 3
        waitDuration: 500ms
      organizationsService:
        maxAttempts: 3
        waitDuration: 500ms
```

### 4.8 Falta WebClient timeouts YAML

```yaml
# ❌ FALTA — Debe tener:
webclient:
  timeout:
    connect: 5000
    read: 10000
    write: 10000
```

---

## 5. APPLICATION-PROD.YML — ⚠️ MENOR

### 5.1 JWT Config — ELIMINAR (mismo que dev)

### 5.2 `server.error` — ELIMINAR (no está en el estándar)

```yaml
# ❌ ELIMINAR
server:
  error:
    include-message: never
    include-stacktrace: never
    include-exception: false
```

### 5.3 File logging — ELIMINAR

```yaml
# ❌ ELIMINAR file logging (estándar usa solo console)
logging:
  file: ...   # ELIMINAR
```

### 5.4 Falta RabbitMQ config con env vars

```yaml
# ❌ FALTA:
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
```

---

## 6. SECURITY CONFIG — ❌ MAYOR

### Actual

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)      // ❌ NO ESTÁNDAR
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)      // ❌ NO ESTÁNDAR
                .logout(ServerHttpSecurity.LogoutSpec::disable)            // ❌ NO ESTÁNDAR
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll())
                .build();
    }
}
```

### Esperado (con perfiles dev/prod)

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
        "/actuator/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/webjars/**"
    };

    @Bean
    @Profile("dev")
    public SecurityWebFilterChain devSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .build();
    }

    @Bean
    @Profile("!dev")
    public SecurityWebFilterChain prodSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .pathMatchers("/api/**").permitAll()
                        .anyExchange().authenticated())
                .build();
    }
}
```

### Problemas

| # | Problema |
|---|---|
| 6.1 | No tiene `@Profile` annotations (dev/prod) |
| 6.2 | No tiene `PUBLIC_PATHS` array |
| 6.3 | `.httpBasic(disable)` — NO está en el estándar |
| 6.4 | `.formLogin(disable)` — NO está en el estándar |
| 6.5 | `.logout(disable)` — NO está en el estándar |
| 6.6 | Solo un bean (debe ser 2: dev + prod) |

---

## 7. WEBCLIENT CONFIG — ❌ MAYOR

### Actual (muy simple)

```java
@Bean
public WebClient.Builder webClientBuilder() {
    return WebClient.builder()
            .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024));
}
```

### Esperado (con Netty HttpClient y timeouts)

```java
@Value("${webclient.timeout.connect:5000}")
private int connectTimeout;
@Value("${webclient.timeout.read:10000}")
private int readTimeout;
@Value("${webclient.timeout.write:10000}")
private int writeTimeout;

@Bean
public WebClient.Builder webClientBuilder() {
    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .responseTimeout(Duration.ofMillis(readTimeout))
            .doOnConnected(conn -> conn
                    .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));

    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024));
}
```

### Problemas

| # | Problema |
|---|---|
| 7.1 | No tiene `HttpClient` de Netty |
| 7.2 | No tiene connect timeout |
| 7.3 | No tiene read timeout |
| 7.4 | No tiene write timeout |
| 7.5 | No tiene `@Value` para timeouts desde YAML |

---

## 8. RABBITMQ CONFIG — ❌ CRÍTICO

### Actual

```java
@Value("${rabbitmq.exchange.claims:claims-exchange}")  // ❌ Exchange custom
private String claimsExchange;

@Value("${rabbitmq.queue.complaints:complaints-queue}")  // ❌ Propiedades YAML no existen
private String complaintsQueue;
```

### Esperado

```java
public static final String EXCHANGE = "jass.events";  // ✅ Centralizado

@Bean
public TopicExchange jassEventsExchange() {
    return new TopicExchange(EXCHANGE, true, false);  // durable, no auto-delete
}
```

### Problemas

| # | Problema |
|---|---|
| 8.1 | Exchange `claims-exchange` en vez de `jass.events` (centralizado) |
| 8.2 | `@Value` referencia propiedades YAML que NO existen en ningún application*.yml |
| 8.3 | Queues con nombres custom (`complaints-queue`, `incidents-queue`) |
| 8.4 | Falta la cola de `complaint.response.events.queue` |
| 8.5 | Falta el binding `complaint.response.*` |

---

## 9. RESILIENCE4J CONFIG — ❌ MAYOR

### Actual (configuración Java con Spring Cloud wrapper)

```java
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;

@Bean
public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() { ... }
```

### Esperado

- **ELIMINAR** `Resilience4jConfig.java` completamente
- Configurar **en YAML** (application-dev.yml / application-prod.yml)
- Usar **anotaciones directas** en los clients: `@CircuitBreaker(name = "usersService")`, `@Retry(name = "usersService")`, `@TimeLimiter(name = "usersService")`
- Dependencia directa `resilience4j-spring-boot3` en vez de `spring-cloud-starter-circuitbreaker-reactor-resilience4j`

### Problemas

| # | Problema |
|---|---|
| 9.1 | Usa Spring Cloud CircuitBreaker abstraction (wrapper innecesario) |
| 9.2 | Configuración en Java en vez de YAML |
| 9.3 | Valores de circuit breaker no coinciden con estándar (slidingWindowSize=2 vs 10) |
| 9.4 | No hay retry config (solo circuit breaker y time limiter) |

---

## 10. CONTROLLERS — ❌ CRÍTICO

### 10.1 Rutas API incorrectas (NO funcionan con el Gateway)

| Controller | ACTUAL | ESPERADO |
|---|---|---|
| `IncidentRest` | `/api/admin/incidents` | `/api/v1/incidents` |
| `ComplaintRest` | `/api/complaints` | `/api/v1/complaints` |
| `IncidentTypeRest` | `/api/admin/incident-types` | `/api/v1/incident-types` |
| `IncidentResolutionRest` | `/api/admin/incident-resolutions` | `/api/v1/incident-resolutions` |
| `ComplaintCategoryRest` | ❌ **NO EXISTE** | `/api/v1/complaint-categories` |

**Impacto:** El Gateway hace RewritePath de `/api/v1/**` → `/api/**`. Con las rutas actuales (`/api/admin/...` y `/api/complaints`), las peticiones desde el Gateway **NO LLEGARÁN** a ningún endpoint.

### 10.2 Violación de Arquitectura Hexagonal

#### IncidentTypeRest — ❌ **COMPLETAMENTE BYPASSA DOMINIO**

```java
// ❌ Inyecta directamente el repository de infraestructura
private final IncidentTypeMongoRepository incidentTypeRepository;

// ❌ Usa IncidentTypeDocument (infrastructure) en vez de IncidentType (domain)
public Mono<ResponseEntity<ApiResponse<List<IncidentTypeDocument>>>> findAll() { ... }

// ❌ Recibe IncidentTypeDocument como @RequestBody (expone infraestructura al exterior)
public Mono<ResponseEntity<...>> create(@RequestBody IncidentTypeDocument request) { ... }
```

**Debería:** Inyectar use cases (`ICreateIncidentTypeUseCase`, `IGetIncidentTypeUseCase`, etc.) y usar DTOs.

#### IncidentResolutionRest — ❌ **COMPLETAMENTE BYPASSA DOMINIO**

```java
// ❌ Mismos problemas que IncidentTypeRest
private final IncidentResolutionMongoRepository incidentResolutionRepository;
// ❌ Usa IncidentResolutionDocument directamente
// ❌ Delete es FÍSICO (deleteById) en vez de lógico
```

#### IncidentRest — ⚠️ **PARCIALMENTE BYPASSA DOMINIO**

```java
// ❌ Inyecta IncidentMongoRepository ADEMÁS de los use cases
private final IncidentMongoRepository incidentMongoRepository;

// Los métodos delete, restore, findAllActive, findAllInactive
// usan incidentMongoRepository directamente en vez de use cases:
// ❌ incidentMongoRepository.findById(id) → existing.setRecordStatus("INACTIVE") → save
// ❌ incidentMongoRepository.findByRecordStatus("ACTIVE")
```

**Debería:** Usar `IDeleteIncidentUseCase`, `IRestoreIncidentUseCase` que YA existen pero NO se inyectan.

#### ComplaintRest — ✅ OK (usa use cases correctamente)

Único controller que respeta la arquitectura hexagonal correctamente.

### 10.3 Controller faltante

```
❌ ComplaintCategoryRest NO EXISTE
```

Según el estándar, debería tener un controller en `/api/v1/complaint-categories` con operaciones CRUD + delete/restore.
Existen los use cases (`CreateComplaintCategoryUseCaseImpl`, `GetComplaintCategoryUseCaseImpl`, etc.) pero no hay controller que los exponga.

### 10.4 ComplaintRest — Falta delete/restore

```
❌ No tiene endpoints DELETE /{id} ni PATCH /{id}/restore
❌ No tiene endpoints GET /active ni GET /inactive
```

Otros controllers los tienen, pero ComplaintRest no los implementa a pesar de que existen `IDeleteComplaintUseCase` y `IRestoreComplaintUseCase`.

---

## 11. IDIOMA — ❌ MAYOR

### Todo el código está en español. DEBE estar en inglés

#### 11.1 Logs (ejemplos)

```java
log.info("Obteniendo todos los incidentes");           // → "Fetching all incidents"
log.info("Buscando incidente con ID: {}", id);         // → "Finding incident by ID: {}"
log.info("Creando nuevo incidente: {}", ...);          // → "Creating new incident: {}"
log.info("Eliminando lógicamente incidente con ID: {}");// → "Soft deleting incident by ID: {}"
log.debug("No se encontró header de usuario autenticado"); // → "Authenticated user header not found"
```

#### 11.2 Swagger descriptions/summaries

```java
@Operation(summary = "Listar todos los incidentes")    // → "List all incidents"
@Operation(summary = "Obtener incidente por ID")       // → "Get incident by ID"
@Tag(description = "API para gestión de incidentes")   // → "Incidents management API"
```

#### 11.3 Mensajes de respuesta

```java
"Incidente creado exitosamente"    // → "Incident created successfully"
"Queja cerrada exitosamente"       // → "Complaint closed successfully"
```

#### 11.4 Comentarios

```java
/** Controlador REST para gestión de incidentes */     // → "REST controller for incidents management"
/** Configuración de seguridad para WebFlux */          // → "WebFlux security configuration"
```

#### 11.5 YAML

```yaml
description: Microservicio para gestión de reclamos e incidentes  # → English or REMOVE
```

---

## 12. GATEWAY HEADERS EXTRACTOR — ⚠️ MENOR

### Actual

```java
public Optional<AuthenticatedUser> extractUser(ServerHttpRequest request) { ... }
public Optional<String> extractUserId(ServerHttpRequest request) { ... }
public Optional<String> extractOrganizationId(ServerHttpRequest request) { ... }
```

### Esperado (patrón reactivo con ServerWebExchange)

```java
public Mono<GatewayHeaders> extract(ServerWebExchange exchange) { ... }
```

**Nota:** El patrón actual funciona, pero no sigue el estándar definido para los otros microservicios que usan `ServerWebExchange` y retornan `Mono<GatewayHeaders>`.

---

## 13. DOCKER-COMPOSE.YML — ⚠️ MENOR

### 13.1 `target: runtime` — NO EXISTE en el Dockerfile

```yaml
build:
  target: runtime  # ❌ El Dockerfile no tiene un stage llamado "runtime"
```

### 13.2 CORS — ELIMINAR

```yaml
# ❌ CORS lo maneja el Gateway, no cada microservicio
- CORS_ALLOWED_ORIGINS=...
- CORS_ALLOWED_METHODS=...
- CORS_ALLOWED_HEADERS=...
```

### 13.3 JAVA_OPTS — ELIMINAR

```yaml
# ❌ No está en el estándar
- JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
```

### 13.4 JWT variables — ELIMINAR

```yaml
# ❌ JWT lo maneja el Gateway
- JWT_PRIVATE_KEY=...
- JWT_PUBLIC_KEY=...
- JWT_EXPIRATION=...
```

### 13.5 APP_* variables — ELIMINAR

```yaml
# ❌ No están en el estándar
- APP_NAME=...
- APP_DESCRIPTION=...
- APP_VERSION=...
```

### 13.6 Puerto incorrecto

```
ACTUAL:    expose 8089
ESPERADO:  expose 8085
```

### 13.7 MongoDB Atlas en dev

```yaml
# ❌ Dev debe apuntar a MongoDB local o contenedor, NO a Atlas
- SPRING_DATA_MONGODB_URL=mongodb+srv://santiago:santiago19@...
```

---

## 14. ARCHIVOS QUE SOBRAN

| Archivo | Razón |
|---|---|
| `Resilience4jConfig.java` | Debe ser configuración YAML, no Java |
| `RequestContextFilter.java` | Verificar si es necesario o si `GatewayHeadersFilter` lo reemplaza |

---

## 15. ARCHIVOS/COMPONENTES QUE FALTAN

| Componente | Ubicación esperada |
|---|---|
| `ComplaintCategoryRest` | `infrastructure/adapters/in/rest/ComplaintCategoryRest.java` |
| `ComplaintCategoryMapper` | `application/mappers/ComplaintCategoryMapper.java` (verificar si existe) |
| `IncidentTypeMapper` | `application/mappers/IncidentTypeMapper.java` (verificar si existe) |
| `IncidentResolutionMapper` | `application/mappers/IncidentResolutionMapper.java` (verificar si existe) |
| Config RabbitMQ en YAML | `application-dev.yml` y `application-prod.yml` |
| Config Resilience4j en YAML | `application-dev.yml` y `application-prod.yml` |
| Config WebClient timeouts en YAML | `application-dev.yml` y `application-prod.yml` |

---

## 16. LO QUE ESTÁ BIEN ✅

| Componente | Estado |
|---|---|
| Estructura de carpetas hexagonal | ✅ Correcta (domain/application/infrastructure) |
| Modelos de dominio (Complaint, Incident, etc.) | ✅ Presentes y bien definidos |
| Value Objects (enums) | ✅ Presentes (aunque algunos nombres en español) |
| Ports IN (interfaces de use cases) | ✅ Todas las interfaces definidas |
| Ports OUT (interfaces de repositories y clients) | ✅ Todas las interfaces definidas |
| Use Cases Implementation | ✅ Todos implementados (complaint: 7, incident: 8, incidentType: 5, complaintCategory: 5) |
| Events (Complaint + Incident) | ✅ 10 eventos definidos |
| Repository Implementations (RepositoryImpl) | ✅ 6 implementaciones existen |
| External Clients (UserServiceClientImpl, InfrastructureClientImpl) | ✅ Existen |
| Event Publisher (ClaimsEventPublisherImpl) | ✅ Existe |
| Mappers (ComplaintMapper, IncidentMapper) | ✅ Existen (faltan 2 más) |
| MongoDB Documents | ✅ 7 documents definidos (incluye MaterialUsedEmbedded) |
| MongoDB Repositories | ✅ 6 repositories definidos |
| GlobalExceptionHandler | ✅ Existe |
| GatewayHeadersFilter | ✅ Existe (funcional aunque diferente patrón) |
| SecurityContextAdapter | ✅ Existe |
| AuthenticatedUser | ✅ Existe |
| Excepciones de dominio (6 específicas + 5 base) | ✅ Correctas |
| Domain Service (ClaimsAuthorizationService) | ✅ Existe |
| DTOs (request/response) | ✅ Presentes para las 4 entidades |
| MongoConfig | ✅ OK |

---

## PLAN DE CORRECCIÓN PRIORIZADO

### Fase 1 — CRÍTICO (Debe hacerse primero)

1. ✏️ **pom.xml**: Spring Boot → 3.5.0, Java → 21, eliminar spring-cloud-gateway, corregir dependencias
2. ✏️ **Controllers paths**: Cambiar TODOS a `/api/v1/...`
3. ✏️ **IncidentTypeRest**: Reescribir usando use cases + DTOs (no Documents)
4. ✏️ **IncidentResolutionRest**: Reescribir usando use cases + DTOs (no Documents)
5. ✏️ **IncidentRest**: Eliminar IncidentMongoRepository, usar use cases para delete/restore/active/inactive
6. ✏️ **ComplaintCategoryRest**: CREAR el controller que falta
7. ✏️ **ComplaintRest**: Agregar delete/restore/active/inactive endpoints
8. ✏️ **RabbitMQConfig**: Cambiar exchange a `jass.events`
9. ✏️ **Puerto**: Cambiar a 8085 en todos los archivos

### Fase 2 — MAYOR (Segundo en prioridad)

10. ✏️ **Dockerfile**: Reescribir completamente con patrón estándar
2. ✏️ **SecurityConfig**: Reescribir con perfiles dev/prod y PUBLIC_PATHS
3. ✏️ **WebClientConfig**: Agregar Netty HttpClient con timeouts
4. ✏️ **application.yml**: Eliminar bloques no estándar, simplificar springdoc y management
5. ✏️ **application-dev.yml**: MongoDB localhost, agregar RabbitMQ, resilience4j, webclient timeouts, eliminar JWT
6. ✏️ **application-prod.yml**: Agregar RabbitMQ env vars, eliminar JWT, server.error, file logging
7. ✏️ **Resilience4jConfig.java**: ELIMINAR y mover a YAML
8. ✏️ **Idioma**: Cambiar TODOS los logs, Swagger y comentarios a inglés

### Fase 3 — MENOR (Último)

18. ✏️ **docker-compose.yml**: Limpiar CORS, JAVA_OPTS, JWT, APP_*, fix target, fix port
2. ✏️ **GatewayHeadersExtractor**: Evaluar si migrar al patrón reactivo estándar
3. ✏️ **Mappers faltantes**: Crear IncidentTypeMapper, IncidentResolutionMapper si no existen
4. ✏️ **pom.xml cleanup**: Eliminar maven-compiler-plugin innecesario, arreglar description

---

**Total de archivos a modificar: ~20**
**Archivos a crear: 1-3** (ComplaintCategoryRest + mappers faltantes)
**Archivos a eliminar: 1** (Resilience4jConfig.java)
