package com.ai.avance.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model.default:gemini-2.0-flash}")
    private String defaultModel;

    private RestTemplate restTemplate;
    private String apiUrl;

    @PostConstruct
    public void init() {
        try {
            // Inicializamos un RestTemplate para hacer llamadas HTTP directas a la API de Gemini
            this.restTemplate = new RestTemplate();
            this.apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + defaultModel + ":generateContent?key=" + apiKey;
            
            System.out.println("Servicio Gemini inicializado correctamente usando el modelo: " + defaultModel);
        } catch (Exception e) {
            System.err.println("Error al inicializar el servicio de Gemini: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera un saludo aleatorio usando la API de Gemini
     * @return Saludo generado
     */
    public String generateGreeting() {
        try {
            String prompt = "Genera un saludo amigable y motivador para un usuario que visita una plataforma de IA. " +
                    "El saludo debe ser breve (máximo 150 caracteres), positivo y en español. " +
                    "No incluyas comillas ni prefijos como 'Saludo:'. Solo devuelve el texto del saludo.";

            // Construir el cuerpo de la solicitud
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(part);
            content.put("parts", parts);
            content.put("role", "user");
            
            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(content);
            
            requestBody.put("contents", contents);

            // Configurar los encabezados
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Realizar la solicitud a la API de Gemini
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            
            // Procesar la respuesta
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> candidate = candidates.get(0);
                        Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
                        List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");
                        if (candidateParts != null && !candidateParts.isEmpty()) {
                            return (String) candidateParts.get(0).get("text");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar la respuesta: " + e.getMessage());
                }
            }
            
            return "¡Bienvenido a Avance AI! Estamos encantados de tenerte aquí.";
            
        } catch (Exception e) {
            System.err.println("Error al generar saludo con Gemini: " + e.getMessage());
            e.printStackTrace();
            return "¡Bienvenido a Avance AI! Estamos encantados de tenerte aquí.";
        }
    }

    /**
     * Genera respuestas a partir de un prompt
     * @param prompt Prompt a procesar
     * @return Respuesta generada
     */
    public String generateResponse(String prompt) {
        try {
            // Construir el cuerpo de la solicitud
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(part);
            content.put("parts", parts);
            content.put("role", "user");
            
            List<Map<String, Object>> contents = new ArrayList<>();
            contents.add(content);
            
            requestBody.put("contents", contents);

            // Configurar los encabezados
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Realizar la solicitud a la API de Gemini
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            
            // Procesar la respuesta
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> candidate = candidates.get(0);
                        Map<String, Object> candidateContent = (Map<String, Object>) candidate.get("content");
                        List<Map<String, Object>> candidateParts = (List<Map<String, Object>>) candidateContent.get("parts");
                        if (candidateParts != null && !candidateParts.isEmpty()) {
                            return (String) candidateParts.get(0).get("text");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar la respuesta: " + e.getMessage());
                }
            }
            
            return "Lo siento, no pude generar una respuesta en este momento.";
            
        } catch (Exception e) {
            System.err.println("Error al generar respuesta con Gemini: " + e.getMessage());
            e.printStackTrace();
            return "Lo siento, no pude generar una respuesta en este momento.";
        }
    }
} 