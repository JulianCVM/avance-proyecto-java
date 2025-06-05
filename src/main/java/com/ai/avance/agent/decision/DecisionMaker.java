package com.ai.avance.agent.decision;

import com.ai.avance.agent.AgentState;

public interface DecisionMaker {
    void makeDecision(AgentState currentState);
    void updateStrategy(Strategy newStrategy);
    Strategy getCurrentStrategy();
} 