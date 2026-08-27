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

1. Como administrador quiero registrar nuevos usuarios asignándoles un rol,
   validando que los datos ingresados sean correctos, para controlar el
   acceso al sistema.
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

## Alcance de esta evidencia

Esta entrega corresponde únicamente a la codificación del **módulo de
autenticación y gestión de usuarios** de DigiStock. DigiStock, como sistema
completo, contempla además un módulo de inventario (productos, categorías,
stock) y un módulo de facturación (clientes, facturas, detalle de factura),
los cuales se apoyarán en los roles y usuarios definidos aquí, pero no forman
parte del código entregado en esta evidencia.

## Decisiones de diseño

- Se separó la lógica de negocio en una capa de **servicio**
  (`UsuarioService`) independiente del controlador, para mantener el
  controlador enfocado solo en manejar peticiones HTTP y facilitar las
  pruebas unitarias con mocks.
- La validación de los datos del formulario se hace en dos niveles:
  Bean Validation (`@NotBlank`, `@Size`, `@Pattern`, `@Email`) en el DTO
  para errores de formato, y validaciones de negocio (usuario/correo
  duplicado) en el servicio.
- Las cuentas no se eliminan físicamente; se desactivan (`activo = false`)
  para conservar el historial y evitar inconsistencias con datos
  relacionados en los módulos futuros de facturación e inventario.

## Limitaciones conocidas

- El formulario de registro (`/registro`) es público y permite elegir
  cualquier rol, incluido `ADMINISTRADOR`. Esto se dejó así
  intencionalmente para poder crear el primer usuario administrador sin
  depender de datos precargados en MongoDB; en un entorno de producción
  este registro se restringiría o se movería detrás de una pantalla
  exclusiva para administradores.
- Los índices únicos declarados en `Usuario` (`nombreUsuario`, `correo`)
  dependen de que `spring.data.mongodb.auto-index-creation=true` esté
  habilitado; de lo contrario, la unicidad solo queda garantizada por la
  validación manual en `UsuarioService`.
- No se incluyen pruebas de integración de los controladores ni de la
  configuración de seguridad; las pruebas actuales cubren únicamente la
  lógica de negocio del servicio de usuarios.
