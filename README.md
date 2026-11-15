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

## Módulo de inventario (productos)

Gestión de los productos que se venden en las facturas, con pantallas web
y API REST.

- Listado, creación, edición y eliminación de productos (`/productos`).
- Cada producto guarda nombre, precio y stock disponible (colección `productos`).
- Permisos por rol: ver productos lo pueden hacer `ADMINISTRADOR`, `VENDEDOR`
  y `ALMACENISTA`; crear, editar y eliminar, solo `ADMINISTRADOR` y `ALMACENISTA`.
- El stock se descuenta al facturar y se reintegra al anular una factura.
- La versión API de este módulo está en los servicios 3 a 7 de la sección siguiente.

> Nota: las menciones anteriores a que el inventario "no forma parte de esta
> evidencia" corresponden a AA3 y AA4; en AA5 el módulo ya está implementado.

## Evidencia GA7-220501096-AA5-EV03 — Servicios web (API REST)

**URL base:** `http://localhost:8080` · **Formato:** `application/json` · **Seguridad:** todas las rutas
`/api/**`, excepto `POST /api/auth/login`, usan **HTTP Basic** (`Authorization: Basic base64(usuario:contraseña)`)
con permisos por rol; la API no crea sesión. Las contraseñas se guardan cifradas con BCrypt.
Documentación completa en [docs/API.md](docs/API.md) y Swagger UI en `http://localhost:8080/swagger-ui.html`.

### Servicios

| # | Método | Ruta | Descripción | Rol requerido | Códigos |
|---|--------|------|-------------|---------------|---------|
| 1 | POST | `/api/auth/login` | Verificar usuario y contraseña | Público | 200, 400, 401 |
| 2 | POST | `/api/auth/registro` | Registrar un usuario | ADMINISTRADOR | 201, 400 |
| 3 | GET | `/api/usuarios` | Listar usuarios | ADMINISTRADOR | 200 |
| 4 | GET | `/api/usuarios/{id}` | Consultar un usuario | ADMINISTRADOR | 200, 404 |
| 5 | PATCH | `/api/usuarios/{id}/estado` | Activar o desactivar una cuenta | ADMINISTRADOR | 200, 404 |
| 6 | GET | `/api/productos` | Listar productos | Autenticado | 200 |
| 7 | GET | `/api/productos/{id}` | Consultar un producto | Autenticado | 200, 404 |
| 8 | POST | `/api/productos` | Crear un producto | ADMINISTRADOR, ALMACENISTA | 201, 400 |
| 9 | PUT | `/api/productos/{id}` | Actualizar un producto | ADMINISTRADOR, ALMACENISTA | 200, 400, 404 |
| 10 | PATCH | `/api/productos/{id}/stock` | Entrada (+) o salida (−) de stock | ADMINISTRADOR, ALMACENISTA | 200, 400, 404 |
| 11 | DELETE | `/api/productos/{id}` | Eliminar un producto | ADMINISTRADOR, ALMACENISTA | 204, 404 |
| 12 | POST | `/api/facturas` | Crear factura (venta con IVA 19 %) | ADMINISTRADOR, VENDEDOR | 201, 400 |
| 13 | GET | `/api/facturas` | Listar facturas | ADMINISTRADOR, VENDEDOR | 200 |
| 14 | GET | `/api/facturas/{id}` | Consultar una factura | ADMINISTRADOR, VENDEDOR | 200, 404 |
| 15 | PATCH | `/api/facturas/{id}/anular` | Anular factura y reintegrar stock | ADMINISTRADOR | 200, 400, 404 |

### Ejemplo: crear una factura

```
POST /api/facturas
Authorization: Basic base64(usuario:contraseña)
```
```json
{
  "nombreCliente": "Carlos Ruiz",
  "documentoCliente": "1098765432",
  "items": [
    { "productoId": "66f1d2...", "cantidad": 2 }
  ]
}
```
El servidor ignora cualquier precio enviado: lee el precio real de la base de datos, valida el
stock, calcula subtotal, IVA y total, descuenta el stock y toma el vendedor del usuario autenticado.

### Pruebas con Postman

Importar `DigiStock_AA5-EV04.postman_collection.json` y ejecutarla con *Run collection*. Cada petición valida el código de estado y el contenido de la respuesta.

---

## Factura electrónica: CUFE, código QR y verificación pública

Cada factura recibe un **CUFE** (Código Único de Factura Electrónica) al crearse, y su
comprobante muestra un **código QR** que lleva a una página pública de verificación.

- **CUFE:** hash SHA-384 (96 caracteres hexadecimales) calculado con el número de factura,
  fecha, subtotal, IVA, total, documento del cliente y vendedor (`CufeGenerador`). Cualquier
  cambio en esos datos produce un código distinto. Es una simulación con fines formativos:
  en Colombia el CUFE real lo calcula el sistema autorizado por la DIAN.
- **Código QR:** se genera con ZXing (`QrGenerador`) y se sirve como imagen en
  `GET /facturas/{id}/qr.png`.
- **Verificación pública:** `GET /verificar/{cufe}` (sin iniciar sesión) confirma que la
  factura existe y muestra sus datos principales.
- Las facturas creadas antes de esta funcionalidad reciben su CUFE al consultarse
  (`FacturaService.asegurarCufe`).

### Pruebas
`CufeGeneradorTest`, `AutenticacionServiceTest` y `ProductoServiceTest`.