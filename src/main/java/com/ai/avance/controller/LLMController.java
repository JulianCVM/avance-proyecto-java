package com.ai.avance.controller;

import com.ai.avance.model.LLMModel;
import com.ai.avance.model.ModelParameter;
import com.ai.avance.model.TrainingData;
import com.ai.avance.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/llm")
public class LLMController {
    private final LLMService llmService;

    @Autowired
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/models")
    public ResponseEntity<LLMModel> createModel(@RequestBody LLMModel model) {
        return ResponseEntity.ok(llmService.createModel(model));
    }

    @GetMapping("/models")
    public ResponseEntity<List<LLMModel>> getAllModels() {
        return ResponseEntity.ok(llmService.getAllModels());
    }

    @GetMapping("/models/{id}")
    public ResponseEntity<LLMModel> getModel(@PathVariable Long id) {
        return llmService.getModel(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/models/{id}")
    public ResponseEntity<LLMModel> updateModel(@PathVariable Long id, @RequestBody LLMModel model) {
        try {
            return ResponseEntity.ok(llmService.updateModel(id, model));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/models/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        llmService.deleteModel(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/models/{id}/training-data")
    public ResponseEntity<Void> addTrainingData(@PathVariable Long id, @RequestBody TrainingData trainingData) {
        llmService.addTrainingData(id, trainingData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/models/{id}/parameters")
    public ResponseEntity<Void> addParameter(@PathVariable Long id, @RequestBody ModelParameter parameter) {
        llmService.addParameter(id, parameter);
        return ResponseEntity.ok().build();
    }
} 