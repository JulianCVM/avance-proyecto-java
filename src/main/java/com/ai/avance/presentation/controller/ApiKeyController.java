package com.ai.avance.presentation.controller;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.presentation.dto.UserRequest;
import com.ai.avance.presentation.dto.UserRequest.ApiKeyDto;
import com.ai.avance.services.ApiKeyService;
import com.ai.avance.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestionar las API keys de los usuarios.
 */
@Controller
@RequestMapping("/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    /**
     * Página de gestión de API keys.
     */
    @GetMapping
    public String apiKeysPage(Model model, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<ApiKeyDto> apiKeys = apiKeyService.getUserApiKeys(user);
        model.addAttribute("apiKeys", apiKeys);
        model.addAttribute("userRequest", new UserRequest());
        
        return "api-keys";
    }

    /**
     * Guarda una nueva API key.
     */
    @PostMapping("/save")
    public String saveApiKey(@ModelAttribute UserRequest userRequest, 
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Verificar la API key
        boolean isValid = apiKeyService.verifyApiKey(
            userRequest.getCurrentApiKey(), 
            userRequest.getApiKeyProvider()
        );
        
        if (!isValid) {
            redirectAttributes.addFlashAttribute("error", 
                "La API key no es válida o no se puede verificar. Por favor, verifica los datos e intenta nuevamente.");
            return "redirect:/api-keys";
        }
        
        // Guardar la API key
        apiKeyService.saveApiKey(
            user, 
            userRequest.getCurrentApiKey(), 
            userRequest.getApiKeyProvider(), 
            userRequest.getApiKeyName()
        );
        
        redirectAttributes.addFlashAttribute("success", "API key guardada correctamente");
        return "redirect:/api-keys";
    }

    /**
     * Elimina una API key.
     */
    @PostMapping("/delete/{id}")
    public String deleteApiKey(@PathVariable Long id, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        apiKeyService.deleteApiKey(id, user);
        redirectAttributes.addFlashAttribute("success", "API key eliminada correctamente");
        
        return "redirect:/api-keys";
    }
    
    /**
     * Verifica una API key (endpoint AJAX).
     */
    @PostMapping("/verify")
    @ResponseBody
    public String verifyApiKey(@RequestParam String apiKey, @RequestParam String provider) {
        boolean isValid = apiKeyService.verifyApiKey(apiKey, provider);
        return isValid ? "valid" : "invalid";
    }
    
    /**
     * Verifica si el usuario tiene configurada una API key para un proveedor específico.
     */
    @GetMapping("/status/{provider}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getApiKeyStatus(
            @PathVariable String provider,
            HttpSession session) {
        
        UserEntity user = (UserEntity) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        
        if (user == null) {
            response.put("configured", false);
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }
        
        boolean hasApiKey = apiKeyService.getDefaultApiKey(user, provider).isPresent();
        
        response.put("configured", hasApiKey);
        response.put("authenticated", true);
        
        return ResponseEntity.ok(response);
    }
} 