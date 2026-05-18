# ==========================================
# DOCKERFILE: UI-PROFECO (Node.js + Vite)
# ==========================================
# Build context: frontend-web/ui-profeco/
# ==========================================

FROM node:20-alpine
WORKDIR /app

# Copiar archivos de dependencias primero (cache de Docker layers)
COPY package.json package-lock.json ./
RUN npm ci

# Copiar el resto del código fuente y compilar
COPY . .
RUN npm run build

EXPOSE 4173

CMD ["npx", "vite", "preview", "--host", "0.0.0.0"]
