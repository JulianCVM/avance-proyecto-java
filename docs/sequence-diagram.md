# Diagrama de Secuencia del Sistema LLM-Agente

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant Agent
    participant LLM
    participant DB

    %% Creación de Modelo
    Client->>Controller: POST /api/llm/models
    Controller->>Service: createModel()
    Service->>DB: save(model)
    DB-->>Service: model saved
    Service-->>Controller: model
    Controller-->>Client: 201 Created

    %% Entrenamiento
    Client->>Controller: POST /api/llm-agent/train/{id}
    Controller->>Service: trainAgentWithLLM()
    Service->>DB: getModel(id)
    DB-->>Service: model
    Service->>Agent: updateState(LEARNING)
    loop Training Data
        Service->>Agent: processTrainingData()
        Agent->>LLM: process()
        LLM-->>Agent: result
        Agent->>Agent: learn()
    end
    Service->>Agent: updateState(IDLE)
    Service-->>Controller: success
    Controller-->>Client: 200 OK

    %% Aplicación de Parámetros
    Client->>Controller: POST /api/llm-agent/apply-parameters/{id}
    Controller->>Service: applyLLMParameters()
    Service->>DB: getModel(id)
    DB-->>Service: model
    Service->>Agent: applyModelParameters()
    Agent->>Agent: updateBehavior()
    Service-->>Controller: success
    Controller-->>Client: 200 OK

    %% Ejecución
    Client->>Controller: POST /api/agent/execute
    Controller->>Agent: perceive()
    Agent->>Agent: think()
    Agent->>LLM: process()
    LLM-->>Agent: result
    Agent->>Agent: act()
    Agent-->>Controller: result
    Controller-->>Client: 200 OK
```

## Explicación del Flujo

1. **Creación de Modelo**
   - El cliente envía una solicitud para crear un nuevo modelo
   - El controlador delega al servicio
   - El servicio persiste el modelo en la base de datos

2. **Entrenamiento**
   - El cliente inicia el entrenamiento
   - El agente entra en estado de aprendizaje
   - Se procesan los datos de entrenamiento
   - El LLM procesa y devuelve resultados
   - El agente aprende de los resultados

3. **Aplicación de Parámetros**
   - El cliente solicita aplicar parámetros
   - Se obtiene el modelo de la base de datos
   - Se actualizan los parámetros del agente
   - Se actualiza el comportamiento

4. **Ejecución**
   - El cliente solicita una ejecución
   - El agente percibe el entorno
   - Procesa la información con el LLM
   - Ejecuta la acción correspondiente
   - Devuelve el resultado 