package com.ai.avance.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private static final Logger logger = Logger.getLogger(AgentController.class.getName());
    
    private final Agent agent;

    @Autowired
    public AgentController(Agent agent) {
        this.agent = agent;
    }

    @PostMapping("/execute")
    public String executeAgent() {
        logger.info("Iniciando ciclo de ejecución del agente");
        agent.perceive();
        agent.think();
        agent.act();
        return "Ciclo de ejecución completado";
    }

    @GetMapping("/state")
    public AgentState getAgentState() {
        return agent.getCurrentState();
    }
} 