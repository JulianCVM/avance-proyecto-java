package com.ai.avance.agent.decision;

import com.ai.avance.agent.AgentState;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultDecisionMaker implements DecisionMaker {
    private static final Logger logger = Logger.getLogger(DefaultDecisionMaker.class.getName());
    private Strategy currentStrategy;
    private List<Strategy> availableStrategies;

    public DefaultDecisionMaker() {
        this.availableStrategies = new ArrayList<>();
    }

    @Override
    public void makeDecision(AgentState currentState) {
        logger.info("Tomando decisión para el estado: " + currentState);
        
        // Seleccionar la estrategia más apropiada
        Strategy selectedStrategy = selectStrategy(currentState);
        if (selectedStrategy != null) {
            selectedStrategy.executeStrategy(currentState);
        } else {
            logger.warning("No se encontró una estrategia aplicable para el estado: " + currentState);
        }
    }

    @Override
    public void updateStrategy(Strategy newStrategy) {
        this.currentStrategy = newStrategy;
        if (!availableStrategies.contains(newStrategy)) {
            availableStrategies.add(newStrategy);
        }
        logger.info("Estrategia actualizada: " + newStrategy.getStrategyName());
    }

    @Override
    public Strategy getCurrentStrategy() {
        return currentStrategy;
    }

    private Strategy selectStrategy(AgentState currentState) {
        return availableStrategies.stream()
                .filter(strategy -> strategy.isApplicable(currentState))
                .findFirst()
                .orElse(null);
    }
} 