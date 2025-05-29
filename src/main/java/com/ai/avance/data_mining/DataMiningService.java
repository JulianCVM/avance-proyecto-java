package com.ai.avance.data_mining;

import com.ai.avance.config.DataMiningConfig;
import com.ai.avance.data_mining.extractors.DataExtractor;
import com.ai.avance.data_mining.processors.DataProcessor;
import com.ai.avance.data_mining.analyzers.DataAnalyzer;
import com.ai.avance.data_mining.visualizers.DataVisualizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio principal de minería de datos que integra todos los componentes.
 * Este servicio coordina el flujo completo de extracción, procesamiento,
 * análisis y visualización de datos de conversaciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataMiningService {
    
    private final DataExtractor extractor;
    private final DataProcessor processor;
    private final DataAnalyzer analyzer;
    private final DataVisualizer visualizer;
    private final DataMiningConfig.Paths dataMiningPaths;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Ejecuta el pipeline completo de minería de datos
     * @return mapa con los resultados y rutas de archivos generados
     */
    public Map<String, Object> runFullDataMiningPipeline() {
        Map<String, Object> results = new HashMap<>();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        
        try {
            log.info("Iniciando pipeline de minería de datos...");
            
            // Paso 1: Extracción de datos
            log.info("Paso 1: Extrayendo datos...");
            String messagesCSV = extractor.generateMessagesCSV();
            String sessionsCSV = extractor.generateSessionsCSV();
            
            results.put("rawMessagesPath", messagesCSV);
            results.put("rawSessionsPath", sessionsCSV);
            
            // Paso 2: Procesamiento y limpieza
            log.info("Paso 2: Procesando y limpiando datos...");
            String cleaningScript = processor.generateCleaningScript(messagesCSV, sessionsCSV);
            String cleaningResult = processor.executePythonScript(cleaningScript);
            
            // Determinar rutas de archivos de salida del script de limpieza
            String cleanedMessagesPath = dataMiningPaths.getProcessedDataPath() + "/cleaned_data_" + timestamp + "_messages.csv";
            String cleanedSessionsPath = dataMiningPaths.getProcessedDataPath() + "/cleaned_data_" + timestamp + "_sessions.csv";
            
            results.put("cleaningScript", cleaningScript);
            results.put("cleaningResult", cleaningResult);
            results.put("cleanedMessagesPath", cleanedMessagesPath);
            results.put("cleanedSessionsPath", cleanedSessionsPath);
            
            // Comprobar que se generaron los archivos limpios
            if (!new File(cleanedMessagesPath).exists() || !new File(cleanedSessionsPath).exists()) {
                log.warn("Los archivos limpios no fueron generados, usando los archivos originales");
                cleanedMessagesPath = messagesCSV;
                cleanedSessionsPath = sessionsCSV;
            }
            
            // Paso 3: Ingeniería de características
            log.info("Paso 3: Generando características...");
            String featureEngineeringScript = processor.generateFeatureEngineeringScript(cleanedMessagesPath, cleanedSessionsPath);
            String featureEngineeringResult = processor.executePythonScript(featureEngineeringScript);
            
            // Determinar rutas de archivos de salida del script de características
            String featuredMessagesPath = dataMiningPaths.getProcessedDataPath() + "/featured_data_" + timestamp + "_messages_features.csv";
            String featuredSessionsPath = dataMiningPaths.getProcessedDataPath() + "/featured_data_" + timestamp + "_session_features.csv";
            
            results.put("featureEngineeringScript", featureEngineeringScript);
            results.put("featureEngineeringResult", featureEngineeringResult);
            results.put("featuredMessagesPath", featuredMessagesPath);
            results.put("featuredSessionsPath", featuredSessionsPath);
            
            // Comprobar que se generaron los archivos con características
            if (!new File(featuredMessagesPath).exists() || !new File(featuredSessionsPath).exists()) {
                log.warn("Los archivos con características no fueron generados, usando los archivos limpios");
                featuredMessagesPath = cleanedMessagesPath;
                featuredSessionsPath = cleanedSessionsPath;
            }
            
            // Paso 4: Análisis exploratorio
            log.info("Paso 4: Realizando análisis exploratorio...");
            String edaScript = analyzer.generateExploratoryAnalysisScript(featuredMessagesPath, featuredSessionsPath);
            String edaResult = analyzer.executePythonScript(edaScript);
            
            results.put("edaScript", edaScript);
            results.put("edaResult", edaResult);
            
            // Paso 5: Clustering de temas
            log.info("Paso 5: Realizando clustering de temas...");
            String clusteringScript = analyzer.generateTopicClusteringScript(featuredMessagesPath);
            String clusteringResult = analyzer.executePythonScript(clusteringScript);
            
            results.put("clusteringScript", clusteringScript);
            results.put("clusteringResult", clusteringResult);
            
            // Paso 6: Visualizaciones
            log.info("Paso 6: Generando visualizaciones...");
            String staticVisualizationsScript = visualizer.generateStaticVisualizationsScript(featuredMessagesPath, featuredSessionsPath);
            String staticVisualizationsResult = visualizer.executePythonScript(staticVisualizationsScript);
            
            results.put("staticVisualizationsScript", staticVisualizationsScript);
            results.put("staticVisualizationsResult", staticVisualizationsResult);
            
            // Paso 7: Dashboard interactivo (sólo se genera el script, no se ejecuta automáticamente)
            log.info("Paso 7: Generando script de dashboard...");
            String dashboardScript = visualizer.generateDashboardScript(featuredMessagesPath, featuredSessionsPath);
            
            results.put("dashboardScript", dashboardScript);
            
            log.info("Pipeline de minería de datos completado con éxito");
            results.put("status", "success");
            
        } catch (Exception e) {
            log.error("Error durante el pipeline de minería de datos", e);
            results.put("status", "error");
            results.put("errorMessage", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Ejecuta sólo la fase de extracción y limpieza de datos
     * @return mapa con los resultados y rutas de archivos generados
     */
    public Map<String, Object> extractAndCleanData() {
        Map<String, Object> results = new HashMap<>();
        
        try {
            log.info("Extrayendo y limpiando datos...");
            
            // Extracción
            String messagesCSV = extractor.generateMessagesCSV();
            String sessionsCSV = extractor.generateSessionsCSV();
            
            results.put("rawMessagesPath", messagesCSV);
            results.put("rawSessionsPath", sessionsCSV);
            
            // Limpieza
            String cleaningScript = processor.generateCleaningScript(messagesCSV, sessionsCSV);
            String cleaningResult = processor.executePythonScript(cleaningScript);
            
            results.put("cleaningScript", cleaningScript);
            results.put("cleaningResult", cleaningResult);
            
            log.info("Extracción y limpieza completadas con éxito");
            results.put("status", "success");
            
        } catch (Exception e) {
            log.error("Error durante la extracción y limpieza de datos", e);
            results.put("status", "error");
            results.put("errorMessage", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Ejecuta sólo la fase de análisis y visualización
     * @param messagesPath ruta al archivo CSV de mensajes procesados
     * @param sessionsPath ruta al archivo CSV de sesiones procesadas
     * @return mapa con los resultados y rutas de archivos generados
     */
    public Map<String, Object> analyzeAndVisualize(String messagesPath, String sessionsPath) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            log.info("Analizando y visualizando datos...");
            
            // Análisis exploratorio
            String edaScript = analyzer.generateExploratoryAnalysisScript(messagesPath, sessionsPath);
            String edaResult = analyzer.executePythonScript(edaScript);
            
            results.put("edaScript", edaScript);
            results.put("edaResult", edaResult);
            
            // Clustering de temas
            String clusteringScript = analyzer.generateTopicClusteringScript(messagesPath);
            String clusteringResult = analyzer.executePythonScript(clusteringScript);
            
            results.put("clusteringScript", clusteringScript);
            results.put("clusteringResult", clusteringResult);
            
            // Visualizaciones estáticas
            String staticVisualizationsScript = visualizer.generateStaticVisualizationsScript(messagesPath, sessionsPath);
            String staticVisualizationsResult = visualizer.executePythonScript(staticVisualizationsScript);
            
            results.put("staticVisualizationsScript", staticVisualizationsScript);
            results.put("staticVisualizationsResult", staticVisualizationsResult);
            
            log.info("Análisis y visualización completados con éxito");
            results.put("status", "success");
            
        } catch (Exception e) {
            log.error("Error durante el análisis y visualización", e);
            results.put("status", "error");
            results.put("errorMessage", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Genera el script para un dashboard interactivo
     * @param messagesPath ruta al archivo CSV de mensajes procesados
     * @param sessionsPath ruta al archivo CSV de sesiones procesadas
     * @return ruta al script de dashboard generado
     */
    public String generateDashboard(String messagesPath, String sessionsPath) {
        log.info("Generando script de dashboard interactivo...");
        return visualizer.generateDashboardScript(messagesPath, sessionsPath);
    }
} 