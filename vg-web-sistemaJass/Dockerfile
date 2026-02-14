# ============================================
# Stage 1: Build Angular app
# ============================================
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y lockfile para cachear npm install
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Copiar el resto del código fuente
COPY . .

# Build de producción (CSR)
RUN npx ng build --configuration=production

# ============================================
# Stage 2: Servir con Nginx
# ============================================
FROM nginx:alpine

# Eliminar config default de nginx
RUN rm -rf /etc/nginx/conf.d/default.conf /usr/share/nginx/html/*

# Copiar config personalizada
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar los archivos compilados de Angular
COPY --from=build /app/dist/vg-web-sistemaJass/browser /usr/share/nginx/html

# Exponer puerto 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
