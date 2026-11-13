# Proyecto Cocina · Arquitectura y referencia de la API

La finalidad del proyecto es gestionar perfiles de chefs y clientes y solicitudes de contratación de servicios gastronómicos. La demo web de GitHub Pages funciona de forma independiente y **no consume los endpoints del servidor**.

## Arquitectura

~~~mermaid
flowchart TD
    C[Cliente API / Swagger] --> HTTP[Controladores REST]
    HTTP --> SEC[Spring Security · JWT]
    HTTP --> S[Servicios y reglas de negocio]
    S --> R[Repositorios Spring Data JPA]
    R --> DB[(PostgreSQL)]
    WEB[Demo de GitHub Pages] --> LS[(localStorage del navegador)]
~~~

Las capas del backend conservan los paquetes del proyecto original, incluido el nombre <code>respository</code> para evitar una reorganización que no aporta funcionalidad.

## Autenticación y roles

- **Público:** registro de clientes o chefs, inicio de sesión y consulta de chefs activos.
- **CLIENTE:** consultar sus contratos, crear solicitudes, cancelar contratos permitidos y valorar los servicios completados.
- **CHEF:** actualizar su perfil, consultar sus contratos y cambiar su estado según las transiciones admitidas.
- **ADMINISTRADOR:** gestión de usuarios y modificación administrativa de contratos. No puede registrarse desde el endpoint público.
- **JWT:** contraseña BCrypt en base de datos, token firmado con HS256 y clave privada suministrada en la variable de entorno <code>JWT_SECRET</code>.

Se puede iniciar un administrador local de prueba proporcionando <code>APP_ADMIN_USER</code> y <code>APP_ADMIN_PASSWORD</code> al arrancar. No se crea ninguna cuenta administrativa por defecto.

## Endpoints principales

| Método | Ruta | Acceso | Operación |
| --- | --- | --- | --- |
| POST | <code>/auth/registro</code> | Público | Registrar chef o cliente |
| POST | <code>/auth/login</code> | Público | Obtener JWT |
| GET | <code>/chef</code> | Público | Listar chefs activos |
| GET | <code>/chef/{id}</code> | Público | Consultar un chef |
| GET | <code>/chef/me</code> | Chef | Consultar su perfil |
| PUT | <code>/chef/{id}</code> | Chef propietario / Admin | Actualizar perfil |
| GET | <code>/cliente/me</code> | Cliente | Consultar su perfil |
| GET | <code>/contrato</code> | Autenticado | Listar los contratos que puede ver |
| GET | <code>/contrato/{id}</code> | Participante / Admin | Consultar un contrato |
| POST | <code>/contrato</code> | Cliente | Solicitar contratación |
| PUT | <code>/contrato/{id}/estado</code> | Participante autorizado / Admin | Confirmar, completar o cancelar |
| PUT | <code>/contrato/{id}/valoracion</code> | Cliente del contrato | Valorar servicio completado |

Hay rutas administrativas adicionales para consultar o gestionar perfiles. Revisa **Swagger** para la referencia generada automáticamente.

## Reglas relevantes

Un chef no acepta reservas solapadas en la misma fecha si tiene otra contratación **pendiente o confirmada**. La creación de un contrato bloquea el registro del chef durante la comprobación de disponibilidad para impedir solicitudes simultáneas sobre la misma fecha.

Los contratos comienzan como <code>PENDIENTE</code>, pueden pasar a <code>CONFIRMADO</code> o <code>CANCELADO</code> y, una vez confirmados y realizada la fecha del servicio, a <code>COMPLETADO</code>. Los estados se almacenan como texto.

Las valoraciones de 1 a 5 solo se permiten al **cliente del contrato**, después de completarlo y una sola vez.

## Configuración

| Variable | Necesaria | Uso |
| --- | --- | --- |
| <code>JWT_SECRET</code> | Sí | Clave HS256 aleatoria, mínimo 32 bytes |
| <code>DB_URL</code> | No en local | URL JDBC de PostgreSQL |
| <code>DB_USER</code> | No en local | Usuario de PostgreSQL |
| <code>DB_PASSWORD</code> | No en local | Contraseña de PostgreSQL |
| <code>PORT</code> | No | Puerto HTTP (8080) |
| <code>APP_ADMIN_USER</code> | Opcional | Cuenta de administrador local |
| <code>APP_ADMIN_PASSWORD</code> | Opcional | Contraseña inicial del administrador |

No se almacenan secretos reales en el repositorio. Se deben proporcionar en variables de entorno. La configuración de Hibernate con <code>ddl-auto=update</code> está pensada para una base de desarrollo, no para gestionar migraciones de producción.

## Pruebas

~~~bash
./mvnw --batch-mode verify
node --check docs/app.js
node scripts/check-demo.mjs
~~~

La suite de Java utiliza JUnit, Mockito y H2 en modo de compatibilidad con PostgreSQL para los tests de contexto. Se prueban reglas de roles, reservas duplicadas y tokens JWT.

## Limitaciones

- **El backend se ejecuta en local** y no está alojado públicamente.
- **La demo web usa datos ficticios**, y su simulación de reservas no crea contratos en PostgreSQL.
- No existen pagos, comunicaciones con chefs ni verificación de identidad.
- La base original puede requerir una migración explícita antes de utilizar el nuevo modelo de entidades y la biblioteca JWT; no conviene aplicar esta versión sobre una base de datos con información valiosa sin copia de seguridad.
- Es un trabajo académico colaborativo y conserva la procedencia del [repositorio original](https://github.com/Josemajr6/GestionCocina-Spring).
