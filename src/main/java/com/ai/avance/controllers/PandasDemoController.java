package com.ai.avance.controllers;

import com.ai.avance.data_mining.testing.PandasTestService;
import com.ai.avance.config.DataMiningConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    private final DataMiningConfig.Paths dataMiningPaths;
    
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
    public Map<String, Object> generateData(@RequestParam(defaultValue = "50") int numSessions) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Recibida solicitud para generar {} sesiones", numSessions);
            Map<String, Object> results = pandasTestService.generateTestData(numSessions);
            response.put("status", "success");
            response.put("scriptOutput", results.get("scriptOutput"));
            log.info("Datos generados exitosamente");
        } catch (Exception e) {
            log.error("Error generando datos de prueba", e);
            response.put("status", "error");
            response.put("errorMessage", "Error: " + e.getMessage() + "\n" + 
                          "Detalles: " + e.toString());
        }
        
        return response;
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
    
    /**
     * Sirve imágenes de visualización desde el directorio de resultados
     * @param filename nombre del archivo de imagen
     * @return recurso de imagen
     */
    @GetMapping("/results/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getResultImage(@PathVariable String filename) {
        String resultsPath = dataMiningPaths.getResultsPath();
        File imageFile = new File(resultsPath + "/" + filename);
        
        // Si no se encuentra en la ubicación principal, buscar en ubicaciones alternativas
        if (!imageFile.exists()) {
            log.info("Buscando imagen en ubicaciones alternativas: {}", filename);
            
            // Posibles ubicaciones alternativas
            String[] possiblePaths = {
                resultsPath + "/../results/" + filename,
                resultsPath + "/../" + filename,
                resultsPath + "/../../results/" + filename,
                "./data/mining/results/" + filename  // Ruta absoluta común
            };
            
            for (String path : possiblePaths) {
                File altFile = new File(path);
                if (altFile.exists()) {
                    imageFile = altFile;
                    log.info("Imagen encontrada en: {}", path);
                    break;
                }
            }
        }
        
        if (!imageFile.exists()) {
            log.warn("Imagen solicitada no encontrada: {}", filename);
            return ResponseEntity.notFound().build();
        }
        
        Resource resource = new FileSystemResource(imageFile);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    /**
     * Lista los archivos CSV disponibles en el directorio de datos
     * @return lista de archivos CSV
     */
    @GetMapping("/list-csv-files")
    @ResponseBody
    public Map<String, Object> listCsvFiles() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> files = pandasTestService.listCsvFiles();
            response.put("status", "success");
            response.put("files", files);
        } catch (Exception e) {
            log.error("Error listando archivos CSV", e);
            response.put("status", "error");
            response.put("errorMessage", e.getMessage());
        }
        
        return response;
    }

    /**
     * Analiza un archivo CSV existente
     * @param filename nombre del archivo a analizar
     * @return resultados del análisis
     */
    @PostMapping("/analyze-existing")
    @ResponseBody
    public Map<String, Object> analyzeExistingFile(@RequestParam String filename) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String scriptOutput = pandasTestService.analyzeExistingFile(filename);
            response.put("status", "success");
            response.put("scriptOutput", scriptOutput);
        } catch (Exception e) {
            log.error("Error analizando archivo existente", e);
            response.put("status", "error");
            response.put("errorMessage", e.getMessage());
        }
        
        return response;
    }
} 