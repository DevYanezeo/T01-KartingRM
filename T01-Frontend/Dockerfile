# Usar una imagen base de Node.js
FROM node:18 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos necesarios para instalar dependencias
COPY package.json package-lock.json ./

# Instalar dependencias
RUN npm install

# Copiar todo el código fuente
COPY . ./

# Construir el proyecto
RUN npm run build

# Usar Nginx para servir la aplicación
FROM nginx:alpine

# Copiar el archivo de configuración de Nginx
COPY nginx.conf /etc/nginx/nginx.conf

# Copiar el contenido del directorio build al contenedor
COPY --from=build /app/build /usr/share/nginx/html

# Exponer el puerto 80 para servir la app
EXPOSE 80

# Comando para iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
