# Sistema Integral PROFECO

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-244c5a?style=for-the-badge&logo=grpc&logoColor=white)
![Envoy Proxy](https://img.shields.io/badge/Envoy_Proxy-E15024?style=for-the-badge&logo=envoyproxy&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-B73BFE?style=for-the-badge&logo=vite&logoColor=FFD62E)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

Un sistema distribuido basado en una arquitectura de microservicios, diseñado para gestionar de manera integral las interacciones entre los consumidores, los comercios y la Procuraduría Federal del Consumidor (PROFECO). 

Este proyecto implementa interfaces dedicadas para cada rol de usuario, orquestadas a través de un backend robusto que aprovecha la comunicación gRPC inter-servicios y un Gateway de API para el acceso desde clientes externos.

---

## Arquitectura del Sistema

El ecosistema está fragmentado en módulos lógicos que garantizan alta disponibilidad, escalabilidad independiente y tolerancia a fallos.

### Backend (`/backend`)
Desarrollado en Java utilizando Spring Boot, emplea gRPC para la comunicación interna y Envoy Proxy para transcodificación y exposición de APIs hacia los clientes.

* **Microservicios (`/microservices`):**
  * `ms-catalogo`: Gestión y mantenimiento del catálogo de productos.
  * `ms-comercio`: Administración de entidades comerciales y validación de perfiles.
  * `ms-multas`: Motor de generación, asignación y seguimiento de multas.
  * `ms-ofertas`: Controlador de reglas de negocio para promociones y ofertas vigentes.
  * `ms-resenias`: Procesamiento de calificaciones y opiniones generadas por los usuarios.
  * `ms-usuarios`: Gestión de identidades, perfiles, y preferencias del consumidor.
  * `notificaciones`: Servicio encargado de emitir alertas y comunicaciones.
* **Gateway (`/infrastructure/envoy`):** Instancias de Envoy Proxy encapsuladas en Docker que fungen como API Gateways independientes (`gateway-comercio`, `gateway-consumidor`, `gateway-consumidor-movil`, `gateway-profeco`), realizando enrutamiento, balanceo de carga y transcodificación de gRPC a JSON/REST.
* **Contratos gRPC (`/common-grpc`):** Definición centralizada de esquemas `.proto` utilizados para la generación de stubs y contratos de comunicación en todo el ecosistema.

### Frontend Web (`/frontend-web`)
Aplicaciones Single-Page (SPA) de alto rendimiento, optimizadas con Vite y desarrolladas en React.

* `ui-comercio`: Plataforma de gestión B2B para que los negocios operen sus catálogos y respondan métricas.
* `ui-consumidor`: Portal B2C orientado al cliente final para exploración, gestión de listas y seguimiento.
* `ui-profeco`: Consola de administración gubernamental exclusiva para operaciones de auditoría y sanciones.

### Frontend Móvil (`/frontend-mobile`)
* `app-consumidor`: Aplicación nativa Android desarrollada en Kotlin, ofreciendo un canal móvil optimizado para la experiencia del usuario final.

---

## Requisitos de Entorno

Para compilar y ejecutar el ecosistema en un entorno de desarrollo local, es estrictamente necesario contar con las siguientes herramientas instaladas y configuradas en tu `PATH`:

* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Maven:** Versión 3.8+ (para resolución de dependencias, compilación de binarios y generación de Stubs de gRPC).
* **Node.js y NPM:** Versión 18+ (para el despliegue de clientes web).
* **Docker y Docker Compose:** Fundamentales para orquestar los microservicios y Envoy Proxies.
* **Bases de Datos Host:** Instancias de **MySQL** (puerto 3306) y **RabbitMQ** (puerto 5672) operando de manera nativa en el Host, o virtualizadas bajo puertos accesibles en `host.docker.internal`.
* **Android Studio:** Última versión estable (obligatorio únicamente para la compilación y pruebas del entorno móvil).

---

## Guía Detallada de Despliegue Local

El proyecto requiere una orquestación estricta, dado que los contenedores Docker dependen de los binarios de Java pre-compilados y de una red virtual compartida. Sigue estos pasos en orden secuencial:

### 1. Preparación de la Red Docker
Todos los contenedores del ecosistema se comunican mediante una red virtual compartida llamada `profeco-net`. Debes crearla manualmente antes de invocar los archivos de compose:
```bash
docker network create profeco-net
```

### 2. Compilación de Binarios (Java y gRPC)
Los archivos `Dockerfile` de cada microservicio no compilan código fuente, sino que copian los archivos `.jar` ya ensamblados. Es obligatorio ejecutar la fase de compilación local de Maven desde la raíz del backend:
```bash
cd backend/profeco-backend
mvn clean install -DskipTests
```
*Nota: El argumento `-DskipTests` se sugiere para acelerar el despliegue local inicial. Este proceso resolverá las dependencias de `common-grpc` y construirá los artefactos (`.jar`) en el directorio `/target` de cada microservicio y proxy.*

### 3. Orquestación del API Gateway (Envoy Proxies)
Una vez que los proto-descriptors están generados, se debe inicializar el enjambre de Proxies Envoy que manejarán las peticiones REST.
```bash
cd backend/profeco-backend/infrastructure/envoy
docker-compose up -d --build
```
*Nota: Esto levantará contenedores independientes para los gateways de comercio (8082), consumidor (8083), consumidor móvil (8084) y profeco (8085).*

### 4. Despliegue de los Microservicios
Con los `.jar` listos y la red establecida, se inicializan todos los microservicios del negocio, los cuales se enlazarán a las bases de datos de tu host local (`host.docker.internal`):
```bash
cd backend/profeco-backend/microservices
docker-compose up -d --build
```
Para verificar la salud de los contenedores o leer los logs en vivo, puedes ejecutar: `docker compose logs -f ms-catalogo` (reemplaza por el nombre del servicio deseado).

### 5. Inicialización de Aplicaciones Web Frontend
Las aplicaciones web operan independientemente de Docker. Para iniciar cualquiera de ellas, navega a su directorio, instala los paquetes e inicializa el servidor Vite:
```bash
# Ejemplo usando ui-consumidor:
cd frontend-web/ui-consumidor
npm install
npm run dev
```

### 6. Compilación del Entorno Móvil
1. Abre **Android Studio**.
2. Selecciona `File > Open` y elige el directorio `frontend-mobile/app-consumidor`.
3. Espera a que la Sincronización Gradle finalice por completo.
4. Selecciona tu emulador o dispositivo físico y haz clic en `Run` (o presiona `Shift + F10`).

---

## Licencia y Confidencialidad
El código fuente aquí provisto es para propósitos académicos y de demostración. Su uso comercial, distribución o modificación se encuentra sujeto a las normativas de la organización responsable.
