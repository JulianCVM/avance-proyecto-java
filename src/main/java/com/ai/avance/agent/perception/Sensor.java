package com.ai.avance.agent.perception;

public interface Sensor {
    void perceive();
    Object getPerception();
    boolean isPerceptionValid();
} 