# 🤖 Sistema de Agente Inteligente con LLM Personalizado

<div align="center">
  <img src="docs/images/architecture.png" alt="Arquitectura del Sistema" width="800"/>
  <br/>
  <em>Arquitectura del Sistema LLM-Agente</em>
</div>

## 📋 Índice
- [Descripción General](#descripción-general)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Componentes del Sistema](#componentes-del-sistema)
- [Implementación Técnica](#implementación-técnica)
- [Flujo de Datos](#flujo-de-datos)
- [Configuración](#configuración)
- [API Reference](#api-reference)
- [Guía de Uso](#guía-de-uso)
- [Proceso de Aprendizaje](#proceso-de-aprendizaje)
- [Características Avanzadas](#características-avanzadas)
- [Despliegue](#despliegue)
- [Contribución](#contribución)

## 🎯 Descripción General

Este proyecto implementa un sistema de agente inteligente que utiliza un modelo de lenguaje (LLM) personalizado para el procesamiento y aprendizaje de tareas. El sistema está diseñado para ser modular, extensible y fácilmente configurable, permitiendo la integración de diferentes modelos de lenguaje y comportamientos del agente.

### Características Principales
- 🤖 Agente inteligente con estados y comportamientos configurables
- 🧠 Integración con modelos de lenguaje personalizados
- 📚 Sistema de aprendizaje y entrenamiento
- 🔄 Proceso de decisión basado en LLM
- 📊 Monitoreo y métricas de rendimiento
- 🔒 Seguridad y autenticación integrada

## 🏗 Arquitectura del Sistema

### Diagrama de Arquitectura
```mermaid
graph TD
    A[Cliente] --> B[API REST]
    B --> C[Controladores]
    C --> D[Servicios]
    D --> E[Agente]
    D --> F[LLM]
    E --> G[Base de Datos]
    F --> G
    E --> H[Memoria]
    E --> I[Comportamiento]
```

### Capas del Sistema
1. **Capa de Presentación**
   - Controladores REST
   - Endpoints API
   - Manejo de solicitudes

2. **Capa de Servicio**
   - Lógica de negocio
   - Integración LLM-Agente
   - Procesamiento de datos

3. **Capa de Persistencia**
   - Repositorios
   - Entidades
   - Base de datos

## 🔧 Componentes del Sistema

### 1. Modelo LLM (`LLMModel`)
```java
@Entity
@Table(name = "llm_models")
public class LLMModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String version;
    
    @Column(nullable = false)
    private String modelType; // GPT, GEMINI, etc.
    
    @Column(nullable = false)
    private Integer maxTokens;
    
    @Column(nullable = false)
    private Double temperature;
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<TrainingData> trainingData = new ArrayList<>();
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<ModelParameter> parameters = new ArrayList<>();
}
```
**Explicación**: Esta entidad representa el modelo de lenguaje. Almacena la configuración básica del modelo, incluyendo su nombre, versión, tipo y parámetros de generación. Mantiene relaciones con los datos de entrenamiento y parámetros específicos.

#### Características
- **Configuración**: Parámetros del modelo ajustables
- **Versiones**: Control de versiones del modelo
- **Entrenamiento**: Gestión de datos de entrenamiento
- **Rendimiento**: Métricas y monitoreo

### 2. Agente Inteligente (`Agent`)
```java
@Component
public class Agent {
    private AgentState currentState;
    private AgentMemory memory;
    private Behavior behavior;
    private Sensor sensor;
    private DecisionMaker decisionMaker;
    // ... implementación
}
```

#### Estados del Agente
| Estado | Descripción |
|--------|-------------|
| IDLE | Estado de espera |
| PERCEIVING | Perceptando el entorno |
| THINKING | Procesando información |
| ACTING | Ejecutando acciones |
| LEARNING | Aprendiendo de nuevos datos |
| ERROR | Manejo de errores |

### 3. Sistema de Entrenamiento
```java
@Service
public class LLMAgentService {
    @Transactional
    public void trainAgentWithLLM(Long modelId) {
        // Implementación del entrenamiento
    }
}
```

## 💻 Implementación Técnica

### 1. Integración LLM-Agente
```java
@Service
public class LLMAgentService {
    private final LLMService llmService;
    private final Agent agent;

    @Transactional
    public void trainAgentWithLLM(Long modelId) {
        Optional<LLMModel> modelOpt = llmService.getModel(modelId);
        if (modelOpt.isPresent()) {
            LLMModel model = modelOpt.get();
            List<TrainingData> trainingData = model.getTrainingData();
            agent.updateState(AgentState.LEARNING);
            // Procesamiento de datos
        }
    }
}
```

### 2. Procesamiento de Datos
```java
private void processTrainingData(TrainingData data) {
    // 1. Procesar input
    String processedInput = preprocessInput(data.getInput());
    
    // 2. Aplicar aprendizaje
    LearningResult result = applyLearning(processedInput);
    
    // 3. Validar resultados
    validateResults(result, data.getExpectedOutput());
}
```

## 🔄 Flujo de Datos

### 1. Entrenamiento
```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as API
    participant S as Servicio
    participant Ag as Agente
    participant L as LLM
    
    C->>A: POST /train
    A->>S: trainAgentWithLLM()
    S->>Ag: updateState(LEARNING)
    loop Training Data
        S->>Ag: processTrainingData()
        Ag->>L: process()
        L-->>Ag: result
        Ag->>Ag: learn()
    end
    Ag-->>C: success
```

### 2. Ejecución
```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as API
    participant Ag as Agente
    participant L as LLM
    
    C->>A: POST /execute
    A->>Ag: perceive()
    Ag->>Ag: think()
    Ag->>L: process()
    L-->>Ag: result
    Ag->>Ag: act()
    Ag-->>C: response
```

## ⚙️ Configuración

### 1. Requisitos del Sistema
- Java 17+
- PostgreSQL 13+
- Maven 3.8+
- Spring Boot 3.0+
- Docker (opcional)

### 2. Configuración de Base de Datos
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/llm_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### 3. Variables de Entorno
```bash
OPENAI_API_KEY=your_key_here
GEMINI_API_KEY=your_key_here
JWT_SECRET=your_secret_here
```

## 📡 API Reference

### Endpoints Principales

#### Gestión de Modelos
```http
POST /api/llm/models
Content-Type: application/json

{
    "name": "AgentTrainer",
    "version": "1.0",
    "modelType": "GPT",
    "maxTokens": 2048,
    "temperature": 0.7
}
```

#### Entrenamiento
```http
POST /api/llm-agent/train/{modelId}
Content-Type: application/json

{
    "trainingData": [
        {
            "input": "Procesar solicitud",
            "expectedOutput": "Respuesta"
        }
    ]
}
```

## 🚀 Guía de Uso

### 1. Iniciar el Sistema
```bash
# Usando Docker
docker-compose up -d

# Usando Maven
mvn spring-boot:run
```

### 2. Crear un Modelo
```bash
curl -X POST http://localhost:8080/api/llm/models \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MiModelo",
    "version": "1.0",
    "modelType": "GPT"
  }'
```

### 3. Entrenar el Agente
```bash
curl -X POST http://localhost:8080/api/llm-agent/train/1
```

## 📚 Proceso de Aprendizaje

### 1. Fase de Entrenamiento
1. **Recolección de Datos**
   - Input del usuario
   - Respuestas esperadas
   - Contexto de la interacción

2. **Procesamiento**
   - Análisis de patrones
   - Extracción de características
   - Validación de resultados

3. **Aplicación de Aprendizaje**
   - Actualización de parámetros
   - Ajuste de comportamiento
   - Validación de resultados

### 2. Fase de Ejecución
1. **Percepción**
   - Captura de input
   - Análisis de contexto
   - Validación de datos

2. **Decisión**
   - Procesamiento con LLM
   - Selección de acción
   - Validación de decisión

3. **Acción**
   - Ejecución de tarea
   - Retroalimentación
   - Actualización de estado

## 🔥 Características Avanzadas

### 1. Sistema de Memoria
- Almacenamiento de interacciones
- Historial de decisiones
- Base de conocimiento

### 2. Adaptabilidad
- Ajuste automático de parámetros
- Aprendizaje continuo
- Optimización de rendimiento

### 3. Seguridad
- Autenticación JWT
- CORS configurado
- Validación de datos

## 🚢 Despliegue

### 1. Docker
```bash
# Construir imagen
docker build -t llm-agent .

# Ejecutar contenedor
docker run -d -p 8080:8080 llm-agent
```

### 2. Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: llm-agent
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: llm-agent
        image: llm-agent:latest
        ports:
        - containerPort: 8080
```

## 🤝 Contribución

1. Fork el repositorio
2. Crear rama de feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia
Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto
- Email: [email@example.com]
- Issues: [GitHub Issues](https://github.com/your-repo/issues)
- Documentación: [Wiki](https://github.com/your-repo/wiki)

---
<div align="center">
  <sub>Built with ❤️ by Your Team</sub>
</div>

## 📝 Explicación Detallada del Código LLM

### 1. Modelos y Entidades

#### LLMModel
```java
@Entity
@Table(name = "llm_models")
public class LLMModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String version;
    
    @Column(nullable = false)
    private String modelType; // GPT, GEMINI, etc.
    
    @Column(nullable = false)
    private Integer maxTokens;
    
    @Column(nullable = false)
    private Double temperature;
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<TrainingData> trainingData = new ArrayList<>();
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<ModelParameter> parameters = new ArrayList<>();
}
```
**Explicación**: Esta entidad representa el modelo de lenguaje. Almacena la configuración básica del modelo, incluyendo su nombre, versión, tipo y parámetros de generación. Mantiene relaciones con los datos de entrenamiento y parámetros específicos.

#### TrainingData
```java
@Entity
@Table(name = "training_data")
public class TrainingData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    @ManyToOne
    @JoinColumn(name = "model_id")
    private LLMModel model;
}
```
**Explicación**: Almacena los datos de entrenamiento para cada modelo. Cada entrada contiene el input, la salida esperada y metadatos adicionales. Está asociada con un modelo específico.

#### ModelParameter
```java
@Entity
@Table(name = "model_parameters")
public class ModelParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String value;
    
    @ManyToOne
    @JoinColumn(name = "model_id")
    private LLMModel model;
}
```
**Explicación**: Representa parámetros específicos del modelo que pueden ser ajustados para personalizar su comportamiento.

### 2. Servicios

#### LLMService
```java
@Service
@Transactional
public class LLMService {
    private final LLMModelRepository modelRepository;
    private final String openaiApiKey;
    private final String geminiApiKey;
    
    public LLMService(LLMModelRepository modelRepository,
                     @Value("${openai.api.key}") String openaiApiKey,
                     @Value("${gemini.api.key}") String geminiApiKey) {
        this.modelRepository = modelRepository;
        this.openaiApiKey = openaiApiKey;
        this.geminiApiKey = geminiApiKey;
    }
    
    public Optional<LLMModel> getModel(Long id) {
        return modelRepository.findById(id);
    }
    
    public LLMModel createModel(LLMModel model) {
        return modelRepository.save(model);
    }
    
    public void addTrainingData(Long modelId, TrainingData data) {
        modelRepository.findById(modelId).ifPresent(model -> {
            data.setModel(model);
            model.getTrainingData().add(data);
            modelRepository.save(model);
        });
    }
    
    public String processWithModel(Long modelId, String input) {
        return modelRepository.findById(modelId)
            .map(model -> processInput(model, input))
            .orElseThrow(() -> new RuntimeException("Model not found"));
    }
    
    private String processInput(LLMModel model, String input) {
        switch (model.getModelType().toUpperCase()) {
            case "GPT":
                return processWithGPT(model, input);
            case "GEMINI":
                return processWithGemini(model, input);
            default:
                throw new RuntimeException("Unsupported model type");
        }
    }
}
```
**Explicación**: Este servicio maneja toda la lógica relacionada con los modelos LLM. Incluye:
- Gestión de modelos (creación, búsqueda)
- Adición de datos de entrenamiento
- Procesamiento de entradas usando diferentes modelos
- Integración con APIs externas (OpenAI, Gemini)

#### LLMAgentService
```java
@Service
@Transactional
public class LLMAgentService {
    private final LLMService llmService;
    private final Agent agent;
    
    public LLMAgentService(LLMService llmService, Agent agent) {
        this.llmService = llmService;
        this.agent = agent;
    }
    
    public void trainAgentWithLLM(Long modelId) {
        Optional<LLMModel> modelOpt = llmService.getModel(modelId);
        if (modelOpt.isPresent()) {
            LLMModel model = modelOpt.get();
            agent.updateState(AgentState.LEARNING);
            
            try {
                for (TrainingData data : model.getTrainingData()) {
                    processTrainingData(data);
                }
            } finally {
                agent.updateState(AgentState.IDLE);
            }
        }
    }
    
    private void processTrainingData(TrainingData data) {
        String processedInput = preprocessInput(data.getInput());
        LearningResult result = applyLearning(processedInput);
        validateResults(result, data.getExpectedOutput());
    }
    
    public void applyLLMParameters(Long modelId) {
        llmService.getModel(modelId).ifPresent(this::applyModelParameters);
    }
    
    private void applyModelParameters(LLMModel model) {
        agent.getBehavior().updateParameters(model.getParameters());
        agent.getMemory().updateModelContext(model);
    }
}
```
**Explicación**: Este servicio actúa como puente entre el LLM y el agente. Sus funciones principales son:
- Entrenamiento del agente usando datos del LLM
- Aplicación de parámetros del modelo al agente
- Gestión del estado del agente durante el aprendizaje
- Procesamiento y validación de datos de entrenamiento

### 3. Controladores

#### LLMController
```java
@RestController
@RequestMapping("/api/llm")
public class LLMController {
    private final LLMService llmService;
    
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }
    
    @PostMapping("/models")
    public ResponseEntity<LLMModel> createModel(@RequestBody LLMModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(llmService.createModel(model));
    }
    
    @PostMapping("/models/{id}/training-data")
    public ResponseEntity<Void> addTrainingData(
            @PathVariable Long id,
            @RequestBody TrainingData data) {
        llmService.addTrainingData(id, data);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/models/{id}/process")
    public ResponseEntity<String> processInput(
            @PathVariable Long id,
            @RequestBody String input) {
        return ResponseEntity.ok(llmService.processWithModel(id, input));
    }
}
```
**Explicación**: Expone endpoints REST para:
- Creación de modelos
- Adición de datos de entrenamiento
- Procesamiento de entradas con modelos específicos

#### LLMAgentController
```java
@RestController
@RequestMapping("/api/llm-agent")
public class LLMAgentController {
    private final LLMAgentService llmAgentService;
    
    public LLMAgentController(LLMAgentService llmAgentService) {
        this.llmAgentService = llmAgentService;
    }
    
    @PostMapping("/train/{modelId}")
    public ResponseEntity<String> trainAgentWithLLM(@PathVariable Long modelId) {
        try {
            llmAgentService.trainAgentWithLLM(modelId);
            return ResponseEntity.ok("Agent trained successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error training agent: " + e.getMessage());
        }
    }
    
    @PostMapping("/apply-parameters/{modelId}")
    public ResponseEntity<String> applyLLMParameters(@PathVariable Long modelId) {
        try {
            llmAgentService.applyLLMParameters(modelId);
            return ResponseEntity.ok("Parameters applied successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error applying parameters: " + e.getMessage());
        }
    }
}
```
**Explicación**: Proporciona endpoints para:
- Entrenamiento del agente con un modelo específico
- Aplicación de parámetros del modelo al agente
- Manejo de errores y respuestas HTTP apropiadas

### 4. Repositorios

#### LLMModelRepository
```java
@Repository
public interface LLMModelRepository extends JpaRepository<LLMModel, Long> {
    List<LLMModel> findByModelType(String modelType);
    Optional<LLMModel> findByNameAndVersion(String name, String version);
}
```
**Explicación**: Interfaz de repositorio que proporciona:
- Operaciones CRUD básicas para modelos
- Búsqueda por tipo de modelo
- Búsqueda por nombre y versión

### 5. Configuración

#### application.properties
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/llm_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# API Keys
openai.api.key=${OPENAI_API_KEY}
gemini.api.key=${GEMINI_API_KEY}

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Logging
logging.level.com.ai.avance=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```
**Explicación**: Configuración del sistema que incluye:
- Conexión a base de datos
- Claves de API para servicios externos
- Configuración de seguridad JWT
- Configuración de logging
