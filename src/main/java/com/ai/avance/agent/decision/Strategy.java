package com.ai.avance.agent.decision;

import com.ai.avance.agent.AgentState;

public interface Strategy {
    void executeStrategy(AgentState currentState);
    String getStrategyName();
    boolean isApplicable(AgentState currentState);
} 