# 🤖 AVANCE - Sistema de Creación de IA Personalizada

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![OpenAI](https://img.shields.io/badge/OpenAI-API-lightgrey)](https://openai.com/api/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green.svg)](https://www.thymeleaf.org/)

<p align="center">
  <img src="https://via.placeholder.com/800x400?text=AVANCE+IA+Personalizada" alt="AVANCE Logo" width="600"/>
</p>

## 📋 Contenido

- [✨ Descripción](#-descripción)
- [🚀 Características](#-características)
- [🔧 Requisitos técnicos](#-requisitos-técnicos)
- [⚙️ Instalación](#️-instalación)
- [📘 Guía de uso rápido](#-guía-de-uso-rápido)
- [🏗️ Arquitectura](#️-arquitectura)
- [🧪 Tecnologías utilizadas](#-tecnologías-utilizadas)
- [🤝 Contribuir](#-contribuir)
- [📝 Licencia](#-licencia)

## ✨ Descripción

AVANCE democratiza el acceso a la IA personalizada, brindando una solución flexible y accesible que facilita la integración de asistentes inteligentes en distintos ámbitos, desde el uso personal hasta aplicaciones empresariales.

El sistema permite a los usuarios configurar su IA **sin necesidad de conocimientos avanzados** en programación o aprendizaje automático. Incluye herramientas para ajustar el dominio de conocimiento, establecer reglas de respuesta y entrenar modelos de conversación.

<p align="center">
  <img src="https://via.placeholder.com/600x300?text=Dashboard+Preview" alt="Dashboard Preview" width="600"/>
</p>

## 🚀 Características

### 🎛️ Creación y Personalización de IA
- Definición del propósito y comportamiento del asistente
- Personalización del tono, estilo y tipo de respuestas
- Entrenamiento con conocimientos específicos

### 🔍 Configuración de Alcance y Restricciones
- Establecimiento de temas o áreas de conocimiento
- Configuración de filtros para respuestas

### 🔌 Integración con Plataformas Externas
- Conexión con servicios de mensajería
- Compatibilidad con APIs de Procesamiento de Lenguaje Natural
- Posibilidad de incrustar la IA en sitios web o aplicaciones

### 📊 Entrenamiento y Aprendizaje Continuo
- Ajuste y mejora basados en interacciones previas
- Análisis y corrección manual

### 🖥️ Interfaz Intuitiva
- Panel de control con métricas
- Editor visual para flujos de conversación

## 🔧 Requisitos técnicos

| Requisito | Versión | Descripción |
|-----------|---------|-------------|
| ☕ Java | 21+ | Entorno de ejecución |
| 📦 Maven | 3.8+ | Gestor de dependencias |
| 🔑 API Key | OpenAI | Clave para integración con modelos de IA |
| 💾 Memoria | 4 GB+ | RAM recomendada para desarrollo |

## ⚙️ Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/tuusuario/avance.git
cd avance
```

### 2. Configurar la API key de OpenAI

#### Opción 1: En application.properties
```properties
openai.api.key=tu-api-key-aquí
openai.api.url=https://api.openai.com/v1
openai.model.default=gpt-3.5-turbo
```

#### Opción 2: Como variable de entorno
```bash
export OPENAI_API_KEY=tu-api-key-aquí
```

### 3. Compilar el proyecto
```bash
mvn clean install
```

### 4. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 5. Acceder a la aplicación
```
http://localhost:8080
```

## 📘 Guía de uso rápido

<p align="center">
  <img src="https://via.placeholder.com/800x400?text=Flujo+de+trabajo" alt="Workflow" width="600"/>
</p>

### 🆕 Crear un nuevo agente
1. Accede a `/agents/new`
2. Completa el formulario con el nombre, propósito y configuración
3. Guarda los cambios

### 💬 Interactuar con tu agente
1. Accede a `/chat?agentId={id}`
2. Escribe mensajes y recibe respuestas personalizadas

### ⚙️ Ajustar configuración
1. Accede a `/agents/edit/{id}`
2. Modifica los parámetros según necesites

## 🏗️ Arquitectura

AVANCE sigue una arquitectura en capas limpia y bien organizada:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ai/
│   │           └── avance/
│   │               ├── business/            # Lógica de negocio e integraciones
│   │               ├── config/              # Configuraciones
│   │               ├── data/
│   │               │   ├── entities/        # Entidades de dominio
│   │               │   ├── mappers/         # Convertidores DTO-Entity
│   │               │   └── repositories/    # Repositorios
│   │               ├── presentation/
│   │               │   ├── controllers/     # Controladores REST y MVC
│   │               │   └── dto/             # Data Transfer Objects
│   │               └── services/            # Servicios de aplicación
│   └── resources/
│       ├── static/                          # Recursos estáticos (CSS, JS)
│       ├── templates/                       # Plantillas Thymeleaf
│       └── application.properties           # Configuración
```

## 🧪 Tecnologías utilizadas

| Tecnología | Uso |
|------------|-----|
| ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) | Framework principal |
| ![Thymeleaf](https://img.shields.io/badge/-Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white) | Motor de plantillas |
| ![OpenAI](https://img.shields.io/badge/-OpenAI-412991?style=flat&logo=openai&logoColor=white) | Modelos de lenguaje |
| ![Java](https://img.shields.io/badge/-Java-007396?style=flat&logo=java&logoColor=white) | Lenguaje principal |
| ![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat&logo=apache-maven&logoColor=white) | Gestión de dependencias |
| ![Bootstrap](https://img.shields.io/badge/-Bootstrap-7952B3?style=flat&logo=bootstrap&logoColor=white) | Framework CSS |

## 🤝 Contribuir

¡Nos encantaría contar con tu ayuda para mejorar AVANCE! Sigue estos pasos:

1. 🍴 Haz un fork del repositorio
2. 🌿 Crea una rama para tu funcionalidad: `git checkout -b feature/nueva-funcionalidad`
3. 💻 Realiza tus cambios y haz commit: `git commit -m 'Añadir nueva funcionalidad'`
4. ⬆️ Envía tus cambios: `git push origin feature/nueva-funcionalidad`
5. 🔄 Abre un Pull Request

### Directrices de contribución

- Sigue el estilo de código existente
- Escribe pruebas para nuevas funcionalidades
- Mantén la documentación actualizada
- Revisa los problemas abiertos antes de empezar

## 📝 Licencia

Este proyecto está licenciado bajo la [Licencia MIT](LICENSE) - ver el archivo para detalles.

---

<p align="center">
  Desarrollado con ❤️ por el equipo de AVANCE
</p>

<p align="center">
  <a href="https://github.com/tuusuario/avance/issues">Reportar problema</a> •
  <a href="https://github.com/tuusuario/avance/wiki">Wiki</a> •
  <a href="mailto:contacto@ejemplo.com">Contacto</a>
</p>