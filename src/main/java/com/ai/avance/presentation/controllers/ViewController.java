package com.ai.avance.presentation.controllers;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las vistas de la aplicación.
 */
@Controller
@RequiredArgsConstructor
public class ViewController {

    private final AiServiceManager aiServiceManager;
    // Aquí se inyectarían otros servicios necesarios

    /**
     * Página de chat con un agente específico.
     */
    @GetMapping("/chat")
    public String chatPage(@RequestParam(required = false) Long agentId, 
                           @RequestParam(required = false) Long chatSessionId,
                           Model model, 
                           HttpSession session) {
        
        // En un sistema real, obtendríamos el userId de la sesión o autenticación
        Long userId = 1L; // Usuario demo por defecto
        
        // Si no se especifica un agente, usar el predeterminado o el primero disponible
        if (agentId == null) {
            List<AgentDTO> userAgents = aiServiceManager.getUserAgentDtos(userId);
            
            if (!userAgents.isEmpty()) {
                agentId = userAgents.get(0).getId();
                session.setAttribute("agentName", userAgents.get(0).getName());
            } else {
                // Si el usuario no tiene agentes, redirigir a la creación de uno
                return "redirect:/agents/new";
            }
        } else {
            // Obtener el nombre del agente para mostrarlo en la interfaz
            Optional<AgentDTO> agentOpt = aiServiceManager.getAgentDtoById(agentId);
            if (agentOpt.isPresent()) {
                session.setAttribute("agentName", agentOpt.get().getName());
            } else {
                // Si el agente no existe, redirigir a la lista de agentes
                return "redirect:/agents";
            }
        }
        
        // En un sistema real, buscaríamos la sesión de chat en la base de datos
        // o crearíamos una nueva si no existe
        if (chatSessionId == null) {
            chatSessionId = System.currentTimeMillis(); // Simulación simplificada
            // Aquí llamaríamos a un servicio para crear una nueva sesión
        }
        
        // Agregar datos necesarios para la vista
        model.addAttribute("agentId", agentId);
        model.addAttribute("chatSessionId", chatSessionId);
        model.addAttribute("userId", userId);
        model.addAttribute("userAgents", aiServiceManager.getUserAgentDtos(userId));
        
        return "chat";
    }

    /**
     * Página de dashboard/panel de control.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // En un sistema real, cargamos estadísticas y métricas aquí
        model.addAttribute("pageTitle", "Dashboard - Avance AI");
        model.addAttribute("currentTime", LocalDateTime.now());
        return "dashboard";
    }
    
    /**
     * Página de configuraciones de usuario.
     */
    @GetMapping("/settings")
    public String userSettings(Model model) {
        // En un sistema real, obtendríamos la información del usuario actual
        Long userId = 1L; // Usuario demo por defecto
        
        model.addAttribute("pageTitle", "Configuración - Avance AI");
        model.addAttribute("userId", userId);
        // Aquí cargaríamos la información del usuario y otras configuraciones
        return "settings";
    }
} 