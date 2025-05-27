package com.ai.avance.presentation.controllers;

import com.ai.avance.presentation.dto.ChatDTO.AgentDTO;
import com.ai.avance.services.AiServiceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para manejar las acciones relacionadas con la configuración de agentes.
 */
@Slf4j
@Controller
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentConfigController {

    private final AiServiceManager aiServiceManager;
    
    /**
     * Lista todos los agentes del usuario.
     */
    @GetMapping
    public String listAgents(Model model, @RequestParam(required = false) Long userId) {
        // En un sistema real, obtendríamos el userId de la sesión o autenticación
        Long userIdToUse = userId != null ? userId : 1L; // Usuario demo por defecto
        
        // Inicializar agentes demo
        aiServiceManager.initDemoAgents();
        
        List<AgentDTO> agents = aiServiceManager.getUserAgentDtos(userIdToUse);
        model.addAttribute("agents", agents);
        return "agent-list";
    }

    /**
     * Formulario para crear un nuevo agente.
     */
    @GetMapping("/new")
    public String newAgentForm(Model model) {
        model.addAttribute("agent", new AgentFormData());
        model.addAttribute("isNew", true);
        return "agent-config";
    }

    /**
     * Formulario para editar un agente existente.
     */
    @GetMapping("/edit/{agentId}")
    public String editAgentForm(@PathVariable Long agentId, Model model) {
        Optional<AgentDTO> agentOpt = aiServiceManager.getAgentDtoById(agentId);
        
        if (agentOpt.isPresent()) {
            AgentDTO agent = agentOpt.get();
            AgentFormData formData = new AgentFormData();
            formData.setId(agent.getId());
            formData.setName(agent.getName());
            formData.setDescription(agent.getDescription());
            formData.setPurpose(agent.getPurpose());
            formData.setTone(agent.getTone());
            formData.setDomainContext(agent.getDomainContext());
            formData.setAllowedTopicsString(agent.getAllowedTopics() != null ? 
                String.join(", ", agent.getAllowedTopics()) : "");
            formData.setRestrictedTopicsString(agent.getRestrictedTopics() != null ? 
                String.join(", ", agent.getRestrictedTopics()) : "");
            formData.setModelConfig(agent.getModelConfig());
            formData.setUserId(agent.getUserId());
            
            model.addAttribute("agent", formData);
            model.addAttribute("isNew", false);
            return "agent-config";
        } else {
            return "redirect:/agents";
        }
    }

    /**
     * Guarda un agente nuevo o actualiza uno existente.
     */
    @PostMapping("/save")
    public String saveAgent(@ModelAttribute("agent") AgentFormData formData, 
                           RedirectAttributes redirectAttributes) {
        try {
            AgentDTO agent = convertToEntity(formData);
            
            if (agent.getId() == null) {
                // Es un agente nuevo
                aiServiceManager.createAgentDto(agent, formData.getUserId() != null ? formData.getUserId() : 1L);
                redirectAttributes.addFlashAttribute("successMessage", "¡Agente creado exitosamente!");
            } else {
                // Es una actualización
                aiServiceManager.updateAgentDto(agent.getId(), agent);
                redirectAttributes.addFlashAttribute("successMessage", "¡Agente actualizado exitosamente!");
            }
            
            return "redirect:/agents";
        } catch (Exception e) {
            log.error("Error al guardar el agente", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el agente: " + e.getMessage());
            return "redirect:/agents/new";
        }
    }

    /**
     * Elimina un agente.
     */
    @PostMapping("/delete/{id}")
    public String deleteAgent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aiServiceManager.deleteAgentDto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Agente eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error al eliminar el agente con ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el agente");
        }
        return "redirect:/agents";
    }
    
    /**
     * Activa o desactiva un agente.
     */
    @PostMapping("/toggle/{id}")
    public String toggleAgentActivation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<AgentDTO> updated = aiServiceManager.toggleAgentDtoActivation(id);
            if (updated.isPresent()) {
                String status = updated.get().isActive() ? "activado" : "desactivado";
                redirectAttributes.addFlashAttribute("successMessage", "Agente " + status + " exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo encontrar el agente");
            }
        } catch (Exception e) {
            log.error("Error al cambiar estado del agente con ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cambiar el estado del agente");
        }
        return "redirect:/agents";
    }

    /**
     * Clase interna para manejar los datos del formulario.
     */
    public static class AgentFormData {
        private Long id;
        private String name;
        private String description;
        private String purpose;
        private String tone;
        private String styleConfig;
        private String domainContext;
        private String allowedTopicsString;
        private String restrictedTopicsString;
        private String modelConfig;
        private Long userId;
        
        // Getters y setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
        
        public String getTone() { return tone; }
        public void setTone(String tone) { this.tone = tone; }
        
        public String getStyleConfig() { return styleConfig; }
        public void setStyleConfig(String styleConfig) { this.styleConfig = styleConfig; }
        
        public String getDomainContext() { return domainContext; }
        public void setDomainContext(String domainContext) { this.domainContext = domainContext; }
        
        public String getAllowedTopicsString() { return allowedTopicsString; }
        public void setAllowedTopicsString(String allowedTopicsString) { this.allowedTopicsString = allowedTopicsString; }
        
        public String getRestrictedTopicsString() { return restrictedTopicsString; }
        public void setRestrictedTopicsString(String restrictedTopicsString) { this.restrictedTopicsString = restrictedTopicsString; }
        
        public String getModelConfig() { return modelConfig; }
        public void setModelConfig(String modelConfig) { this.modelConfig = modelConfig; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
    
    /**
     * Convierte los datos del formulario a una entidad de agente.
     */
    private AgentDTO convertToEntity(AgentFormData formData) {
        List<String> allowedTopics = formData.getAllowedTopicsString() != null ? 
                Arrays.stream(formData.getAllowedTopicsString().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()) : 
                List.of();
                
        List<String> restrictedTopics = formData.getRestrictedTopicsString() != null ? 
                Arrays.stream(formData.getRestrictedTopicsString().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()) : 
                List.of();
        
        return AgentDTO.builder()
                .id(formData.getId())
                .name(formData.getName())
                .description(formData.getDescription())
                .purpose(formData.getPurpose())
                .tone(formData.getTone())
                .domainContext(formData.getDomainContext())
                .allowedTopics(allowedTopics)
                .restrictedTopics(restrictedTopics)
                .modelConfig(formData.getModelConfig())
                .userId(formData.getUserId())
                .active(true) // Por defecto, los nuevos agentes están activos
                .build();
    }
} 