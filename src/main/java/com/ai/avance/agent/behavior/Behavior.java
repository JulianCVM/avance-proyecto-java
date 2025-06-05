package com.ai.avance.agent.behavior;

public interface Behavior {
    void execute();
    boolean canExecute();
    void updateBehavior(Object newData);
} 