package com.ai.avance.presentation.controller;

import com.ai.avance.presentation.dto.ChatDTO.MessageDTO;
import com.ai.avance.services.DefaultChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para interactuar con el chatbot de Gemini.
 */
@Controller
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final DefaultChatService defaultChatService;

    /**
     * Página principal del chatbot.
     */
    @GetMapping
    public String chatbotPage(Model model) {
        model.addAttribute("pageTitle", "Chatbot - Avance AI");
        return "chatbot";
    }

    /**
     * Endpoint para enviar un mensaje al chatbot y recibir una respuesta.
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String sessionId = (String) request.get("sessionId");
        
        // Si no hay sessionId, crear uno nuevo
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = "session_" + System.currentTimeMillis();
        }
        
        // Procesar el mensaje con el servicio de chat
        String response = defaultChatService.processMessage(message, sessionId);
        
        // Preparar la respuesta
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("response", response);
        responseMap.put("sessionId", sessionId);
        responseMap.put("timestamp", LocalDateTime.now().toString());
        responseMap.put("model", "gemini-2.0-flash");
        
        return ResponseEntity.ok(responseMap);
    }
} 