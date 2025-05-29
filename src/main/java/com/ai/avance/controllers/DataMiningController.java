package com.ai.avance.controllers;

import com.ai.avance.data_mining.DataMiningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para exponer funcionalidades de minería de datos.
 */
@Slf4j
@RestController
@RequestMapping("/api/data-mining")
@RequiredArgsConstructor
public class DataMiningController {
    
    private final DataMiningService dataMiningService;
    
    /**
     * Ejecuta el pipeline completo de minería de datos
     * @return resultados del pipeline
     */
    @PostMapping("/full-pipeline")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> runFullPipeline() {
        log.info("Solicitud para ejecutar el pipeline completo de minería de datos");
        Map<String, Object> results = dataMiningService.runFullDataMiningPipeline();
        return ResponseEntity.ok(results);
    }
    
    /**
     * Ejecuta sólo la fase de extracción y limpieza de datos
     * @return resultados de la extracción y limpieza
     */
    @PostMapping("/extract-clean")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> extractAndCleanData() {
        log.info("Solicitud para extraer y limpiar datos");
        Map<String, Object> results = dataMiningService.extractAndCleanData();
        return ResponseEntity.ok(results);
    }
    
    /**
     * Ejecuta sólo la fase de análisis y visualización
     * @param messagesPath ruta al archivo CSV de mensajes procesados
     * @param sessionsPath ruta al archivo CSV de sesiones procesadas
     * @return resultados del análisis y visualización
     */
    @PostMapping("/analyze-visualize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> analyzeAndVisualize(
            @RequestParam String messagesPath,
            @RequestParam String sessionsPath) {
        log.info("Solicitud para analizar y visualizar datos. Mensajes: {}, Sesiones: {}", messagesPath, sessionsPath);
        Map<String, Object> results = dataMiningService.analyzeAndVisualize(messagesPath, sessionsPath);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Genera el script para un dashboard interactivo
     * @param messagesPath ruta al archivo CSV de mensajes procesados
     * @param sessionsPath ruta al archivo CSV de sesiones procesadas
     * @return ruta al script de dashboard generado
     */
    @PostMapping("/generate-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateDashboard(
            @RequestParam String messagesPath,
            @RequestParam String sessionsPath) {
        log.info("Solicitud para generar dashboard. Mensajes: {}, Sesiones: {}", messagesPath, sessionsPath);
        String dashboardScriptPath = dataMiningService.generateDashboard(messagesPath, sessionsPath);
        return ResponseEntity.ok(dashboardScriptPath);
    }
} 