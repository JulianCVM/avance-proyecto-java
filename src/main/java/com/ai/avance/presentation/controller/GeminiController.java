package com.ai.avance.presentation.controller;

import com.ai.avance.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    /**
     * Endpoint para generar saludos aleatorios
     * @return Saludo generado en formato JSON
     */
    @GetMapping("/greeting")
    public ResponseEntity<Map<String, String>> generateGreeting() {
        String greeting = geminiService.generateGreeting();
        
        Map<String, String> response = new HashMap<>();
        response.put("greeting", greeting);
        
        return ResponseEntity.ok(response);
    }
} 