package com.ai.avance.agent;

import java.util.ArrayList;
import java.util.List;

public class AgentMemory {
    private List<AgentState> stateHistory;
    private List<Object> knowledgeBase;

    public AgentMemory() {
        this.stateHistory = new ArrayList<>();
        this.knowledgeBase = new ArrayList<>();
    }

    public void updateState(AgentState state) {
        stateHistory.add(state);
    }

    public AgentState getCurrentState() {
        return stateHistory.isEmpty() ? AgentState.IDLE : stateHistory.get(stateHistory.size() - 1);
    }

    public void addKnowledge(Object knowledge) {
        knowledgeBase.add(knowledge);
    }

    public List<Object> getKnowledgeBase() {
        return new ArrayList<>(knowledgeBase);
    }
} 