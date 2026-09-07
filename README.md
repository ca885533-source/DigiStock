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

---

## Evidencia GA7-220501096-AA4-EV03 — Módulo de facturación (front-end)

Continuación del proyecto: codificación del **módulo de facturación/ventas**,
construido sobre la base de autenticación y roles ya existente.

### Funcionalidad codificada

- Listado de facturas generadas (`/facturas`), ordenadas de la más reciente
  a la más antigua, con indicador visual (insignia) de estado.
- Formulario de creación de factura (`/facturas/nueva`) con líneas de
  producto agregadas dinámicamente mediante JavaScript (agregar/quitar
  producto) y cálculo de subtotal, IVA (19%) y total en tiempo real en el
  navegador.
- Registro de la venta (`POST /facturas`): valida stock disponible,
  calcula los totales con el precio real guardado en base de datos
  (nunca con el valor recibido del formulario) y descuenta el stock
  vendido del inventario.
- Vista de detalle/comprobante de factura (`/facturas/{id}`), con opción
  de impresión y de anulación (solo si la factura sigue en estado
  `PAGADA`); anular reintegra el stock de cada producto.
- Acceso restringido por rol: `/facturas/**` solo para `ADMINISTRADOR` y
  `VENDEDOR` (configurado en `SecurityConfig`).
- Catálogo mínimo de productos (`Producto`) con datos de ejemplo
  precargados (`DatosIniciales`), como soporte temporal mientras se
  desarrolla el módulo de inventario completo en otra evidencia.

### Historias de usuario cubiertas

- Como vendedor quiero registrar una venta seleccionando productos y
  cantidades, para generar una factura con el total a cobrar.
- Como vendedor quiero consultar el historial de facturas generadas,
  para hacer seguimiento a mis ventas.
- Como administrador quiero poder anular una factura errónea, para que
  el inventario y los reportes queden correctos.

### Estándares de codificación aplicados

- Convención de nombres Java: clases en PascalCase, atributos y métodos
  en camelCase, paquetes en minúscula.
- Arquitectura en capas (Controller → Service → Repository → Model),
  igual que en el módulo de usuarios.
- DTOs con Bean Validation (`@NotBlank`, `@NotEmpty`, `@Valid`) separados
  de las entidades de persistencia.
- Comentarios Javadoc en todas las clases y métodos públicos, explicando
  el propósito y las decisiones de diseño relevantes.
- El precio y el stock de un producto se leen siempre desde la base de
  datos en el servidor; el formulario solo envía el id del producto y la
  cantidad, evitando que un valor manipulado en el navegador afecte el
  cobro real.

### Control de versiones — commits de esta evidencia

1. `feat: entidades Factura, ItemFactura, Producto y enum EstadoFactura`
2. `feat: repositorios de Producto y Factura`
3. `feat: DTOs del formulario de creacion de factura con validaciones`
4. `feat: logica de negocio de facturacion (calculo IVA, control de stock, anulacion)`
5. `feat: controlador del modulo de facturacion y permisos por rol`
6. `feat: vistas Thymeleaf del modulo de facturacion (listado, formulario dinamico y comprobante)`
7. `test: pruebas unitarias del servicio de facturacion (IVA, stock, anulacion)`

### Pruebas unitarias

En `src/test/java/com/digistock/app/FacturaServiceTest.java`, cubren:

- Cálculo correcto de subtotal, IVA y total.
- Descuento del stock vendido al crear una factura.
- Rechazo de la venta cuando no hay stock suficiente.
- Reintegro del stock al anular una factura.

### Limitaciones conocidas de esta evidencia

- El número de factura (`FAC-000001`, `FAC-000002`, ...) se genera contando
  los documentos existentes; en un escenario con varios vendedores
  facturando al mismo tiempo se recomendaría un contador atómico en
  MongoDB para evitar duplicados.
- El módulo de inventario completo (CRUD de productos, categorías,
  proveedores) no forma parte de esta evidencia; `Producto` se dejó con
  los campos mínimos para que la facturación pueda seleccionar productos
  y descontar stock.
