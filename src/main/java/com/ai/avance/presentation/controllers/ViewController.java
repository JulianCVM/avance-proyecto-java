package com.ai.avance.presentation.controllers;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
     * Página de inicio que redirige a la lista de agentes.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/agents";
    }

    /* Comentado para resolver conflicto con AgentConfigController
    @GetMapping("/agents")
    public String listAgents(Model model, @RequestParam(required = false) Long userId) {
        // En un sistema real, obtendríamos el userId de la sesión o autenticación
        Long userIdToUse = userId != null ? userId : 1L; // Usuario demo por defecto
        
        List<AgentDTO> agents = aiServiceManager.getUserAgents(userIdToUse);
        model.addAttribute("agents", agents);
        return "agent-list";
    }
    */

    /* Comentado para resolver conflicto con AgentConfigController
    @GetMapping("/agents/new")
    public String newAgentForm(Model model) {
        model.addAttribute("agent", new AgentDTO());
        model.addAttribute("isNew", true);
        return "agent-config";
    }
    */

    /* Comentado para resolver conflicto con AgentConfigController
    @GetMapping("/agents/edit/{agentId}")
    public String editAgentForm(@PathVariable Long agentId, Model model) {
        Optional<AgentDTO> agentOpt = aiServiceManager.getAgentById(agentId);
        
        if (agentOpt.isPresent()) {
            model.addAttribute("agent", agentOpt.get());
            model.addAttribute("isNew", false);
            return "agent-config";
        } else {
            return "redirect:/agents";
        }
    }
    */

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
            List<AgentDTO> userAgents = aiServiceManager.getUserAgents(userId);
            
            if (!userAgents.isEmpty()) {
                agentId = userAgents.get(0).getId();
                session.setAttribute("agentName", userAgents.get(0).getName());
            } else {
                // Si el usuario no tiene agentes, redirigir a la creación de uno
                return "redirect:/agents/new";
            }
        } else {
            // Obtener el nombre del agente para mostrarlo en la interfaz
            Optional<AgentDTO> agentOpt = aiServiceManager.getAgentById(agentId);
            if (agentOpt.isPresent()) {
                session.setAttribute("agentName", agentOpt.get().getName());
            }
        }
        
        // En un sistema real, buscaríamos la sesión de chat en la base de datos
        // o crearíamos una nueva si no existe
        if (chatSessionId == null) {
            chatSessionId = System.currentTimeMillis(); // Simulación simplificada
        }
        
        // Agregar datos necesarios para la vista
        model.addAttribute("agentId", agentId);
        model.addAttribute("chatSessionId", chatSessionId);
        model.addAttribute("userAgents", aiServiceManager.getUserAgents(userId));
        
        return "chat";
    }

    /**
     * Página de dashboard/panel de control.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // En un sistema real, cargamos estadísticas y métricas aquí
        return "dashboard";
    }
} 