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

Este proyecto implementa interfaces dedicadas para cada rol de usuario, orquestadas a través de un backend robusto que aprovecha la comunicación gRPC inter-servicios y un Gateway de API para el acceso desde clientes externos con soporte nativo para balanceo de cargas.

---

## Arquitectura del Sistema

El ecosistema está totalmente Dockerizado y fragmentado en nodos lógicos que garantizan alta disponibilidad, escalabilidad independiente y tolerancia a fallos.

### Backend (`/backend`)
Desarrollado en Java utilizando Spring Boot, emplea gRPC para la comunicación interna y Envoy Proxy para transcodificación y exposición de APIs hacia los clientes, balanceando la carga dinámicamente (`Least Request`) hacia $N$ instancias del mismo microservicio.

* **Microservicios Centrales (`ms-catalogo`, `ms-comercio`, `ms-usuarios`, `ms-multas`):** Operan en el `servidor-central`.
* **Microservicios Secundarios (`ms-ofertas`, `ms-resenias`):** Operan en el `servidor-secundario`.
* **Infraestructura (`notificaciones`, RabbitMQ, Envoy):** Capa transversal de eventos y enrutamiento.

### Gateways y Frontend (`/frontend-web` y `/docker`)
Cada rol de usuario cuenta con su propia Interfaz Web (React/Vite) empaquetada junto a su respectivo Envoy Proxy (API Gateway) para transcodificar sus peticiones HTTP/JSON a gRPC.

* `ui-comercio` + `gateway-comercio`
* `ui-consumidor` + `gateway-consumidor`
* `ui-profeco` + `gateway-profeco`

---

## Requisitos de Entorno

Para compilar y ejecutar el ecosistema en un entorno local, requieres:

* **Docker y Docker Compose:** (Imprescindibles para orquestar la arquitectura completa).
* **Java Development Kit (JDK 17+) y Maven (3.8+):** Requeridos si se desean compilar los microservicios localmente.
* **Android Studio:** Únicamente para compilar el cliente móvil nativo (`frontend-mobile`).

---

## Guía para Despliegue Local

El proyecto está diseñado para levantarse íntegramente a través de Docker Compose. Todas las dependencias de Java (Maven) y web (Node/React) se descargan y compilan automáticamente dentro de los contenedores Docker usando "Multi-stage builds".

Abre tu terminal (PowerShell recomendado) y sigue estos pasos al pie de la letra:

### 1. Clonar el Repositorio
Primero, descarga el código fuente a tu computadora y entra a la carpeta del proyecto.
```bash
git clone https://github.com/SebastianMorenoV/profeco-project.git
cd profeco-project
```

### 2. Preparar la Red Compartida
Todos los contenedores, sin importar en qué archivo YAML estén, se comunican a través de esta red virtual. Debe crearse primero. Ejecuta:
```bash
docker network create profeco-net
```

### 3. Navegar al directorio de orquestación
Todo el flujo de despliegue de Docker se realiza desde la carpeta `docker`.
```bash
cd docker
```
*(Nota: A partir de este punto, todos los comandos se ejecutan dentro de la carpeta `profeco-project/docker`)*

### 4. Levantar el Middleware
El broker de mensajería (RabbitMQ) debe ser el primero en arrancar para que los microservicios puedan suscribirse a sus colas sin errores.
```bash
docker compose -f docker-compose.middleware.yml up -d --build
```

### 5. Levantar las Bases de Datos y Microservicios
Levantaremos los servidores central y secundario. **¡Opcionalmente puedes escalar las instancias para probar el balanceador de cargas Envoy!**
```bash
# Servidor central (escalando usuarios y catálogo para demostrar balanceo)
docker compose -f docker-compose.servidor-central.yml up -d --build --scale ms-usuarios=3 --scale ms-catalogo=2

# Servidor secundario
docker compose -f docker-compose.servidor-secundario.yml up -d --build
```

### 6. Inyectar Datos Raíz (Seed Data)
Antes de empezar a usar la aplicación, es necesario crear las tablas e insertar los datos iniciales (Usuarios, Productos, etc.) ejecutando el archivo `databases.sql` en cada uno de los contenedores de base de datos.
Copia y pega este bloque completo en tu consola de PowerShell (asegúrate de seguir en la carpeta `docker`):

```powershell
# 1. Base de datos de Usuarios
docker cp ..\requirements\databases.sql db-usuarios:/tmp/databases.sql
docker exec db-usuarios mysql -uroot -pitson -e "source /tmp/databases.sql"

# 2. Base de datos de Comercio
docker cp ..\requirements\databases.sql db-comercio:/tmp/databases.sql
docker exec db-comercio mysql -uroot -pitson -e "source /tmp/databases.sql"

# 3. Base de datos de Catálogo
docker cp ..\requirements\databases.sql db-catalogo:/tmp/databases.sql
docker exec db-catalogo mysql -uroot -pitson -e "source /tmp/databases.sql"

# 4. Base de datos de Ofertas
docker cp ..\requirements\databases.sql db-ofertas:/tmp/databases.sql
docker exec db-ofertas mysql -uroot -pitson -e "source /tmp/databases.sql"

# 5. Base de datos de Reseñas
docker cp ..\requirements\databases.sql db-resenias:/tmp/databases.sql
docker exec db-resenias mysql -uroot -pitson -e "source /tmp/databases.sql"

# 6. Base de datos de Multas
docker cp ..\requirements\databases.sql db-multas:/tmp/databases.sql
docker exec db-multas mysql -uroot -pitson -e "source /tmp/databases.sql"
```

### 7. Levantar Notificaciones
Ahora que RabbitMQ y las bases de datos están listos, levantamos el consumidor de eventos.
```bash
docker compose -f docker-compose.notificaciones.yml up -d --build
```

### 8. Levantar Gateways (Envoy) y Frontends (React)
Finalmente, levantamos los balanceadores de carga y las interfaces gráficas. Cada archivo levantará la interfaz y el proxy del rol correspondiente.
```bash
docker compose -f docker-compose.gateway-comercio.yml up -d --build
docker compose -f docker-compose.gateway-consumidor.yml up -d --build
docker compose -f docker-compose.gateway-profeco.yml up -d --build
docker compose -f docker-compose.gateway-movil.yml up -d --build
```

¡Listo! Todo el ecosistema distribuido está operando. Cualquier petición que pase por los Gateways se enviará automáticamente al microservicio correspondiente y será balanceada usando la estrategia `Least Request` entre todos los contenedores vivos de dicho servicio.

---

## Licencia y Confidencialidad
El código fuente aquí provisto es para propósitos académicos y de demostración. Su uso comercial, distribución o modificación se encuentra sujeto a las normativas de la organización responsable.
