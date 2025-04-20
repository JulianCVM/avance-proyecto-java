# 🤖 AVANCE - Sistema de Creación de IA Personalizada

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![OpenAI](https://img.shields.io/badge/OpenAI-API-lightgrey)](https://openai.com/api/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green.svg)](https://www.thymeleaf.org/)
[![H2](https://img.shields.io/badge/H2-Database-blue.svg)](https://www.h2database.com/)
[![Gemini](https://img.shields.io/badge/Gemini-API-purple)](https://ai.google.dev/gemini)

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
- [🗄️ Estructura de la Base de Datos](#️-estructura-de-la-base-de-datos)
- [🧪 Tecnologías utilizadas](#-tecnologías-utilizadas)
- [🤝 Contribuir](#-contribuir)
- [📝 Licencia](#-licencia)
- [🔄 Estado del Proyecto](#-estado-del-proyecto)

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

### 🤖 Chatbot Informativo por Defecto
- Sistema de ayuda integrado usando Gemini
- Proporciona información y asistencia sobre la plataforma

## 🔧 Requisitos técnicos

| Requisito | Versión | Descripción |
|-----------|---------|-------------|
| ☕ Java | 21+ | Entorno de ejecución |
| 📦 Maven | 3.8+ | Gestor de dependencias |
| 🔑 API Key | OpenAI/Gemini | Claves para integración con modelos de IA |
| 🗄️ H2 | 2.1+ | Base de datos en memoria (MySQL 8.0+ en futuras versiones) |
| 💾 Memoria | 4 GB+ | RAM recomendada para desarrollo |

## ⚙️ Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/tuusuario/avance.git
cd avance
```

### 2. Configuración de la base de datos
> **⚠️ NOTA IMPORTANTE:** Actualmente, el proyecto utiliza la base de datos H2 en memoria para facilitar el desarrollo y las pruebas. En próximas versiones, se incluirá soporte completo para MySQL. 

#### Configuración actual con H2 (por defecto):
```properties
# Esta configuración ya viene por defecto en application.properties
spring.datasource.url=jdbc:h2:mem:avancedb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### Futura configuración con MySQL (próximas versiones):
```properties
# En futuras versiones podrás usar esta configuración
spring.datasource.url=jdbc:mysql://localhost:3306/avancedb?useSSL=false&serverTimezone=UTC
spring.datasource.username=usuario
spring.datasource.password=contraseña
```

### 3. Configurar las API keys

#### Para OpenAI
```properties
# En application.properties o como variable de entorno
openai.api.key=tu-api-key-aquí
openai.api.url=https://api.openai.com/v1
openai.model.default=gpt-3.5-turbo
```

#### Para Gemini (chatbot por defecto)
```properties
# En application.properties o como variable de entorno
gemini.api.key=tu-api-key-gemini-aquí
gemini.model.default=gemini-pro
```

### 4. Compilar el proyecto
```bash
mvn clean install
```

### 5. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 6. Acceder a la aplicación
```
http://localhost:8080
```

#### Acceso a la consola H2 (solo en desarrollo)
```
http://localhost:8080/h2-console
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
│   │               ├── security/            # Autenticación y autorización
│   │               └── services/            # Servicios de aplicación
│   └── resources/
│       ├── static/                          # Recursos estáticos (CSS, JS)
│       ├── templates/                       # Plantillas Thymeleaf
│       └── application.properties           # Configuración
```

## 🗄️ Estructura de la Base de Datos

AVANCE utilizará MySQL como sistema de gestión de base de datos en futuras versiones. Actualmente, utiliza H2 en memoria para desarrollo. A continuación se muestra la estructura relacional:

### Diagrama de Entidad-Relación

```
                                                     ┌─────────────────┐
                                                     │     users       │
                                                     ├─────────────────┤
                                                     │ id              │
                                                     │ username        │
┌──────────────────┐                                 │ email           │
│  password_reset  │                                 │ password        │
│     tokens       │◄──────────────────────────────►│ first_name      │
├──────────────────┤                                 │ last_name       │
│ id               │                                 │ active          │
│ token            │                                 │ created_at      │
│ user_id          │                                 │ last_login      │
│ expiry_date      │                                 └────────┬────────┘
│ is_used          │                                          │
└──────────────────┘                                          │
                                                              │
┌──────────────────┐          ┌────────────────┐             │
│    api_tokens    │          │   user_roles   │◄────────────┤
├──────────────────┤          ├────────────────┤             │
│ id               │          │ user_id        │             │
│ user_id          │◄─────────┤ role           │             │
│ token_value      │          └────────────────┘             │
│ provider         │                                          │
│ is_active        │          ┌────────────────┐             │
│ name             │          │  token_usage   │             │
│ created_at       │          ├────────────────┤             │
│ last_used        │          │ id             │             │
└──────────────────┘          │ user_id        │◄────────────┤
                              │ agent_id        │             │
                              │ input_tokens    │             │
                              │ output_tokens   │             │
┌────────────────────┐        │ model           │             │
│   default_agent    │        │ provider        │             │
├────────────────────┤        │ timestamp       │             │
│ id                 │        │ session_id      │             │
│ name               │        └────────────────┘             │
│ description        │                                        │
│ system_prompt      │                                        │
│ api_token          │        ┌─────────────────┐             │
│ model_provider     │        │     agents      │             │
│ active             │        ├─────────────────┤             │
│ created_at         │        │ id              │             │
│ updated_at         │        │ name            │             │
└────────────────────┘        │ description     │             │
                              │ purpose         │             │
                              │ tone            │             │
                              │ active          │             │
                              │ model_config    │             │
                              │ domain_context  │             │
                              │ api_token       │             │
                              │ model_provider  │             │
                              │ created_at      │             │
                              │ updated_at      │             │
                              │ user_id         │◄────────────┘
                              └───────┬─────────┘
                                      │
      ┌───────────────────┐           │            ┌────────────────────┐
      │agent_allowed_topics│◄──────────┤            │agent_restricted_topics│
      ├───────────────────┤           │            ├────────────────────┤
      │agent_id           │           │            │agent_id            │
      │topic              │           │            │topic               │
      └───────────────────┘           │            └────────────────────┘
                                      │
                                      │
                              ┌───────▼─────────┐
                              │  chat_sessions  │
                              ├─────────────────┤       ┌────────────────────┐
                              │ id              │       │  default_chat_sessions  │
                              │ user_id         │       ├────────────────────┤
                              │ agent_id        │       │ id                 │
                              │ title           │       │ user_id            │
                              │ created_at      │       │ session_key        │
                              │ last_activity   │       │ created_at         │
                              │ active          │       │ last_activity      │
                              │ context_info    │       └────────┬───────────┘
                              │ total_tokens_used│                │
                              └───────┬─────────┘                │
                                      │                          │
                                      │                          │
                              ┌───────▼─────────┐        ┌───────▼───────┐
                              │    messages     │        │default_messages│
                              ├─────────────────┤        ├───────────────┤
                              │ id              │        │ id            │
                              │ chat_session_id │        │ chat_session_id│
                              │ content         │        │ content       │
                              │ role            │        │ role          │
                              │ timestamp       │        │ timestamp     │
                              │ token_count     │        └───────────────┘
                              │ flagged         │
                              │ flag_reason     │
                              │ model_used      │
                              └─────────────────┘
```

### Principales tablas y relaciones:

1. **Users**: Almacena la información de los usuarios del sistema.
2. **Agents**: Contiene los agentes de IA configurados por los usuarios.
3. **API Tokens**: Guarda los tokens de API (OpenAI, Gemini) para acceder a los modelos.
4. **Chat Sessions**: Registra las conversaciones entre usuarios y agentes.
5. **Messages**: Almacena los mensajes individuales de cada conversación.
6. **Default Agent**: Configura el chatbot informativo del sistema.

### Inicialización de la base de datos H2

Actualmente, la base de datos H2 se inicializa automáticamente con el siguiente script:

```bash
# Ejecutar la aplicación con la base de datos H2 en memoria
mvn spring-boot:run
```

En el futuro, para MySQL:

```bash
# Genera un script SQL de la estructura actual
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.jpa.hibernate.ddl-auto=create-drop" 
```

## 🧪 Tecnologías utilizadas

| Tecnología | Uso |
|------------|-----|
| ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) | Framework principal |
| ![Thymeleaf](https://img.shields.io/badge/-Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white) | Motor de plantillas |
| ![OpenAI](https://img.shields.io/badge/-OpenAI-412991?style=flat&logo=openai&logoColor=white) | Modelos de lenguaje |
| ![Gemini](https://img.shields.io/badge/-Gemini-4285F4?style=flat&logo=google&logoColor=white) | Chatbot por defecto |
| ![Java](https://img.shields.io/badge/-Java-007396?style=flat&logo=java&logoColor=white) | Lenguaje principal |
| ![H2](https://img.shields.io/badge/-H2-0000AA?style=flat&logo=h2-database&logoColor=white) | Base de datos desarrollo |
| ![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat&logo=apache-maven&logoColor=white) | Gestión de dependencias |
| ![Bootstrap](https://img.shields.io/badge/-Bootstrap-7952B3?style=flat&logo=bootstrap&logoColor=white) | Framework CSS |
| ![Spring Security](https://img.shields.io/badge/-Spring%20Security-6DB33F?style=flat&logo=spring-security&logoColor=white) | Autenticación y autorización |

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

## 🔄 Estado del Proyecto

### Versión actual: 0.5.0-ALPHA

El proyecto se encuentra actualmente en fase de desarrollo activo con las siguientes limitaciones y planes:

- ✅ Base de datos H2 en memoria para facilitar desarrollo
- ⏳ Soporte para MySQL planificado para la versión 1.0.0
- ⏳ Mejoras en la gestión de usuarios y permisos
- ⏳ Optimización de la interfaz de usuario
- ⏳ Implementación de API completa para integraciones

Para la próxima versión (0.6.0-BETA), se implementará:
- Soporte para persistencia en MySQL
- Mejoras en la UI/UX
- Mejor gestión de API keys

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