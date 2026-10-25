# Eventify - Sistema de Gestión de Eventos y Lugares

Plataforma desarrollada con **Spring Boot** y **Java 21** para la administración, programación y consulta de eventos y recintos (*venues*), combinando una **API RESTful** con una **interfaz web administrativa interactiva (Thymeleaf MVC)**.

---

## 📋 Tabla de Contenidos

1. [Características Principales](#-características-principales)
2. [Tecnologías y Dependencias](#-tecnologías-y-dependencias)
3. [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
4. [Panel Administrativo Web (Thymeleaf MVC)](#-panel-administrativo-web-thymeleaf-mvc)
5. [Endpoints de la API REST](#-endpoints-de-la-api-rest)
6. [Modelo de Datos](#-modelo-de-datos)
7. [Manejo Global de Excepciones](#-manejo-global-de-excepciones)
8. [Base de Datos y Persistencia](#-base-de-datos-y-persistencia)
9. [Poblado de Datos Iniciales (Data Seeder)](#-poblado-de-datos-iniciales-data-seeder)
10. [Documentación Interactiva (Swagger / OpenAPI)](#-documentación-interactiva-swagger--openapi)
11. [Pruebas Automatizadas](#-pruebas-automatizadas)
12. [Requisitos y Ejecución](#-requisitos-y-ejecución)

---

## 🚀 Características Principales

- **Panel Web Administrativo (Thymeleaf)**: Vistas interactivas para coordinadores en `/admin/events`, con diseño responsive basado en Bootstrap 5 e iconos de Bootstrap Icons.
- **Separación de Responsabilidades**: Rutas web (`/admin/**`) separadas de las rutas de API (`/api/**`).
- **Estados Dinámicos y Casos Borde**: Manejo de inventario vacío mediante mensajes amigables (*"Actualmente no hay eventos programados"* / *"Actualmente no hay lugares registrados"*).
- **Patrón Post-Redirect-Get (PRG)**: Formularios de registro con `th:object` y `th:action` que redirigen con atributos flash (`RedirectAttributes`) para confirmar acciones visualmente y evitar reenvíos accidentales.
- **Operaciones CRUD en API REST**: Gestión completa con paginación (`Pageable`) y ordenamiento en Spring Data JPA.
- **Documentación OpenAPI Limpia**: Swagger UI configurado para exponer exclusivamente los endpoints de la API (`/api/**`), excluyendo las vistas web.
- **Persistencia en Archivo**: Base de datos H2 en `./data/eventifydb.mv.db` para conservar los datos entre sesiones.
- **Suite Completa de Pruebas**: 23 pruebas automatizadas (tests unitarios Mockito, tests de persistencia `@DataJpaTest` y pruebas de controladores web con `MockMvc`).

---

## 🛠 Tecnologías y Dependencias

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 4.1.1
- **Motor de Plantillas**: Thymeleaf (`spring-boot-starter-thymeleaf`)
- **Frontend**: HTML5, Bootstrap 5.3.3, Bootstrap Icons
- **Persistencia**: Spring Data JPA & Hibernate
- **Base de Datos**: H2 Database Engine (modo archivo)
- **Documentación**: SpringDoc OpenAPI Starter WebMVC UI v3.1.0
- **Productividad**: Project Lombok
- **Testing**: JUnit 5, Mockito, Spring Test (`MockMvc`), `@DataJpaTest`

---

## 🏛 Arquitectura del Proyecto

```
src/main/
├── java/com/eventify/
│   ├── EventifyApplication.java           # Clase principal Spring Boot
│   │
│   ├── config/                            # Configuraciones del sistema
│   │   ├── DataSeederConfig.java          # Carga de datos iniciales controlada
│   │   └── OpenApiConfig.java             # Segmentación OpenAPI para /api/**
│   │
│   ├── controller/                        # Capa de presentación
│   │   ├── AdminController.java           # Controlador MVC de Vistas Web (@Controller)
│   │   ├── EventController.java           # Controlador REST de Eventos (@RestController)
│   │   └── VenueController.java           # Controlador REST de Lugares (@RestController)
│   │
│   ├── service/                           # Lógica de negocio y validaciones
│   │   ├── EventService.java
│   │   └── VenueService.java
│   │
│   ├── repository/                        # Acceso a datos
│   │   ├── EventRepository.java
│   │   └── VenueRepository.java
│   │
│   ├── model/                             # Entidades de dominio
│   │   ├── Event.java
│   │   └── Venue.java
│   │
│   └── exception/                         # Manejo centralizado de excepciones
│       ├── GlobalExceptionHandler.java
│       ├── InvalidDataException.java
│       └── ResourceNotFoundException.java
│
└── resources/
    ├── application.properties             # Configuración de base de datos y OpenAPI
    └── templates/                         # Plantillas Thymeleaf
        ├── fragments/
        │   └── navbar.html                # Componentes reutilizables (Navbar, Footer)
        └── admin/
            ├── events.html                # Panel centralizado de eventos y recintos
            ├── event-form.html            # Formulario de alta de eventos
            └── venue-form.html            # Formulario de alta de lugares
```

---

## 🖥 Panel Administrativo Web (Thymeleaf MVC)

### Rutas de la Interfaz Web (`/admin/**`)

| Ruta | Método | Controlador | Descripción |
| :--- | :--- | :--- | :--- |
| `/` | `GET` | `AdminController` | Redirección amigable a `/admin/events` |
| `/admin` o `/admin/events` | `GET` | `AdminController` | Panel principal con tablas de eventos y lugares |
| `/admin/events/new` | `GET` | `AdminController` | Formulario web para nuevo evento |
| `/admin/events` | `POST` | `AdminController` | Guarda el evento y redirige (`PRG`) con mensaje flash |
| `/admin/venues/new` | `GET` | `AdminController` | Formulario web para nuevo lugar/recinto |
| `/admin/venues` | `POST` | `AdminController` | Guarda el recinto y redirige (`PRG`) con mensaje flash |

### Directivas Thymeleaf Utilizadas
- **`th:each`**: Iteración de colecciones para renderizar filas en tablas HTML (`th:each="event : ${events}"`).
- **`th:text`**: Inserción de valores dinámicos seguros (`th:text="${event.nombre}"`).
- **`th:if` / `th:unless`**: Renderizado condicional para mostrar la tabla o la alerta informativa cuando el catálogo está vacío (`th:if="${#lists.isEmpty(events)}"`).
- **`th:action` y `th:object`**: Enlace bidireccional y envío seguro de formularios (`th:action="@{/admin/events}" th:object="${event}"`).
- **`th:replace`**: Inclusión modular de fragmentos de navegación y pie de página (`th:replace="~{fragments/navbar :: navbar}"`).

---

## 📡 Endpoints de la API REST

### Rutas de la API (`/api/**`)

| Método | Endpoint | Descripción | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/events` | Crea un evento | `201 Created`, `400 Bad Request` |
| `GET` | `/api/events` | Catálogo de eventos paginado (`page`, `size`, `sort`) | `200 OK` |
| `GET` | `/api/events/{id}` | Detalle de un evento por ID | `200 OK`, `404 Not Found` |
| `PUT` | `/api/events/{id}` | Actualiza un evento | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE`| `/api/events/{id}` | Eliminación física de un evento | `204 No Content`, `404 Not Found` |
| `POST` | `/api/venues` | Crea un recinto | `201 Created`, `400 Bad Request` |
| `GET` | `/api/venues` | Catálogo de recintos paginado | `200 OK` |
| `GET` | `/api/venues/{id}` | Detalle de un recinto por ID | `200 OK`, `404 Not Found` |
| `PUT` | `/api/venues/{id}` | Actualiza un recinto | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE`| `/api/venues/{id}` | Eliminación física de un recinto | `204 No Content`, `404 Not Found` |

---

## 📊 Modelo de Datos

### 1. Entidad `Event` (Tabla: `events`)
- `id` (`Long`): Identificador único autogenerado.
- `nombre` (`String`, máx. 100): Obligatorio.
- `fecha` (`LocalDate`): Fecha del evento con `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)`.
- `descripcion` (`String`, máx. 450): Detalle opcional.

### 2. Entidad `Venue` (Tabla: `venues`)
- `id` (`Long`): Identificador único autogenerado.
- `nombre` (`String`, máx. 200): Obligatorio.
- `direccion` (`String`, máx. 100): Obligatorio.
- `capacidad` (`Integer`): Aforo máximo permitido.

---

## 🛡 Manejo Global de Excepciones

- **`ResourceNotFoundException`** &rarr; HTTP `404 Not Found`.
- **`InvalidDataException`** &rarr; HTTP `400 Bad Request` (en API) y redirección con mensaje de error contextual en formularios web.

---

## 💾 Base de Datos y Persistencia

Configurada mediante H2 en archivo local (`./data/eventifydb`):

```properties
spring.application.name=Eventify
spring.datasource.url=jdbc:h2:file:./data/eventifydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update

# Limitar documentación Swagger exclusivamente a endpoints REST
springdoc.paths-to-match=/api/**
```

---

## 🌾 Poblado de Datos Iniciales (Data Seeder)

La clase [`DataSeederConfig.java`](src/main/java/com/eventify/config/DataSeederConfig.java) implementa `CommandLineRunner` verificando si existen registros antes de insertar datos de prueba:

```java
@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedInitialData(EventService eventService, VenueService venueService) {
        return args -> {
            if (venueService.findAll().isEmpty()) {
                venueService.save(new Venue(null, "Centro de Convenciones Principal", "Av. El Sol 123", 500));
                venueService.save(new Venue(null, "Auditorio Tecnológico", "Calle Innovación 456", 150));
            }

            if (eventService.findAll().isEmpty()) {
                eventService.save(new Event(null, "Conferencia Tech 2026", LocalDate.parse("2026-10-15"), "Encuentro anual de desarrollo"));
                eventService.save(new Event(null, "Workshop Spring Boot", LocalDate.parse("2026-11-20"), "Taller práctico de backend"));
            }
        };
    }
}
```

---

## 📖 Documentación Interactiva (Swagger / OpenAPI)

Para consultar únicamente las APIs REST sin interferencia de las rutas web:
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **JSON OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧪 Pruebas Automatizadas

El proyecto cuenta con **23 pruebas automatizadas** que garantizan la calidad del software:

1. **Pruebas de Interfaz Web (`MockMvc`):**
   - [`AdminControllerTest`](src/test/java/com/eventify/controller/AdminControllerTest.java):
     - Renderizado de dashboard con estado `200 OK`, vista `"admin/events"` y presencia de atributos `events` y `venues` en el `Model`.
     - Manejo de inventario vacío mostrando `"Actualmente no hay eventos programados"`.
     - Despliegue de formularios `admin/event-form` y `admin/venue-form`.
     - Validación del patrón Post-Redirect-Get (`3xxRedirection`, redirección a `/admin/events` y flash attributes de confirmación).
     - Redirección de la raíz `/` hacia el panel de administración.
2. **Pruebas de Servicios (Mockito & JUnit 5):**
   - [`EventServiceTest`](src/test/java/com/eventify/service/EventServiceTest.java) y [`VenueServiceTest`](src/test/java/com/eventify/service/VenueServiceTest.java): Guardado, validación de excepciones ante nombres vacíos, consultas por ID y paginación.
3. **Pruebas de Repositorio (`@DataJpaTest`):**
   - [`EventRepositoryTest`](src/test/java/com/eventify/repository/EventRepositoryTest.java) y [`VenueRepositoryTest`](src/test/java/com/eventify/repository/VenueRepositoryTest.java): Persistencia en BD en memoria, paginación y autogeneración de identificadores.

Para ejecutar todas las pruebas:
```bash
.\mvnw.cmd test
```

---

## ⚡ Requisitos y Ejecución

- **Requisitos:** Java 21 LTS instalado y configurado en el sistema.
- **Compilación:**
  ```bash
  .\mvnw.cmd clean package
  ```
- **Iniciar el servidor:**
  ```bash
  .\mvnw.cmd spring-boot:run
  ```
- **Acceso:**
  - Panel Web: [http://localhost:8080/admin/events](http://localhost:8080/admin/events)
  - API Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
