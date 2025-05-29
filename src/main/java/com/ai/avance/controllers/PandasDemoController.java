package com.ai.avance.controllers;

import com.ai.avance.data_mining.testing.PandasTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador para la demostración de funcionalidades de Pandas.
 * Proporciona endpoints para la generación de datos de prueba, 
 * análisis con Pandas y visualización de resultados.
 */
@Slf4j
@Controller
@RequestMapping("/pandas")
@RequiredArgsConstructor
public class PandasDemoController {

    private final PandasTestService pandasTestService;
    
    /**
     * Página principal de demostración de Pandas
     * @param model modelo para la vista
     * @return nombre de la plantilla
     */
    @GetMapping
    public String pandasDemoPage(Model model) {
        // Añadir datos para la vista
        model.addAttribute("title", "Demostración de Pandas");
        model.addAttribute("description", "Explora las capacidades de análisis de datos con Pandas");
        
        // Intentar obtener cualquier resumen de análisis previo
        Map<String, Object> summary = pandasTestService.getAnalysisSummary();
        model.addAttribute("summary", summary);
        
        return "pandas/demo";
    }
    
    /**
     * Genera datos de prueba para la demostración
     * @param numSessions número de sesiones a generar (por defecto 50)
     * @return resultados de la generación
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateTestData(
            @RequestParam(defaultValue = "50") int numSessions) {
        log.info("Generando {} sesiones de datos de prueba para demostración de Pandas", numSessions);
        Map<String, Object> results = pandasTestService.generateTestData(numSessions);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Procesa un archivo CSV subido y realiza análisis con Pandas
     * @param file archivo CSV a analizar
     * @param dataType tipo de datos en el archivo ("mensajes" o "sesiones")
     * @return resultados del análisis
     */
    @PostMapping("/analyze")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeUploadedFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "mensajes") String dataType) {
        log.info("Analizando archivo CSV subido: {}, tipo: {}", file.getOriginalFilename(), dataType);
        Map<String, Object> results = pandasTestService.processUploadedFile(file, dataType);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Obtiene el resumen del análisis actual
     * @return datos del resumen
     */
    @GetMapping("/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAnalysisSummary() {
        Map<String, Object> summary = pandasTestService.getAnalysisSummary();
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Página de visualización de resultados
     * @param model modelo para la vista
     * @return nombre de la plantilla
     */
    @GetMapping("/visualize")
    public String visualizationPage(Model model) {
        // Añadir datos para la vista
        model.addAttribute("title", "Visualización de Datos con Pandas");
        model.addAttribute("description", "Visualizaciones generadas por análisis de Pandas");
        
        // Obtener el resumen del análisis
        Map<String, Object> summary = pandasTestService.getAnalysisSummary();
        model.addAttribute("summary", summary);
        
        return "pandas/visualize";
    }
} 