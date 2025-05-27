# Documentación Completa de Docker para el Proyecto

## Índice
1. [Introducción](#introducción)
2. [Requisitos Previos](#requisitos-previos)
3. [Instalación de Docker](#instalación-de-docker)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Configuración del Dockerfile](#configuración-del-dockerfile)
6. [Proceso de Construcción](#proceso-de-construcción)
7. [Ejecución del Contenedor](#ejecución-del-contenedor)
8. [Recursos Adicionales](#recursos-adicionales)
9. [Solución de Problemas Comunes](#solución-de-problemas-comunes)
10. [Mejores Prácticas](#mejores-prácticas)
11. [Seguridad](#seguridad)
12. [Monitoreo y Mantenimiento](#monitoreo-y-mantenimiento)

## Introducción
Este documento proporciona una guía exhaustiva sobre la implementación y ejecución de Docker en nuestro proyecto. Docker es una plataforma de contenedorización que nos permite empaquetar nuestra aplicación Java junto con todas sus dependencias en un contenedor aislado. Esto garantiza que la aplicación funcione de manera consistente en cualquier entorno, desde desarrollo hasta producción, eliminando el problema clásico de "funciona en mi máquina".

### Beneficios de Usar Docker
- **Consistencia**: El mismo entorno en todas las etapas del desarrollo
- **Aislamiento**: Cada contenedor funciona de manera independiente
- **Portabilidad**: Fácil despliegue en diferentes entornos
- **Escalabilidad**: Simple replicación de contenedores
- **Eficiencia**: Mejor uso de recursos del sistema

## Requisitos Previos
### Hardware Mínimo Recomendado
- CPU: 2 núcleos o más
- RAM: 4GB mínimo (8GB recomendado)
- Disco: 20GB de espacio libre

### Software Requerido
1. **Sistema Operativo**:
   - Windows 10/11 Pro, Enterprise o Education (64-bit)
   - Linux (Ubuntu 20.04 LTS o superior)
   - macOS 10.15 o superior

2. **Docker Desktop**:
   - Versión estable más reciente
   - WSL 2 (para Windows)

3. **Herramientas Adicionales**:
   - Git (versión 2.30.0 o superior)
   - Maven (versión 3.9.0 o superior)
   - Java JDK 21 (Amazon Corretto)

## Instalación de Docker
### Windows
1. **Preparación del Sistema**:
   - Habilitar Hyper-V y Virtualización en BIOS
   - Instalar WSL 2 si no está presente
   - Actualizar Windows al último Service Pack

2. **Instalación de Docker Desktop**:
   - Descargar el instalador desde [docker.com](https://www.docker.com/products/docker-desktop)
   - Ejecutar el instalador como administrador
   - Aceptar los términos de licencia
   - Seleccionar la configuración recomendada
   - Completar la instalación y reiniciar

3. **Verificación de la Instalación**:
   ```bash
   docker --version
   docker-compose --version
   docker run hello-world
   ```

### Linux (Ubuntu)
1. **Preparación del Sistema**:
   ```bash
   sudo apt-get update
   sudo apt-get install apt-transport-https ca-certificates curl gnupg lsb-release
   ```

2. **Agregar la Clave GPG Oficial**:
   ```bash
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
   ```

3. **Configurar el Repositorio**:
   ```bash
   echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
   ```

4. **Instalar Docker Engine**:
   ```bash
   sudo apt-get update
   sudo apt-get install docker-ce docker-ce-cli containerd.io
   ```

5. **Verificar la Instalación**:
   ```bash
   sudo docker run hello-world
   ```

### macOS
1. **Requisitos del Sistema**:
   - macOS 10.15 o superior
   - Procesador Intel o Apple Silicon
   - 4GB de RAM mínimo

2. **Instalación**:
   - Descargar Docker Desktop para Mac
   - Arrastrar la aplicación a la carpeta Applications
   - Iniciar Docker Desktop
   - Completar la configuración inicial

3. **Configuración Inicial**:
   - Ajustar recursos asignados (CPU, RAM)
   - Configurar el proxy si es necesario
   - Establecer el directorio de volúmenes

## Estructura del Proyecto
```
proyecto/
├── Dockerfile              # Configuración del contenedor
├── .dockerignore          # Archivos a excluir del build
├── pom.xml                # Configuración de Maven
├── src/
│   └── main/
│       ├── java/         # Código fuente Java
│       └── resources/    # Recursos de la aplicación
├── wait-for-it.sh        # Script de espera para dependencias
└── docker-compose.yml    # Configuración de servicios (opcional)
```

## Configuración del Dockerfile
Nuestro Dockerfile implementa una arquitectura multi-etapa para optimizar el tamaño final de la imagen y mejorar la seguridad:

### Etapa de Construcción
```dockerfile
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests
```

**Explicación Detallada**:
1. **Imagen Base**: Utilizamos `maven:3.9-amazoncorretto-21` que incluye:
   - Maven 3.9 para la construcción
   - Amazon Corretto JDK 21
   - Herramientas de desarrollo necesarias

2. **Gestión de Dependencias**:
   - `mvn dependency:go-offline` descarga todas las dependencias
   - Esto acelera builds posteriores al cachear las dependencias

3. **Compilación**:
   - Se copia el código fuente
   - Se ejecuta el empaquetado con Maven
   - Se omite la ejecución de tests para acelerar el build

### Etapa de Ejecución
```dockerfile
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN apk add --no-cache bash curl
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
EXPOSE 8080
CMD ["/bin/bash", "-c", "java -jar app.jar"]
```

**Explicación Detallada**:
1. **Imagen Base Ligera**:
   - `amazoncorretto:21-alpine` es una imagen minimalista
   - Reduce significativamente el tamaño final
   - Mantiene solo lo necesario para ejecutar la aplicación

2. **Configuración del Entorno**:
   - Se establece el directorio de trabajo
   - Se copia el JAR compilado
   - Se instalan herramientas esenciales

3. **Script de Inicio**:
   - `wait-for-it.sh` espera a que las dependencias estén disponibles
   - Se establecen los permisos de ejecución
   - Se configura el comando de inicio

## Proceso de Construcción
### 1. Construcción de la Imagen
```bash
# Construir la imagen con un nombre específico
docker build -t nombre-proyecto:1.0.0 .

# Construir con caché deshabilitado (para builds limpios)
docker build --no-cache -t nombre-proyecto:1.0.0 .

# Construir con argumentos de build
docker build --build-arg JAVA_OPTS="-Xmx512m" -t nombre-proyecto:1.0.0 .
```

### 2. Verificación de la Imagen
```bash
# Listar todas las imágenes
docker images

# Inspeccionar detalles de la imagen
docker inspect nombre-proyecto:1.0.0

# Ver el historial de construcción
docker history nombre-proyecto:1.0.0
```

## Ejecución del Contenedor
### 1. Ejecución Básica
```bash
# Ejecutar en primer plano
docker run -p 8080:8080 nombre-proyecto:1.0.0

# Ejecutar en segundo plano
docker run -d -p 8080:8080 --name mi-contenedor nombre-proyecto:1.0.0

# Ejecutar con variables de entorno
docker run -e "SPRING_PROFILES_ACTIVE=prod" -p 8080:8080 nombre-proyecto:1.0.0
```

### 2. Gestión del Contenedor
```bash
# Ver contenedores en ejecución
docker ps

# Ver todos los contenedores (incluyendo detenidos)
docker ps -a

# Detener un contenedor
docker stop mi-contenedor

# Eliminar un contenedor
docker rm mi-contenedor

# Ver logs del contenedor
docker logs mi-contenedor
```

## Recursos Adicionales
- [Presentación del Proyecto](https://www.canva.com/design/DAGlgDj8gAI/49hn-JQCm4f1ASTVEkKU2g/edit?utm_content=DAGlgDj8gAI&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)
- [Documentación Oficial de Docker](https://docs.docker.com/)
- [Guía de Amazon Corretto](https://docs.aws.amazon.com/corretto/)
- [Best Practices for Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Docker Security Best Practices](https://docs.docker.com/engine/security/security/)

## Solución de Problemas Comunes
### 1. Problemas de Permisos
```bash
# Agregar usuario al grupo docker
sudo usermod -aG docker $USER

# Verificar permisos
docker info

# Solucionar problemas de permisos en WSL
sudo chown "$USER":"$USER" /home/"$USER"/.docker -R
```

### 2. Problemas de Memoria
1. **Configuración de Docker Desktop**:
   - Ajustar límites de memoria en Preferences > Resources
   - Configurar CPU y memoria según necesidades

2. **Optimización de Contenedores**:
   ```bash
   # Limitar memoria del contenedor
   docker run -m 512m --memory-swap 1G nombre-proyecto:1.0.0
   
   # Limitar CPU
   docker run --cpus 1.5 nombre-proyecto:1.0.0
   ```

### 3. Problemas de Red
1. **Verificación de Puertos**:
   ```bash
   # Ver puertos en uso
   netstat -tuln | grep 8080
   
   # Ver puertos expuestos por contenedores
   docker port mi-contenedor
   ```

2. **Configuración de Red**:
   ```bash
   # Crear red personalizada
   docker network create mi-red
   
   # Conectar contenedor a red
   docker run --network mi-red nombre-proyecto:1.0.0
   ```

## Mejores Prácticas
### 1. Optimización de Imágenes
- Usar imágenes base oficiales y verificadas
- Implementar multi-stage builds
- Minimizar el número de capas
- Usar .dockerignore apropiadamente

### 2. Gestión de Dependencias
- Mantener dependencias actualizadas
- Usar versiones específicas de imágenes
- Implementar escaneo de vulnerabilidades

### 3. Seguridad
- No ejecutar contenedores como root
- Usar secrets para información sensible
- Implementar escaneo de imágenes
- Mantener imágenes actualizadas

## Seguridad
### 1. Buenas Prácticas de Seguridad
1. **Gestión de Secretos**:
   ```bash
   # Usar Docker Secrets
   echo "mi-secreto" | docker secret create db-password -
   
   # Montar secretos en contenedor
   docker service create --secret db-password nombre-proyecto:1.0.0
   ```

2. **Escaneo de Vulnerabilidades**:
   ```bash
   # Instalar trivy
   brew install trivy
   
   # Escanear imagen
   trivy image nombre-proyecto:1.0.0
   ```

### 2. Hardening del Contenedor
1. **Configuración de Seguridad**:
   ```dockerfile
   # Ejecutar como usuario no-root
   USER nobody
   
   # Configurar capabilities
   RUN setcap 'cap_net_bind_service=+ep' /app/app.jar
   ```

2. **Políticas de Seguridad**:
   - Implementar AppArmor o SELinux
   - Usar namespaces de forma apropiada
   - Configurar límites de recursos

## Monitoreo y Mantenimiento
### 1. Monitoreo de Contenedores
```bash
# Ver estadísticas en tiempo real
docker stats

# Ver uso de recursos
docker container top mi-contenedor

# Inspeccionar contenedor
docker inspect mi-contenedor
```

### 2. Mantenimiento Regular
1. **Limpieza de Recursos**:
   ```bash
   # Eliminar contenedores detenidos
   docker container prune
   
   # Eliminar imágenes no utilizadas
   docker image prune
   
   # Eliminar todos los recursos no utilizados
   docker system prune
   ```

2. **Actualización de Imágenes**:
   ```bash
   # Actualizar imagen base
   docker pull amazoncorretto:21-alpine
   
   # Reconstruir con imagen actualizada
   docker build --pull -t nombre-proyecto:1.0.0 .
   ```

## Funcionamiento Detallado del Dockerfile y Docker Compose

### Dockerfile: Construcción de Imágenes

#### Estructura Básica
```dockerfile
# Etapa de construcción
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Etapa de ejecución
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN apk add --no-cache bash curl
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
EXPOSE 8080
CMD ["/bin/bash", "-c", "java -jar app.jar"]
```

#### Explicación de Comandos
1. **FROM**: 
   - Define la imagen base
   - `maven:3.9-amazoncorretto-21` para construcción
   - `amazoncorretto:21-alpine` para ejecución

2. **WORKDIR**:
   - Establece el directorio de trabajo
   - Crea el directorio si no existe
   - Afecta a todos los comandos siguientes

3. **COPY**:
   - Copia archivos del host al contenedor
   - `COPY pom.xml .` copia el archivo de dependencias
   - `COPY src ./src` copia el código fuente

4. **RUN**:
   - Ejecuta comandos durante la construcción
   - `mvn dependency:go-offline` descarga dependencias
   - `mvn package` compila el proyecto

5. **EXPOSE**:
   - Documenta el puerto que la aplicación usa
   - No publica el puerto automáticamente
   - Se usa con `-p` en `docker run`

6. **CMD**:
   - Define el comando por defecto al iniciar
   - Solo puede haber un CMD por Dockerfile
   - Se puede sobrescribir al ejecutar

### Docker Compose: Orquestación de Servicios

#### Estructura Básica
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    image: nombre-proyecto:1.0.0
    container_name: mi-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:mysql://db:3306/mydb
    depends_on:
      - db
    networks:
      - app-network

  db:
    image: mysql:8.0
    container_name: mi-db
    environment:
      - MYSQL_ROOT_PASSWORD=rootpass
      - MYSQL_DATABASE=mydb
      - MYSQL_USER=user
      - MYSQL_PASSWORD=pass
    volumes:
      - db-data:/var/lib/mysql
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  db-data:
```

#### Explicación de Componentes

1. **Servicios**:
   - `app`: Nuestra aplicación Java
   - `db`: Base de datos MySQL
   - Cada servicio se ejecuta en su propio contenedor

2. **Configuración de Build**:
   ```yaml
   build:
     context: .
     dockerfile: Dockerfile
   ```
   - Define cómo construir la imagen
   - `context` especifica el directorio de construcción
   - `dockerfile` especifica el archivo a usar

3. **Configuración de Red**:
   ```yaml
   networks:
     app-network:
       driver: bridge
   ```
   - Crea una red aislada
   - Permite comunicación entre servicios
   - Usa el driver bridge por defecto

4. **Volúmenes**:
   ```yaml
   volumes:
     db-data:
   ```
   - Persiste datos entre reinicios
   - Comparte datos entre contenedores
   - Mejora el rendimiento

5. **Variables de Entorno**:
   ```yaml
   environment:
     - SPRING_PROFILES_ACTIVE=prod
     - DB_URL=jdbc:mysql://db:3306/mydb
   ```
   - Configura el entorno de ejecución
   - Puede usar archivos .env
   - Sobrescribe variables del Dockerfile

#### Comandos de Docker Compose

1. **Iniciar Servicios**:
   ```bash
   # Iniciar todos los servicios
   docker-compose up
   
   # Iniciar en segundo plano
   docker-compose up -d
   
   # Reconstruir e iniciar
   docker-compose up --build
   ```

2. **Detener Servicios**:
   ```bash
   # Detener todos los servicios
   docker-compose down
   
   # Detener y eliminar volúmenes
   docker-compose down -v
   ```

3. **Ver Estado**:
   ```bash
   # Ver logs
   docker-compose logs
   
   # Ver estado
   docker-compose ps
   
   # Ver estadísticas
   docker-compose stats
   ```

4. **Ejecutar Comandos**:
   ```bash
   # Ejecutar comando en servicio
   docker-compose exec app bash
   
   # Ejecutar comando en servicio específico
   docker-compose run app mvn test
   ```

#### Ventajas de Docker Compose

1. **Simplificación**:
   - Un solo comando para múltiples contenedores
   - Configuración centralizada
   - Fácil reproducción de entornos

2. **Orquestación**:
   - Control de dependencias entre servicios
   - Gestión de redes compartidas
   - Manejo de volúmenes

3. **Desarrollo**:
   - Entorno consistente
   - Fácil configuración
   - Rápida iteración

4. **Producción**:
   - Configuración como código
   - Fácil despliegue
   - Escalabilidad

#### Mejores Prácticas

1. **Versionado**:
   - Usar versiones específicas de imágenes
   - Mantener docker-compose.yml en control de versiones
   - Documentar cambios

2. **Seguridad**:
   - Usar variables de entorno para secretos
   - Limitar recursos por servicio
   - Implementar health checks

3. **Mantenimiento**:
   - Limpiar recursos no utilizados
   - Actualizar imágenes regularmente
   - Monitorear uso de recursos

4. **Optimización**:
   - Usar multi-stage builds
   - Implementar caching
   - Optimizar capas de imagen 