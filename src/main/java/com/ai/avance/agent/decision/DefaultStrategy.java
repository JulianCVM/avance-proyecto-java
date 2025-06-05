package com.ai.avance.agent.decision;

import com.ai.avance.agent.AgentState;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class DefaultStrategy implements Strategy {
    private static final Logger logger = Logger.getLogger(DefaultStrategy.class.getName());
    private static final String STRATEGY_NAME = "Estrategia por Defecto";

    @Override
    public void executeStrategy(AgentState currentState) {
        logger.info("Ejecutando estrategia por defecto para el estado: " + currentState);
        // Implementar lógica de la estrategia
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean isApplicable(AgentState currentState) {
        // Esta estrategia es aplicable en cualquier estado excepto ERROR
        return currentState != AgentState.ERROR;
    }
} 