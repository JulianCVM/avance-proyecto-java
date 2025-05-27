package com.ai.avance.presentation.controller;

import com.ai.avance.data.entities.UserEntities.UserEntity;
import com.ai.avance.presentation.dto.ChatDTO.SessionDTO;
import com.ai.avance.services.AiServiceManager;
import com.ai.avance.services.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la página principal y de inicio.
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final AiServiceManager aiServiceManager;
    private final ApiKeyService apiKeyService;

    /**
     * Página de inicio principal.
     */
    @GetMapping({"/", "/home", "/index"})
    public String index(Model model, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        // Si hay un usuario autenticado, cargamos sus datos
        if (user != null) {
            Long userId = user.getId();
            
            // Obtener agentes del usuario
            model.addAttribute("userAgents", aiServiceManager.getUserAgentDtos(userId));
            
            // Obtener conversaciones recientes
            List<SessionDTO> recentSessions = Collections.emptyList();
            try {
                recentSessions = aiServiceManager.getUserRecentSessions(userId, 5);
            } catch (Exception e) {
                // Manejar la excepción silenciosamente
            }
            model.addAttribute("recentSessions", recentSessions);
            
            // Estadísticas del usuario (simulación básica)
            model.addAttribute("userStats", Map.of(
                "agentCount", aiServiceManager.getUserAgentDtos(userId).size(),
                "sessionCount", recentSessions.size(),
                "messageCount", 0
            ));
            
            // Contar API keys del usuario
            model.addAttribute("userApiKeyCount", apiKeyService.getUserApiKeys(user).size());
        }
        
        return "index";
    }
} 