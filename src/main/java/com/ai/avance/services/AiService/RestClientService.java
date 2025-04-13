package com.ai.avance.services.AiService;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para manejar comunicaciones con APIs externas de IA.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestClientService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model.default}")
    private String defaultModel;

    /**
     * Obtiene una respuesta del servicio de IA.
     * 
     * @param messages Lista de mensajes de la conversación
     * @param agent Configuración del agente IA
     * @return Respuesta generada por el modelo de IA
     */
    public String getAiResponse(List<MessageDTO> messages, AgentDTO agent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            
            // Usar el modelo configurado en el agente o el modelo por defecto
            String modelToUse = agent.getModelConfig() != null && !agent.getModelConfig().isEmpty() 
                    ? agent.getModelConfig() 
                    : defaultModel;
            
            requestBody.put("model", modelToUse);
            
            // Convertir los mensajes al formato esperado por la API de OpenAI
            List<Map<String, String>> formattedMessages = new ArrayList<>();
            
            // Primero agregar un mensaje de sistema que define el comportamiento del agente
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            
            // Construir las instrucciones del sistema basadas en la configuración del agente
            StringBuilder systemPrompt = new StringBuilder();
            systemPrompt.append("Eres un asistente IA especializado según las siguientes indicaciones:\n");
            systemPrompt.append("- Propósito: ").append(agent.getPurpose()).append("\n");
            systemPrompt.append("- Tono de comunicación: ").append(agent.getTone()).append("\n");
            
            if (agent.getDomainContext() != null && !agent.getDomainContext().isEmpty()) {
                systemPrompt.append("- Contexto de dominio: ").append(agent.getDomainContext()).append("\n");
            }
            
            if (agent.getAllowedTopics() != null && !agent.getAllowedTopics().isEmpty()) {
                systemPrompt.append("- Temas permitidos: ").append(String.join(", ", agent.getAllowedTopics())).append("\n");
            }
            
            if (agent.getRestrictedTopics() != null && !agent.getRestrictedTopics().isEmpty()) {
                systemPrompt.append("- Evita hablar sobre: ").append(String.join(", ", agent.getRestrictedTopics())).append("\n");
            }
            
            systemMessage.put("content", systemPrompt.toString());
            formattedMessages.add(systemMessage);
            
            // Agregar el resto de mensajes de la conversación
            for (MessageDTO message : messages) {
                Map<String, String> formattedMessage = new HashMap<>();
                formattedMessage.put("role", message.getRole());
                formattedMessage.put("content", message.getContent());
                formattedMessages.add(formattedMessage);
            }
            
            requestBody.put("messages", formattedMessages);
            requestBody.put("temperature", 0.7); // Nivel de creatividad/aleatoriedad
            requestBody.put("max_tokens", 1000); // Longitud máxima de respuesta
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/chat/completions", 
                    requestEntity, 
                    Map.class
            );
            
            // Extraer la respuesta del modelo de IA
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    
                    if (message != null && message.containsKey("content")) {
                        return message.get("content");
                    }
                }
            }
            
            log.error("No se pudo obtener una respuesta válida de OpenAI: {}", response.getBody());
            return "Lo siento, no pude generar una respuesta en este momento.";
            
        } catch (Exception e) {
            log.error("Error al comunicarse con la API de OpenAI", e);
            return "Ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo más tarde.";
        }
    }
} 