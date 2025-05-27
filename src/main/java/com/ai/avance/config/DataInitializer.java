package com.ai.avance.config;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.data.repositories.AgentRepository;
import com.ai.avance.data.repositories.UserRepository;
import com.ai.avance.services.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Inicializando datos de prueba...");
        
        // Crear usuario de prueba si no existe
        UserEntity testUser = createTestUserIfNotExists();
        
        // Crear agentes de ejemplo si no existen
        if (agentRepository.findByName("Asistente General").isEmpty()) {
            createDemoAgents(testUser);
        }
        
        log.info("Datos de prueba inicializados correctamente");
    }
    
    private UserEntity createTestUserIfNotExists() {
        Optional<UserEntity> existingUser = userRepository.findByUsername("admin");
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        UserEntity adminUser = new UserEntity();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@avance.com");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Usuario");
        adminUser.setActive(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setRoles(new HashSet<>(Arrays.asList("ROLE_ADMIN")));
        
        return userRepository.save(adminUser);
    }
    
    private void createDemoAgents(UserEntity user) {
        // Crear un agente de asistente general
        AgentEntity generalAssistant = new AgentEntity();
        generalAssistant.setName("Asistente General");
        generalAssistant.setDescription("Un asistente de propósito general para responder preguntas diversas");
        generalAssistant.setPurpose("Ayudar con consultas generales y proporcionar información útil sobre diversos temas");
        generalAssistant.setTone("amigable");
        generalAssistant.setDomainContext("Conocimiento general y asistencia diaria");
        generalAssistant.setAllowedTopics(List.of("ciencia", "tecnología", "cultura", "historia", "deportes"));
        generalAssistant.setRestrictedTopics(List.of("política controversial", "contenido para adultos", "ilegalidades"));
        generalAssistant.setModelConfig("gpt-3.5-turbo");
        generalAssistant.setApiToken("sk-demo-token-general");
        generalAssistant.setModelProvider("openai");
        generalAssistant.setActive(true);
        generalAssistant.setUser(user);
        
        // Crear un agente especializado en programación
        AgentEntity programmingAssistant = new AgentEntity();
        programmingAssistant.setName("Asistente de Programación");
        programmingAssistant.setDescription("Especializado en ayudar con dudas de programación y desarrollo de software");
        programmingAssistant.setPurpose("Proporcionar asistencia técnica en programación, debugging y diseño de software");
        programmingAssistant.setTone("técnico");
        programmingAssistant.setDomainContext("Programación, desarrollo de software, algoritmos, bases de datos");
        programmingAssistant.setAllowedTopics(List.of("Java", "Python", "JavaScript", "SQL", "algoritmos", "patrones de diseño"));
        programmingAssistant.setRestrictedTopics(new ArrayList<>());
        programmingAssistant.setModelConfig("gpt-4");
        programmingAssistant.setApiToken("sk-demo-token-programming");
        programmingAssistant.setModelProvider("openai");
        programmingAssistant.setActive(true);
        programmingAssistant.setUser(user);
        
        // Guardarlos
        AgentEntity savedGeneralAgent = agentRepository.save(generalAssistant);
        AgentEntity savedProgrammingAgent = agentRepository.save(programmingAssistant);
        
        log.info("Agentes de prueba creados: {} y {}", savedGeneralAgent.getId(), savedProgrammingAgent.getId());
        
        // Crear una sesión de ejemplo para el agente general
        ChatSessionEntity demoSession = new ChatSessionEntity();
        demoSession.setTitle("Conversación con Asistente General");
        demoSession.setUser(user);
        demoSession.setAgent(savedGeneralAgent);
        demoSession.setCreatedAt(LocalDateTime.now());
        demoSession.setLastActivity(LocalDateTime.now());
        demoSession.setActive(true);
        demoSession.setMessages(new ArrayList<>());
        demoSession.setContextInfo("Sesión de demostración");
        
        ChatSessionEntity savedSession = chatService.saveSession(demoSession);
        log.info("Sesión de chat de ejemplo creada con ID: {}", savedSession.getId());
    }
} 