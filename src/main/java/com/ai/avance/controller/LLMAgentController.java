package com.ai.avance.controller;

import com.ai.avance.service.LLMAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/llm-agent")
public class LLMAgentController {
    private final LLMAgentService llmAgentService;

    @Autowired
    public LLMAgentController(LLMAgentService llmAgentService) {
        this.llmAgentService = llmAgentService;
    }

    @PostMapping("/train/{modelId}")
    public ResponseEntity<String> trainAgentWithLLM(@PathVariable Long modelId) {
        try {
            llmAgentService.trainAgentWithLLM(modelId);
            return ResponseEntity.ok("Entrenamiento completado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en el entrenamiento: " + e.getMessage());
        }
    }

    @PostMapping("/apply-parameters/{modelId}")
    public ResponseEntity<String> applyLLMParameters(@PathVariable Long modelId) {
        try {
            llmAgentService.applyLLMParameters(modelId);
            return ResponseEntity.ok("Parámetros aplicados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al aplicar parámetros: " + e.getMessage());
        }
    }
} 