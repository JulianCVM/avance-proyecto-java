package com.ai.avance.agent.behavior;

import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class ReactiveBehavior implements Behavior {
    private static final Logger logger = Logger.getLogger(ReactiveBehavior.class.getName());
    private Object currentData;

    @Override
    public void execute() {
        logger.info("Ejecutando comportamiento reactivo");
        // Implementar lógica reactiva basada en currentData
    }

    @Override
    public boolean canExecute() {
        return currentData != null;
    }

    @Override
    public void updateBehavior(Object newData) {
        this.currentData = newData;
        logger.info("Comportamiento reactivo actualizado con nuevos datos");
    }
} 