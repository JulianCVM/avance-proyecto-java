package com.ai.avance.services;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import com.ai.avance.data.repositories.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar el entrenamiento de modelos de IA.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {

    private final AgentRepository agentRepository;
    private final IntegrationService integrationService;
    
    // Mapa para seguir el estado de los entrenamientos
    private final Map<Long, TrainingStatus> trainingStatusMap = new HashMap<>();
    
    /**
     * Estado del proceso de entrenamiento.
     */
    public enum TrainingStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    /**
     * Inicia el entrenamiento para un agente específico.
     */
    public boolean startTraining(Long agentId) {
        Optional<AgentEntity> agentOpt = agentRepository.findById(agentId);
        
        if (agentOpt.isEmpty()) {
            log.error("No se puede iniciar el entrenamiento para un agente inexistente: {}", agentId);
            return false;
        }
        
        AgentEntity agent = agentOpt.get();
        
        // Verificar si hay tokens de API disponibles para el proveedor del modelo
        if (!integrationService.hasActiveTokenForProvider(agent.getModelProvider())) {
            log.error("No hay tokens de API disponibles para el proveedor: {}", agent.getModelProvider());
            return false;
        }
        
        // Preparar el estado del entrenamiento
        trainingStatusMap.put(agentId, TrainingStatus.PENDING);
        
        // Iniciar un proceso asincrónico para el entrenamiento (simulado)
        new Thread(() -> {
            try {
                // Cambiar el estado a EN PROGRESO
                trainingStatusMap.put(agentId, TrainingStatus.IN_PROGRESS);
                log.info("Entrenamiento iniciado para el agente {}", agentId);
                
                // Simular un proceso de entrenamiento
                Thread.sleep(5000);
                
                // Actualizar el agente con la información de entrenamiento
                agent.setUpdatedAt(LocalDateTime.now());
                agentRepository.save(agent);
                
                // Cambiar el estado a COMPLETADO
                trainingStatusMap.put(agentId, TrainingStatus.COMPLETED);
                log.info("Entrenamiento completado para el agente {}", agentId);
            } catch (Exception e) {
                // Cambiar el estado a FALLIDO en caso de error
                trainingStatusMap.put(agentId, TrainingStatus.FAILED);
                log.error("Error durante el entrenamiento del agente {}: {}", agentId, e.getMessage(), e);
            }
        }).start();
        
        return true;
    }
    
    /**
     * Obtiene el estado actual del entrenamiento para un agente.
     */
    public TrainingStatus getTrainingStatus(Long agentId) {
        return trainingStatusMap.getOrDefault(agentId, TrainingStatus.PENDING);
    }
    
    /**
     * Cancela un entrenamiento en curso.
     */
    public boolean cancelTraining(Long agentId) {
        if (!trainingStatusMap.containsKey(agentId) || 
            trainingStatusMap.get(agentId) == TrainingStatus.COMPLETED || 
            trainingStatusMap.get(agentId) == TrainingStatus.FAILED) {
            return false;
        }
        
        trainingStatusMap.put(agentId, TrainingStatus.FAILED);
        log.info("Entrenamiento cancelado para el agente {}", agentId);
        return true;
    }
}
