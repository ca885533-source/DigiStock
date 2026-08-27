# DigiStock

Sistema web de facturación e inventario — proyecto formativo SENA, programa
Análisis y Desarrollo de Software (ADSI).

**Evidencia:** GA7-220501096-AA3-EV01 — Codificación del módulo de
autenticación y gestión de usuarios.

## Tecnologías

- Java 17
- Spring Boot 3.3 (Web, Security, Data MongoDB, Thymeleaf, Validation)
- MongoDB
- Maven
- Lombok

## Módulo codificado en esta evidencia

**Autenticación y gestión de usuarios**, con:

- Registro de usuarios con validación de campos (nombre de usuario, correo,
  nombre completo, contraseña, rol).
- Inicio de sesión (login) con Spring Security.
- Roles: `ADMINISTRADOR`, `VENDEDOR`, `ALMACENISTA`.
- Panel principal (`dashboard`) que cambia según el rol autenticado.
- Módulo de administración de usuarios (activar/desactivar cuentas),
  restringido al rol `ADMINISTRADOR`.
- Contraseñas encriptadas con BCrypt (nunca se guardan en texto plano).

## Historias de usuario cubiertas

1. Como usuario quiero registrarme en el sistema indicando mis datos y mi
   rol, validando que la información sea correcta.
2. Como usuario quiero iniciar sesión con mi usuario y contraseña para
   acceder al sistema según mi rol.
3. Como administrador quiero ver el listado de usuarios y poder activar o
   desactivar cuentas, para controlar quién puede acceder al sistema.

## Cómo ejecutar el proyecto

1. Tener instalado Java 17, Maven y MongoDB corriendo localmente
   (`mongodb://localhost:27017`).
2. Clonar el repositorio.
3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
4. Abrir `http://localhost:8080/registro` para crear el primer usuario
   (por ejemplo, con rol `ADMINISTRADOR`).
5. Iniciar sesión en `http://localhost:8080/login`.

## Ejecutar las pruebas unitarias

```bash
mvn test
```

Las pruebas están en `src/test/java/com/digistock/app/UsuarioServiceTest.java`
y validan las reglas de negocio del registro de usuarios (usuario duplicado,
correo duplicado, encriptado de contraseña).

## Estructura del proyecto

```
src/main/java/com/digistock/app/
├── DigistockApplication.java   Clase principal
├── model/                      Entidades (Usuario, Rol)
├── repository/                 Acceso a datos (MongoDB)
├── service/                    Lógica de negocio
├── security/                   Integración con Spring Security
├── controller/                 Controladores MVC
├── dto/                        Objetos de transferencia con validaciones
└── config/                     Configuración de seguridad

src/main/resources/
├── templates/                  Vistas Thymeleaf (login, registro, dashboard, usuarios)
├── static/css/                 Estilos
└── application.properties      Configuración (puerto, conexión a MongoDB)
```

## Control de versiones

Este proyecto se gestiona con Git. Historial de commits sugerido para esta
evidencia:

1. Estructura inicial del proyecto (pom.xml, clase principal).
2. Modelo de datos: entidad Usuario y enum Rol.
3. Repositorio y DTO de registro con validaciones.
4. Configuración de Spring Security e integración con MongoDB.
5. Controladores y vistas del módulo de autenticación.
6. Pruebas unitarias del servicio de usuarios.

## Próximos módulos (fuera del alcance de esta evidencia)

- Módulo de inventario (productos, categorías, stock).
- Módulo de facturación (clientes, facturas, detalle de factura).
