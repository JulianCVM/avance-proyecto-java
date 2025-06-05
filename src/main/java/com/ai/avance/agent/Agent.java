package com.ai.avance.agent;

import com.ai.avance.agent.behavior.Behavior;
import com.ai.avance.agent.perception.Sensor;
import com.ai.avance.agent.decision.DecisionMaker;
import org.springframework.stereotype.Component;

@Component
public class Agent {
    private AgentState currentState;
    private AgentMemory memory;
    private Behavior behavior;
    private Sensor sensor;
    private DecisionMaker decisionMaker;

    public Agent(Behavior behavior, Sensor sensor, DecisionMaker decisionMaker) {
        this.currentState = AgentState.IDLE;
        this.memory = new AgentMemory();
        this.behavior = behavior;
        this.sensor = sensor;
        this.decisionMaker = decisionMaker;
    }

    public void perceive() {
        sensor.perceive();
    }

    public void think() {
        decisionMaker.makeDecision(memory.getCurrentState());
    }

    public void act() {
        behavior.execute();
    }

    public void updateState(AgentState newState) {
        this.currentState = newState;
        memory.updateState(newState);
    }

    public AgentState getCurrentState() {
        return currentState;
    }
} 