<div align="center">

<a href="https://juanjogamero.github.io/API-ProyectoCocina/">
<img src="docs/readme-cover.svg" alt="Proyecto Cocina: plataforma de chefs a domicilio" width="100%">
</a>

# Proyecto Cocina
### Plataforma de chefs y contratación de servicios gastronómicos

**Descubre chefs, consulta sus especialidades y explora cómo se gestiona una contratación desde una API REST.**

[**Explorar la demo interactiva**](https://juanjogamero.github.io/API-ProyectoCocina/) · [**Ver el backend Java**](src/main/java/proyecto) · [**Guía técnica**](Documentacion/ARQUITECTURA.md)

</div>

---

## Un proyecto, dos experiencias

Este proyecto nació como un **trabajo académico colaborativo** de Desarrollo de Aplicaciones Multiplataforma (DAM). Su objetivo es conectar a **clientes que buscan un chef a domicilio** con profesionales de la cocina y gestionar sus solicitudes de contratación.

La solución incluye dos partes claramente diferenciadas:

| **Demo web interactiva** | **API REST con Java** |
| --- | --- |
| Experiencia editorial y adaptable para descubrir chefs. | Registro de clientes y chefs, con contraseñas protegidas. |
| Búsqueda por especialidad, fichas y favoritos. | Autenticación JWT y permisos según el rol. |
| Solicitudes de ejemplo, cambios de estado y cancelaciones. | Contrataciones, fechas, disponibilidad y valoraciones. |
| HTML, CSS, JavaScript y almacenamiento local. | Spring Boot, PostgreSQL y persistencia JPA. |

**[Abrir Proyecto Cocina en el navegador](https://juanjogamero.github.io/API-ProyectoCocina/)**

> **Importante:** la web publicada en GitHub Pages es una **demostración independiente**, con chefs, valoraciones, precios y reservas ficticios. Los datos se guardan únicamente en tu navegador. **No se realizan reservas reales, pagos ni peticiones a la API Java.** Los perfiles usan fotografías gastronómicas ilustrativas, no retratos de profesionales reales.

## Tecnologías

![Java 17](https://img.shields.io/badge/Java-17-b65f41?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-autenticaci%C3%B3n-203c31?style=flat-square)
![JavaScript](https://img.shields.io/badge/JavaScript-demo_web-323330?style=flat-square&logo=javascript&logoColor=F7DF1E)

La API emplea **Java 17**, Spring Boot, Spring Security, Spring Data JPA, Hibernate, PostgreSQL, JWT y Swagger/OpenAPI. La demo utiliza **HTML, CSS y JavaScript sin frameworks** y se publica con GitHub Pages.

## Ejecutar la API localmente

Requisitos: **Java 17** y **Docker** (o PostgreSQL configurado en local).

Desde la raíz del repositorio:

~~~bash
docker compose up -d
export JWT_SECRET="$(openssl rand -base64 48)"
./mvnw spring-boot:run
~~~

En **Windows PowerShell**, arranca primero PostgreSQL y establece una clave antes de ejecutar el servidor:

~~~powershell
docker compose up -d
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
.\mvnw.cmd spring-boot:run
~~~

Abre **[Swagger UI](http://localhost:8080/swagger-ui.html)** para explorar los endpoints de la API y probar el registro, inicio de sesión y contrataciones.

El arranque requiere una clave <code>JWT_SECRET</code> propia de al menos **32 bytes**. Sin ella, la aplicación no se inicia deliberadamente. Las credenciales PostgreSQL por defecto del archivo <code>docker-compose.yml</code> son solo para desarrollo local.

### Probar el flujo

**1. Registrar un cliente**: <code>POST /auth/registro</code>

~~~json
{
  "username": "cliente_demo",
  "password": "UnaClaveSegura2026",
  "nombre": "María",
  "apellido1": "García",
  "correo": "maria@example.com",
  "telefono": "612345678",
  "rol": "CLIENTE",
  "codigoPostal": "41001",
  "alergias": []
}
~~~

**2. Registrarte como chef**: utiliza la misma ruta con otro nombre de usuario, rol <code>CHEF</code> y <code>precioServicio: 90</code>. También puedes consultar los chefs públicamente en <code>GET /chef</code>.

**3. Iniciar sesión**: <code>POST /auth/login</code> devuelve un token JWT. En Swagger, utiliza **Authorize → Bearer** para probar las rutas protegidas.

**4. Solicitar una contratación**: <code>POST /contrato</code>, autenticado como cliente. Indica el ID real del chef registrado, una fecha futura y, si quieres, un comentario.

**5. Gestionar la solicitud**: el chef puede confirmar y completar el contrato; el cliente puede cancelar una solicitud o valorar un servicio completado. Los cambios de estado están validados por la API.

> La API comienza sin chefs ni clientes de ejemplo: tendrás que registrarlos. La demo web sí incluye perfiles ficticios para poder explorarla desde el primer momento.

## Estructura

~~~text
src/main/java/proyecto/
  controller/     Endpoints HTTP y respuestas REST
  dto/            Peticiones y respuestas públicas
  entity/         Usuarios, chefs, clientes y contratos
  respository/    Repositorios JPA (nombre del paquete original)
  security/       JWT, contraseñas y autorización
  service/        Reglas de negocio y contrataciones
  config/         Swagger y administrador local opcional

docs/             Demo visual estática publicada en GitHub Pages
src/test/         Pruebas automatizadas
Documentacion/    Arquitectura, permisos y flujos
~~~

Puedes ejecutar las pruebas del backend con <code>./mvnw test</code>. Las verificaciones de GitHub Actions también incluyen las pruebas de la demo.

## Contexto y autoría

Esta versión de portfolio continúa un **proyecto desarrollado con un compañero de clase**. El repositorio es un **fork** de [Josemajr6/GestionCocina-Spring](https://github.com/Josemajr6/GestionCocina-Spring); se conserva su procedencia y la autoría original. Las mejoras de seguridad, documentación y experiencia web se han añadido sobre esa base.

**Estado:** proyecto académico y demostración técnica; no es un marketplace operativo. No hay pagos, identidad verificada ni un sistema de reservas público listo para producción.

<div align="center">

---

**[Ver la demostración de Proyecto Cocina](https://juanjogamero.github.io/API-ProyectoCocina/)**

<sub>Proyecto Cocina · Java · Spring Boot · PostgreSQL · GitHub Pages</sub>

</div>
