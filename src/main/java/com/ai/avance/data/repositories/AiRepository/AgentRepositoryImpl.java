package com.ai.avance.data.repositories.AiRepository;

import com.ai.avance.data.entities.AIEntities.AgentEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de agentes para desarrollo.
 */
@Repository
public class AgentRepositoryImpl implements AgentRepository {

    private final Map<Long, AgentEntity> agents = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public AgentEntity save(AgentEntity agent) {
        if (agent.getId() == null) {
            agent.setId(idCounter.getAndIncrement());
        }
        agents.put(agent.getId(), agent);
        return agent;
    }

    @Override
    public Optional<AgentEntity> findById(Long id) {
        return Optional.ofNullable(agents.get(id));
    }

    @Override
    public List<AgentEntity> findByUserId(Long userId) {
        return agents.values().stream()
                .filter(agent -> agent.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentEntity> findByNameOrDescriptionContains(String searchTerm) {
        String lowercaseSearchTerm = searchTerm.toLowerCase();
        return agents.values().stream()
                .filter(agent -> 
                    (agent.getName() != null && agent.getName().toLowerCase().contains(lowercaseSearchTerm)) ||
                    (agent.getDescription() != null && agent.getDescription().toLowerCase().contains(lowercaseSearchTerm))
                )
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        agents.remove(id);
    }

    @Override
    public List<AgentEntity> findByActiveTrue() {
        return agents.values().stream()
                .filter(AgentEntity::isActive)
                .collect(Collectors.toList());
    }
} 