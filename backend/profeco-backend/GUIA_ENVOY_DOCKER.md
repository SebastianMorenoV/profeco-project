# Guía de Instalación y Ejecución de Envoy Proxy con Docker

Esta guía te ayudará a instalar Docker en tu entorno de desarrollo (Windows) y te mostrará paso a paso cómo ejecutar los contenedores de Envoy Proxy que hemos configurado para transcodificar las peticiones REST a gRPC en los distintos Gateways de Profeco.

---

## 1. Instalación de Docker en Windows

Dado que Envoy Proxy no ofrece binarios nativos para Windows, la forma oficial y más sencilla de ejecutarlo es utilizando **Docker**.

### Pasos para instalar:
1. **Descargar Docker Desktop:**
   Ve a la página oficial de Docker y descarga el instalador para Windows:
   [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop/)

2. **Instalar:**
   Ejecuta el archivo `.exe` descargado. Asegúrate de mantener marcada la opción de usar **WSL 2 (Windows Subsystem for Linux)** en lugar de Hyper-V, ya que ofrece un rendimiento muy superior.

3. **Reiniciar:**
   Es muy probable que Windows te pida reiniciar tu computadora al finalizar la instalación. Hazlo.

4. **Verificar la instalación:**
   Una vez que tu computadora inicie y abras la aplicación "Docker Desktop" (y veas el motor en verde abajo a la izquierda), abre tu PowerShell o CMD y escribe:
   ```cmd
   docker --version
   ```
   *Deberías ver algo como: `Docker version 24.x.x, build...`*

---

## 2. Preparación de los Descriptores de Envoy

Envoy necesita conocer la estructura de tus mensajes gRPC para poder traducir de JSON a gRPC. Esto se logra mediante los archivos `.pb` (descriptores).

1. Abre una terminal y navega a la carpeta principal de tu backend:
   ```cmd
   cd C:\Ruta\A\Tu\Proyecto\profeco-backend
   ```
2. Ejecuta Maven para compilar todo. Nuestro `pom.xml` ya está configurado para que, además de generar el código Java, exporte automáticamente los archivos `.pb` directamente a la carpeta de Envoy (`infrastructure/envoy/proto/`):
   ```cmd
   mvn clean install
   ```

---

## 3. Ejecutar los Gateways de Envoy en Docker

Hemos configurado 4 puertas de enlace (Gateways) diferentes. Cada una tiene su propio archivo de configuración (`.yaml`) y escucha en un puerto distinto.

Para ejecutar cualquiera de ellos, abre tu terminal y ubícate en la carpeta donde viven las configuraciones:
```cmd
cd C:\Ruta\A\Tu\Proyecto\profeco-backend\infrastructure\envoy
```

A continuación, ejecuta el comando correspondiente para el Gateway que desees levantar.

> **💡 Nota:** El parámetro `-v "%cd%":/etc/envoy` toma la carpeta actual de tu Windows y la mete dentro del contenedor de Linux para que Envoy pueda leer tus archivos `.yaml` y tus carpetas de `/proto/`.

### Gateway Comercio (Puerto 8082)
Enruta hacia el microservicio de Comercio (`ms-comercio` en 9092), Catálogo (9091) y Ofertas (9095).
```cmd
docker run --rm -it -p 8082:8082 -v "%cd%":/etc/envoy envoyproxy/envoy:v1.31-latest -c /etc/envoy/envoy-comercio.yaml
```

### Gateway Consumidor (Puerto 8083)
Enruta hacia Catálogo (9091) y Reseñas (9094).
```cmd
docker run --rm -it -p 8083:8083 -v "%cd%":/etc/envoy envoyproxy/envoy:v1.31-latest -c /etc/envoy/envoy-consumidor.yaml
```

### Gateway Consumidor Móvil (Puerto 8084)
Enruta hacia Catálogo (9091) y Reseñas (9094).
```cmd
docker run --rm -it -p 8084:8084 -v "%cd%":/etc/envoy envoyproxy/envoy:v1.31-latest -c /etc/envoy/envoy-consumidor-movil.yaml
```

### Gateway Profeco (Puerto 8085)
Enruta hacia Multas (9093) y Usuarios (9096).
```cmd
docker run --rm -it -p 8085:8085 -v "%cd%":/etc/envoy envoyproxy/envoy:v1.31-latest -c /etc/envoy/envoy-profeco.yaml
```

---

## 4. Probando la Arquitectura (End-to-End)

Para probar que todo el flujo funciona, sigue este orden exacto:

1. **Levanta tu Microservicio Java:**
   Abre NetBeans y ejecuta (Run) el microservicio objetivo, por ejemplo `ms-comercio`. Verifica en la consola que el servidor gRPC arrancó correctamente en su puerto respectivo (ej. `9092`).
2. **Levanta Envoy:**
   Abre la terminal y ejecuta el comando de Docker para el Gateway Comercio.
3. **Llama a la API (REST):**
   Abre Postman y haz una petición HTTP normal:
   * **URL:** `http://localhost:8082/profeco.comercio.v1.ComercioService/Ping`
   * **Método:** `POST` (o GET si Envoy lo permite).
   * **Headers:** `Content-Type: application/json`
   * **Body:** `{}`

Si Envoy devuelve un `200 OK` con la respuesta en JSON de tu servicio en Java, ¡felicidades, el transcodificador gRPC-JSON está funcionando a la perfección en producción!
