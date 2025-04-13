package com.ai.avance.business;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase base para todas las lógicas de integración con sistemas externos.
 * Proporciona métodos comunes y gestión de errores para las integraciones.
 */
@Slf4j
public abstract class IntegrationLogicBase {
    
    /**
     * Ejecuta una operación de integración con manejo de errores estándar.
     * 
     * @param <T> Tipo de retorno de la operación
     * @param operation Operación a ejecutar
     * @param defaultValue Valor por defecto en caso de error
     * @param errorMessage Mensaje de error a registrar
     * @return El resultado de la operación o el valor por defecto en caso de error
     */
    protected <T> T executeWithErrorHandling(IntegrationOperation<T> operation, T defaultValue, String errorMessage) {
        try {
            return operation.execute();
        } catch (Exception e) {
            log.error(errorMessage, e);
            return defaultValue;
        }
    }
    
    /**
     * Ejecuta una operación de integración sin valor de retorno con manejo de errores estándar.
     * 
     * @param operation Operación a ejecutar
     * @param errorMessage Mensaje de error a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    protected boolean executeVoidWithErrorHandling(VoidIntegrationOperation operation, String errorMessage) {
        try {
            operation.execute();
            return true;
        } catch (Exception e) {
            log.error(errorMessage, e);
            return false;
        }
    }
    
    /**
     * Interfaz funcional para operaciones de integración con valor de retorno.
     */
    @FunctionalInterface
    protected interface IntegrationOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones de integración sin valor de retorno.
     */
    @FunctionalInterface
    protected interface VoidIntegrationOperation {
        void execute() throws Exception;
    }
}
