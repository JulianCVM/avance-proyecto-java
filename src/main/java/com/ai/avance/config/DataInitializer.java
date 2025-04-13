package com.ai.avance.config;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntity.ChatSessionEntity;
import com.ai.avance.data.repositories.AiRepository.AgentRepository;
import com.ai.avance.data.repositories.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente para inicializar datos de prueba en la base de datos.
 * Solo se ejecuta en el perfil "dev".
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final AgentRepository agentRepository;
    private final ChatRepository chatRepository;

    @Override
    public void run(String... args) {
        log.info("Inicializando datos de prueba...");
        
        // Crear agentes de ejemplo si no existen
        if (agentRepository.findByNameOrDescriptionContains("Asistente").isEmpty()) {
            createDemoAgents();
        }
        
        log.info("Datos de prueba inicializados correctamente");
    }
    
    private void createDemoAgents() {
        // Crear un agente de asistente general
        AgentEntity generalAssistant = AgentEntity.builder()
            .name("Asistente General")
            .description("Un asistente de propósito general para responder preguntas diversas")
            .purpose("Ayudar con consultas generales y proporcionar información útil sobre diversos temas")
            .tone("amigable")
            .domainContext("Conocimiento general y asistencia diaria")
            .allowedTopics(List.of("ciencia", "tecnología", "cultura", "historia", "deportes"))
            .restrictedTopics(List.of("política controversial", "contenido para adultos", "ilegalidades"))
            .modelConfig("gpt-3.5-turbo")
            .active(true)
            .userId(1L)
            .build();
            
        // Crear un agente especializado en programación
        AgentEntity programmingAssistant = AgentEntity.builder()
            .name("Asistente de Programación")
            .description("Especializado en ayudar con dudas de programación y desarrollo de software")
            .purpose("Proporcionar asistencia técnica en programación, debugging y diseño de software")
            .tone("técnico")
            .domainContext("Programación, desarrollo de software, algoritmos, bases de datos")
            .allowedTopics(List.of("Java", "Python", "JavaScript", "SQL", "algoritmos", "patrones de diseño"))
            .modelConfig("gpt-4")
            .active(true)
            .userId(1L)
            .build();
            
        // Guardarlos
        agentRepository.save(generalAssistant);
        agentRepository.save(programmingAssistant);
        
        log.info("Agentes de prueba creados");
        
        // Crear una sesión de ejemplo para el agente general
        ChatSessionEntity demoSession = ChatSessionEntity.builder()
            .title("Conversación con Asistente General")
            .userId(1L)
            .agentId(generalAssistant.getId())
            .createdAt(LocalDateTime.now())
            .lastActivity(LocalDateTime.now())
            .active(true)
            .messages(new ArrayList<>())
            .build();
            
        chatRepository.saveSession(demoSession);
        log.info("Sesión de chat de ejemplo creada");
    }
} 