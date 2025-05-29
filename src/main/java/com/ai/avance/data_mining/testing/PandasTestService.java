package com.ai.avance.data_mining.testing;

import com.ai.avance.config.DataMiningConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para demostraciones y pruebas de Pandas.
 * Este servicio permite generar datos de prueba y ejecutar análisis
 * con Pandas para demostración de sus capacidades.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PandasTestService {

    private final DataGenerator dataGenerator;
    private final DataMiningConfig.Paths dataMiningPaths;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Genera datos de prueba para la demostración
     * @param numSessions número de sesiones a generar
     * @return mapa con las rutas de los archivos generados y resúmenes
     */
    public Map<String, Object> generateTestData(int numSessions) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Asegurar que los directorios existan
            dataGenerator.ensureDirectoriesExist();
            
            // Generar datos de mensajes
            int[] messagesPerSession = {4, 20}; // Rango de mensajes por sesión
            String messagesPath = dataGenerator.generateMessageData(numSessions, messagesPerSession);
            results.put("messagesPath", messagesPath);
            
            // Generar datos de sesiones
            String sessionsPath = dataGenerator.generateSessionData(numSessions);
            results.put("sessionsPath", sessionsPath);
            
            // Generar script de Python para análisis con Pandas
            String scriptPath = dataGenerator.generatePandasDataGenerationScript(numSessions);
            results.put("scriptPath", scriptPath);
            
            // Ejecutar el script para generar resumen básico
            String scriptOutput = executePythonScript(scriptPath);
            results.put("scriptOutput", scriptOutput);
            
            // Verificar y analizar los archivos CSV generados por Pandas
            String baseRawPath = dataMiningPaths.getRawDataPath();
            
            // Posibles ubicaciones para los archivos generados
            String[] possibleSessionPaths = {
                baseRawPath + "/pandas_generated_sessions.csv",
                baseRawPath + "/../pandas_generated_sessions.csv",
                baseRawPath + "/../raw/pandas_generated_sessions.csv"
            };
            
            String[] possibleMessagePaths = {
                baseRawPath + "/pandas_generated_messages.csv",
                baseRawPath + "/../pandas_generated_messages.csv",
                baseRawPath + "/../raw/pandas_generated_messages.csv"
            };
            
            // Buscar el archivo de sesiones
            File pandasSessionsFile = null;
            String pandasSessionsPath = null;
            
            for (String path : possibleSessionPaths) {
                File file = new File(path);
                if (file.exists()) {
                    pandasSessionsFile = file;
                    pandasSessionsPath = path;
                    break;
                }
            }
            
            // Buscar el archivo de mensajes
            File pandasMessagesFile = null;
            String pandasMessagesPath = null;
            
            for (String path : possibleMessagePaths) {
                File file = new File(path);
                if (file.exists()) {
                    pandasMessagesFile = file;
                    pandasMessagesPath = path;
                    break;
                }
            }
            
            // Si existen los archivos generados por Pandas, analizarlos
            if (pandasSessionsFile != null) {
                log.info("Analizando archivo de sesiones generado por Pandas: {}", pandasSessionsPath);
                String sessionAnalysisScript = generateAnalysisScript("sesiones", pandasSessionsPath);
                String sessionAnalysisOutput = executePythonScript(sessionAnalysisScript);
                results.put("sessionAnalysisOutput", sessionAnalysisOutput);
            } else {
                log.warn("No se encontró el archivo de sesiones generado por Pandas");
            }
            
            if (pandasMessagesFile != null) {
                log.info("Analizando archivo de mensajes generado por Pandas: {}", pandasMessagesPath);
                String messageAnalysisScript = generateAnalysisScript("mensajes", pandasMessagesPath);
                String messageAnalysisOutput = executePythonScript(messageAnalysisScript);
                results.put("messageAnalysisOutput", messageAnalysisOutput);
            } else {
                log.warn("No se encontró el archivo de mensajes generado por Pandas");
            }
            
            results.put("status", "success");
            results.put("numSessions", numSessions);
            results.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            
        } catch (Exception e) {
            log.error("Error al generar datos de prueba", e);
            results.put("status", "error");
            results.put("errorMessage", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Genera un script de análisis de Pandas para un conjunto de datos
     * @param dataType tipo de datos a analizar ("mensajes" o "sesiones")
     * @param filePath ruta al archivo CSV con los datos
     * @return ruta al script generado
     */
    public String generateAnalysisScript(String dataType, String filePath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputDir = dataMiningPaths.getResultsPath();
        String scriptPath = dataMiningPaths.getProcessedDataPath() + "/pandas_analysis_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n");
            writer.write("import os\n");
            writer.write("import json\n\n");
            
            writer.write("# Configuración\n");
            writer.write("pd.set_option('display.max_columns', None)\n");
            writer.write("pd.set_option('display.width', None)\n");
            writer.write("pd.set_option('display.max_colwidth', 50)\n\n");
            
            writer.write("# Configurar estilo de visualización\n");
            writer.write("sns.set(style='whitegrid')\n");
            writer.write("plt.rcParams['figure.figsize'] = (12, 8)\n\n");
            
            writer.write("# Crear directorio para resultados\n");
            writer.write(String.format("output_dir = '%s'\n", outputDir.replace("\\", "\\\\")));
            writer.write("try:\n");
            writer.write("    os.makedirs(output_dir, exist_ok=True)\n");
            writer.write("    print('Directorio de resultados creado:', output_dir)\n");
            writer.write("except Exception as e:\n");
            writer.write("    print('Error al crear directorio de resultados:', e)\n");
            writer.write("    # Intentar con un directorio alternativo\n");
            writer.write("    output_dir = './data/mining/results'\n");
            writer.write("    os.makedirs(output_dir, exist_ok=True)\n");
            writer.write("    print('Usando directorio alternativo:', output_dir)\n\n");
            
            writer.write("# Función para guardar figuras\n");
            writer.write("def save_fig(fig, name):\n");
            writer.write("    fig.savefig(os.path.join(output_dir, name + '.png'), bbox_inches='tight')\n");
            writer.write("    plt.close(fig)\n\n");
            
            writer.write("# Cargar datos\n");
            writer.write(String.format("df = pd.read_csv('%s')\n\n", filePath.replace("\\", "\\\\")));
            
            if (dataType.equals("mensajes")) {
                // Análisis específico para mensajes
                writer.write("# Convertir columnas de fecha\n");
                writer.write("if 'timestamp' in df.columns:\n");
                writer.write("    df['timestamp'] = pd.to_datetime(df['timestamp'])\n");
                writer.write("    df['hour_of_day'] = df['timestamp'].dt.hour\n");
                writer.write("    df['day_of_week'] = df['timestamp'].dt.dayofweek\n");
                writer.write("    df['month'] = df['timestamp'].dt.month\n");
                writer.write("    df['year'] = df['timestamp'].dt.year\n\n");
                
                writer.write("# Análisis básico\n");
                writer.write("print('\\nResumen estadístico:')\n");
                writer.write("print(df.describe())\n\n");
                
                writer.write("print('\\nDistribución por rol:')\n");
                writer.write("role_counts = df['role'].value_counts()\n");
                writer.write("print(role_counts)\n\n");
                
                writer.write("# Visualizaciones\n");
                writer.write("print('\\nGenerando visualizaciones...')\n\n");
                
                writer.write("# 1. Distribución de mensajes por rol\n");
                writer.write("fig, ax = plt.subplots()\n");
                writer.write("sns.countplot(data=df, x='role', ax=ax)\n");
                writer.write("ax.set_title('Distribución de Mensajes por Rol')\n");
                writer.write("save_fig(fig, 'role_distribution')\n\n");
                
                writer.write("# 2. Distribución de mensajes por tema\n");
                writer.write("if 'topic' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    topic_counts = df['topic'].value_counts()\n");
                writer.write("    topic_counts.plot(kind='bar', ax=ax)\n");
                writer.write("    ax.set_title('Distribución de Mensajes por Tema')\n");
                writer.write("    ax.set_ylabel('Número de Mensajes')\n");
                writer.write("    ax.set_xlabel('Tema')\n");
                writer.write("    plt.xticks(rotation=45, ha='right')\n");
                writer.write("    save_fig(fig, 'topic_distribution')\n\n");
                
                writer.write("# 3. Actividad por hora del día\n");
                writer.write("if 'hour_of_day' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    hour_counts = df.groupby('hour_of_day').size()\n");
                writer.write("    hour_counts.plot(kind='bar', ax=ax)\n");
                writer.write("    ax.set_title('Actividad por Hora del Día')\n");
                writer.write("    ax.set_ylabel('Número de Mensajes')\n");
                writer.write("    ax.set_xlabel('Hora')\n");
                writer.write("    save_fig(fig, 'hour_activity')\n\n");
                
                writer.write("# 4. Longitud de mensajes por rol\n");
                writer.write("if 'content' in df.columns:\n");
                writer.write("    df['content_length'] = df['content'].str.len()\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    sns.boxplot(data=df, x='role', y='content_length', ax=ax)\n");
                writer.write("    ax.set_title('Longitud de Mensajes por Rol')\n");
                writer.write("    ax.set_ylabel('Longitud (caracteres)')\n");
                writer.write("    ax.set_xlabel('Rol')\n");
                writer.write("    save_fig(fig, 'message_length_by_role')\n\n");
                
            } else if (dataType.equals("sesiones")) {
                // Análisis específico para sesiones
                writer.write("# Convertir columnas de fecha\n");
                writer.write("if 'created_at' in df.columns:\n");
                writer.write("    df['created_at'] = pd.to_datetime(df['created_at'])\n");
                writer.write("if 'last_activity' in df.columns:\n");
                writer.write("    df['last_activity'] = pd.to_datetime(df['last_activity'])\n\n");
                
                writer.write("# Análisis básico\n");
                writer.write("print('\\nResumen estadístico:')\n");
                writer.write("print(df.describe())\n\n");
                
                writer.write("print('\\nDistribución por tema:')\n");
                writer.write("if 'topic' in df.columns:\n");
                writer.write("    topic_counts = df['topic'].value_counts()\n");
                writer.write("    print(topic_counts)\n\n");
                
                writer.write("# Visualizaciones\n");
                writer.write("print('\\nGenerando visualizaciones...')\n\n");
                
                writer.write("# 1. Distribución de duración de sesiones\n");
                writer.write("if 'duration_minutes' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    sns.histplot(data=df, x='duration_minutes', bins=20, ax=ax)\n");
                writer.write("    ax.set_title('Distribución de Duración de Sesiones')\n");
                writer.write("    ax.set_xlabel('Duración (minutos)')\n");
                writer.write("    ax.set_ylabel('Frecuencia')\n");
                writer.write("    save_fig(fig, 'session_duration')\n\n");
                
                writer.write("# 2. Distribución de mensajes por sesión\n");
                writer.write("if 'message_count' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    sns.histplot(data=df, x='message_count', bins=15, ax=ax)\n");
                writer.write("    ax.set_title('Distribución de Mensajes por Sesión')\n");
                writer.write("    ax.set_xlabel('Número de Mensajes')\n");
                writer.write("    ax.set_ylabel('Frecuencia')\n");
                writer.write("    save_fig(fig, 'messages_per_session')\n\n");
                
                writer.write("# 3. Distribución de sesiones por tema\n");
                writer.write("if 'topic' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    topic_counts = df['topic'].value_counts()\n");
                writer.write("    topic_counts.plot(kind='bar', ax=ax)\n");
                writer.write("    ax.set_title('Distribución de Sesiones por Tema')\n");
                writer.write("    ax.set_ylabel('Número de Sesiones')\n");
                writer.write("    ax.set_xlabel('Tema')\n");
                writer.write("    plt.xticks(rotation=45, ha='right')\n");
                writer.write("    save_fig(fig, 'sessions_by_topic')\n\n");
                
                writer.write("# 4. Relación entre duración y número de mensajes\n");
                writer.write("if 'duration_minutes' in df.columns and 'message_count' in df.columns:\n");
                writer.write("    fig, ax = plt.subplots()\n");
                writer.write("    sns.scatterplot(data=df, x='message_count', y='duration_minutes', ax=ax)\n");
                writer.write("    ax.set_title('Relación entre Duración y Número de Mensajes')\n");
                writer.write("    ax.set_xlabel('Número de Mensajes')\n");
                writer.write("    ax.set_ylabel('Duración (minutos)')\n");
                writer.write("    save_fig(fig, 'duration_vs_messages')\n\n");
            }
            
            writer.write("# Generar resumen JSON para la interfaz web\n");
            writer.write("results = {\n");
            writer.write("    'shape': df.shape,\n");
            writer.write("    'columns': df.columns.tolist(),\n");
            writer.write("    'summary': df.describe().to_dict(),\n");
            writer.write("    'null_counts': df.isnull().sum().to_dict(),\n");
            writer.write("    'sample_rows': df.head(5).to_dict('records')\n");
            writer.write("}\n\n");
            
            writer.write("# Guardar resumen\n");
            writer.write("result_path = os.path.join(output_dir, 'analysis_summary.json')\n");
            writer.write("with open(result_path, 'w') as f:\n");
            writer.write("    json.dump(results, f, default=str)\n\n");
            
            writer.write("print('\\nResumen guardado en: ' + result_path)\n");
            writer.write("print('Visualizaciones guardadas en: ' + output_dir)\n");
            
            log.info("Script de análisis generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de análisis", e);
            throw new RuntimeException("Error al generar script de análisis", e);
        }
    }
    
    /**
     * Procesa un archivo CSV subido y genera análisis
     * @param file archivo CSV a analizar
     * @param dataType tipo de datos en el archivo ("mensajes" o "sesiones")
     * @return resultados del análisis
     */
    public Map<String, Object> processUploadedFile(MultipartFile file, String dataType) {
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Guardar el archivo subido
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = "uploaded_" + timestamp + ".csv";
            String filePath = dataMiningPaths.getRawDataPath() + "/" + fileName;
            
            // Crear directorio si no existe
            File directory = new File(dataMiningPaths.getRawDataPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Guardar archivo
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            
            results.put("originalFileName", file.getOriginalFilename());
            results.put("savedFilePath", filePath);
            
            // Generar y ejecutar script de análisis
            String scriptPath = generateAnalysisScript(dataType, filePath);
            String scriptOutput = executePythonScript(scriptPath);
            
            results.put("scriptPath", scriptPath);
            results.put("scriptOutput", scriptOutput);
            results.put("status", "success");
            
        } catch (Exception e) {
            log.error("Error al procesar archivo subido", e);
            results.put("status", "error");
            results.put("errorMessage", e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Ejecuta un script Python
     * @param scriptPath ruta al script Python
     * @return resultado de la ejecución
     */
    private String executePythonScript(String scriptPath) {
        // Siempre usar proceso externo en lugar de Jython
        return executeExternalPythonProcess(scriptPath);
    }
    
    /**
     * Ejecuta un script Python como proceso externo
     * @param scriptPath ruta al script Python
     * @return salida del proceso
     */
    private String executeExternalPythonProcess(String scriptPath) {
        try {
            // Ruta específica al ejecutable de Python
            String pythonExecutable = "C:/Users/julia/AppData/Local/Programs/Python/Python313/python.exe";
            
            // También intentar con comandos genéricos si la ruta específica falla
            String[] possibleCommands = {
                pythonExecutable,
                "python",
                "python3",
                "py"
            };
            
            ProcessBuilder processBuilder = null;
            Process process = null;
            
            // Intentar con cada comando hasta que uno funcione
            for (String cmd : possibleCommands) {
                try {
                    log.info("Intentando ejecutar Python con: {}", cmd);
                    processBuilder = new ProcessBuilder(cmd, scriptPath);
                    processBuilder.redirectErrorStream(true);
                    process = processBuilder.start();
                    
                    // Si llegamos aquí sin excepción, el comando funcionó
                    log.info("Ejecutando script Python con comando: {}", cmd);
                    break;
                } catch (IOException e) {
                    log.warn("No se pudo ejecutar el script con el comando: {} - Error: {}", cmd, e.getMessage());
                    // Continuar con el siguiente comando
                }
            }
            
            if (process == null) {
                return "Error: No se pudo ejecutar ningún comando de Python. Asegúrate de que Python esté instalado y en el PATH del sistema.";
            }
            
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
            String output = scanner.hasNext() ? scanner.next() : "";
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.warn("El script Python terminó con código de salida: {}", exitCode);
                // Incluir más información en caso de error
                if (!output.isEmpty()) {
                    log.info("Salida del script con error: {}", output);
                }
            }
            
            return output;
            
        } catch (InterruptedException e) {
            log.error("Error al ejecutar script Python como proceso externo", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene el resumen del análisis guardado como JSON
     * @return datos del resumen como mapa
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAnalysisSummary() {
        try {
            String baseResultsPath = dataMiningPaths.getResultsPath();
            
            // Posibles ubicaciones del archivo de resumen
            String[] possiblePaths = {
                baseResultsPath + "/analysis_summary.json",
                baseResultsPath + "/../results/analysis_summary.json",
                baseResultsPath + "/../analysis_summary.json",
                "./data/mining/results/analysis_summary.json" // Ruta absoluta común
            };
            
            File summaryFile = null;
            
            // Buscar en todas las ubicaciones posibles
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists()) {
                    summaryFile = file;
                    log.info("Archivo de resumen encontrado en: {}", path);
                    break;
                }
            }
            
            if (summaryFile == null || !summaryFile.exists()) {
                log.warn("No se encontró archivo de resumen de análisis en ninguna ubicación");
                return new HashMap<>();
            }
            
            String content = new String(Files.readAllBytes(summaryFile.toPath()));
            
            // Usar Jackson para convertir JSON a Map
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(content, HashMap.class);
            
        } catch (IOException e) {
            log.error("Error al leer resumen de análisis", e);
            return new HashMap<>();
        }
    }
} 