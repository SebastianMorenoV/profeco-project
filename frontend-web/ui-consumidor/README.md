# UI Consumidor — PROFECO

Aplicación web para consumidores: búsqueda de productos, comparación de precios entre comercios, exploración de comercios con reseñas y consulta de ofertas vigentes.

## Stack

- React 18
- Vite 5
- React Router 6
- JavaScript (sin TypeScript) + CSS plano

## Requisitos

- Node.js 18+
- Backend levantado: microservicios + Envoy del **gateway-consumidor** escuchando en `http://localhost:8083`

## Configuración

Copia `.env.example` a `.env` si quieres apuntar a otro host:

```
VITE_API_BASE_URL=http://localhost:8083
```

## Instalación y ejecución

```bash
npm install
npm run dev
```

La app queda en `http://localhost:5173`.

## Build de producción

```bash
npm run build
npm run preview
```

## Estructura

```
src/
├── api/              cliente HTTP y wrappers por dominio
│   ├── client.js
│   ├── catalogo.js
│   ├── comercios.js
│   ├── ofertas.js
│   ├── resenias.js
│   └── index.js
├── components/       UI compartido (Layout, StarRating, Loader)
├── hooks/            useFetch
├── pages/            páginas por ruta
├── styles/           CSS global
├── utils/            formatters (MXN, fecha)
├── App.jsx           router
└── main.jsx          entrypoint
```

## Endpoints consumidos (vía Envoy)

| Recurso | Método | Ruta |
|---|---|---|
| Buscar productos | GET | `/api/catalogo/productos?query=&categoria=` |
| Detalle producto | GET | `/api/catalogo/productos/{id}` |
| Precios producto | GET | `/api/catalogo/productos/{id}/precios` |
| Listar comercios | GET | `/api/comercios?ciudad=&tipo_comercio=` |
| Detalle comercio | GET | `/api/comercios/{id}` |
| Reseñas comercio | GET | `/api/resenias/comercio/{id}` |
| Promedio comercio | GET | `/api/resenias/comercio/{id}/promedio` |
| Crear reseña | POST | `/api/resenias` |
| Listar ofertas | GET | `/api/ofertas?solo_activas=true` |
| Ofertas comercio | GET | `/api/ofertas/comercio/{id}` |

## Notas

- El gateway Envoy ya transcodifica JSON ↔ gRPC, por lo que el frontend habla REST estándar.
- No hay autenticación implementada todavía: el formulario de reseñas pide `usuario_id` manual.
- Los nombres de campos provienen del transcoder gRPC-JSON: vienen en `camelCase`.
