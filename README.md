# Hola mundo

java.com.ai.avance:
business
  /AiLogic
    /AgentLogic            # Lógica de personalización de agentes GPT
    /AssistantLogic        # Lógica del asistente IA para la interfaz
    /PromptEngineeringLogic # Lógica para optimización de prompts
  /IntegrationLogic
    /OpenAiIntegration     # Integración específica con OpenAI
    /AnthropicIntegration  # Integración con otros proveedores (Anthropic/Claude)
    /AzureOpenAiIntegration # Integración con Azure OpenAI
  /TrainingLogic
    /PromptTrainingLogic   # Entrenamiento y mejora de prompts
    /UserFeedbackLogic     # Lógica para procesar feedback de usuarios
  /UserLogic
    /PreferencesLogic      # Lógica para gestión de preferencias de usuarios

config
  /SwaggerConfig
  /WebConfig
  /OpenAiConfig           # Configuración del cliente OpenAI
  /RedisConfig            # Configuración de caché
  /WebSocketConfig        # Configuración de WebSockets
  /AsyncConfig            # Configuración para operaciones asíncronas

data
  /entities
    /AiEntity
      /AgentEntity        # Configuraciones de agentes personalizados
      /PromptTemplateEntity # Plantillas de prompts
      /ModelConfigEntity  # Configuraciones de modelos IA
    /ConversationEntity
      /MessageEntity      # Mensajes individuales
      /ChatSessionEntity  # Sesiones de chat
    /UserEntity
      /UserPreferenceEntity # Preferencias de usuario
      /UserAgentEntity    # Relación usuario-agente

  /mappers
    /AiMapper
    /IntegrationMapper
    /UserMapper
    /ConversationMapper

  /repositories
    /AiRepository
      /AgentRepository
      /PromptTemplateRepository
      /ModelConfigRepository
    /ConversationRepository
      /MessageRepository
      /ChatSessionRepository
    /UserRepository
      /UserPreferenceRepository

exceptions
  /CustomExceptionHandler
  /ResourceNotFoundException
  /OpenAiException       # Excepciones específicas de integración
  /RateLimitedException  # Excepciones de límite de peticiones

presentation
  /controllers
    /AiController
      /AgentController    # Endpoints para gestionar agentes
      /AssistantController # Endpoints para el asistente de UI
      /PromptController   # Endpoints para prompts
    /IntegrationController
      /OpenAiController   # Endpoints específicos de OpenAI
    /UserController
      /PreferenceController # Endpoints para preferencias
    /WebSocketController  # Controlador para comunicación en tiempo real

  /dto
    /request
      /AiRequest
        /AgentRequest     # DTO para configuración de agentes
        /PromptRequest    # DTO para definición de prompts
      /IntegrationRequest
      /UserRequest
        /PreferenceRequest # DTO para preferencias
    /response
      /AiResponse
        /AgentResponse
      /ConversationResponse
      /UserResponse

  /views
    /static
      /js
        /chat.js         # Scripts para la interfaz de chat
        /agent-config.js # Scripts para configuración de agentes
      /css
        /styles.css
      /images
    /templates
      /chat.html         # Interfaz principal de chat
      /agent-config.html # Página de configuración
      /user-profile.html # Perfil de usuario
      /dashboard.html    # Panel de control

security
  /CustomUserDetailsService
  /JwtRequestFilter
  /JwtTokenUtil
  /SecurityConfig
  /ApiKeyAuthFilter      # Filtro para autenticación con API key

services
  /AiService
    /AgentService        # Servicio para gestión de agentes
    /AssistantService    # Servicio para el asistente de UI
    /PromptService       # Servicio para gestión de prompts
  /IntegrationService
    /OpenAiService       # Servicio específico para OpenAI
    /AnthropicService    # Servicio para Anthropic/Claude
  /TrainingService
    /FeedbackService     # Servicio para gestión de feedback
  /UserService
    /PreferenceService   # Servicio para preferencias de usuario
  /WebSocketService      # Servicio para gestión de comunicación en tiempo real
  /CacheService          # Servicio para gestión de caché

utils
  /PromptUtils          # Utilidades para manipulación de prompts
  /TokenCounterUtils    # Utilidades para conteo y optimización de tokens
  /RateLimitUtils       # Utilidades para control de límites de peticiones
  /ConversationUtils    # Utilidades para procesamiento de conversaciones

AvanceApplication

resources
  /db
    /migration          # Migraciones Flyway
  /prompts
    /templates          # Plantillas predefinidas de prompts
    /agents             # Configuraciones predefinidas de agentes
  /application.yml      # Configuración principal
  /application-dev.yml  # Configuración de desarrollo
  /application-prod.yml # Configuración de producción