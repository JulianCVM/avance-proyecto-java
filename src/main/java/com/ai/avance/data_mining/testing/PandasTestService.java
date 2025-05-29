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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;

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
            // Generar script para crear datos de ejemplo
            StringBuilder script = new StringBuilder();
            script.append("import pandas as pd\n");
            script.append("import numpy as np\n");
            script.append("import random\n");
            script.append("from datetime import datetime, timedelta\n");
            script.append("import os\n\n");

            // Configuración
            script.append("# Configuración\n");
            script.append("random.seed(42)\n");
            script.append("np.random.seed(42)\n\n");

            // Crear directorio para datos
            script.append("# Crear directorio para datos\n");
            script.append("raw_dir = './data/mining/raw'\n");
            script.append("os.makedirs(raw_dir, exist_ok=True)\n\n");

            // Generar datos de sesiones
            script.append("# Generar datos de sesiones\n");
            script.append("session_data = []\n");
            script.append("start_date = datetime.now() - timedelta(days=30)\n");
            script.append("topics = ['General', 'Técnico', 'Soporte', 'Ventas', 'Otros']\n");
            script.append("roles = ['Usuario', 'Asistente', 'Sistema']\n\n");

            script.append("for i in range(").append(numSessions).append("):\n");
            script.append("    created_at = start_date + timedelta(hours=random.randint(0, 720))\n");
            script.append("    duration = random.randint(5, 120)\n");
            script.append("    last_activity = created_at + timedelta(minutes=duration)\n");
            script.append("    topic = random.choice(topics)\n");
            script.append("    num_messages = random.randint(2, 20)\n\n");

            script.append("    session_data.append({\n");
            script.append("        'session_id': f'S{i+1:03d}',\n");
            script.append("        'created_at': created_at,\n");
            script.append("        'last_activity': last_activity,\n");
            script.append("        'duration_minutes': duration,\n");
            script.append("        'topic': topic,\n");
            script.append("        'num_messages': num_messages\n");
            script.append("    })\n\n");

            script.append("sessions_df = pd.DataFrame(session_data)\n");
            script.append("sessions_df.to_csv(os.path.join(raw_dir, 'sessions.csv'), index=False)\n\n");

            // Generar datos de mensajes
            script.append("# Generar datos de mensajes\n");
            script.append("message_data = []\n");
            script.append("message_templates = [\n");
            script.append("    'Hola, ¿cómo estás?',\n");
            script.append("    'Necesito ayuda con...',\n");
            script.append("    'Gracias por tu respuesta',\n");
            script.append("    '¿Podrías explicarme más sobre...?',\n");
            script.append("    'Entiendo, gracias',\n");
            script.append("    '¿Hay alguna otra opción?',\n");
            script.append("    'Perfecto, eso resuelve mi duda',\n");
            script.append("    '¿Cuánto tiempo tomará?',\n");
            script.append("    '¿Podemos agendar una llamada?',\n");
            script.append("    'Excelente servicio'\n");
            script.append("]\n\n");

            script.append("for session in session_data:\n");
            script.append("    session_id = session['session_id']\n");
            script.append("    start_time = session['created_at']\n");
            script.append("    end_time = session['last_activity']\n");
            script.append("    num_messages = session['num_messages']\n\n");

            script.append("    for i in range(num_messages):\n");
            script.append("        message_time = start_time + timedelta(minutes=random.randint(0, session['duration_minutes']))\n");
            script.append("        role = roles[i % len(roles)]\n");
            script.append("        content = random.choice(message_templates)\n\n");

            script.append("        message_data.append({\n");
            script.append("            'message_id': f'M{len(message_data)+1:04d}',\n");
            script.append("            'session_id': session_id,\n");
            script.append("            'timestamp': message_time,\n");
            script.append("            'role': role,\n");
            script.append("            'content': content,\n");
            script.append("            'topic': session['topic']\n");
            script.append("        })\n\n");

            script.append("messages_df = pd.DataFrame(message_data)\n");
            script.append("messages_df.to_csv(os.path.join(raw_dir, 'messages.csv'), index=False)\n\n");

            // Imprimir resumen
            script.append("# Imprimir resumen\n");
            script.append("print('\\nResumen de datos generados:')\n");
            script.append("print(f'Número de sesiones: {len(sessions_df)}')\n");
            script.append("print(f'Número de mensajes: {len(messages_df)}')\n");
            script.append("print('\\nDistribución de temas:')\n");
            script.append("print(sessions_df['topic'].value_counts())\n");
            script.append("print('\\nDistribución de roles:')\n");
            script.append("print(messages_df['role'].value_counts())\n");

            // Ejecutar script
            String scriptOutput = executePythonScript(script.toString());
            results.put("scriptOutput", scriptOutput);

            // Analizar los archivos generados
            String sessionsAnalysis = analyzeExistingFile("sessions.csv");
            String messagesAnalysis = analyzeExistingFile("messages.csv");

            results.put("sessionAnalysisOutput", sessionsAnalysis);
            results.put("messageAnalysisOutput", messagesAnalysis);

        } catch (Exception e) {
            log.error("Error generando datos de prueba", e);
            throw new RuntimeException("Error generando datos de prueba: " + e.getMessage(), e);
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

    /**
     * Lista los archivos CSV disponibles en el directorio de datos
     * @return lista de nombres de archivos CSV
     */
    public List<String> listCsvFiles() {
        List<String> files = new ArrayList<>();
        File rawDir = new File(dataMiningPaths.getRawDataPath());
        
        if (rawDir.exists() && rawDir.isDirectory()) {
            File[] csvFiles = rawDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (csvFiles != null) {
                for (File file : csvFiles) {
                    files.add(file.getName());
                }
            }
        }
        
        return files;
    }

    /**
     * Analiza un archivo CSV existente
     * @param filename nombre del archivo a analizar
     * @return salida del script de análisis
     */
    public String analyzeExistingFile(String filename) {
        try {
            // Validar que el archivo existe
            File csvFile = new File(dataMiningPaths.getRawDataPath(), filename);
            if (!csvFile.exists()) {
                throw new FileNotFoundException("El archivo " + filename + " no existe");
            }

            // Generar script de análisis
            String script = generateAnalysisScript(csvFile.getAbsolutePath());
            
            // Ejecutar script
            return executePythonScript(script);
        } catch (Exception e) {
            log.error("Error analizando archivo existente: " + filename, e);
            throw new RuntimeException("Error analizando archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el script de Python para analizar un archivo CSV
     * @param csvPath ruta al archivo CSV
     * @return script de Python
     */
    private String generateAnalysisScript(String csvPath) {
        StringBuilder script = new StringBuilder();
        script.append("import pandas as pd\n");
        script.append("import numpy as np\n");
        script.append("import matplotlib.pyplot as plt\n");
        script.append("import seaborn as sns\n");
        script.append("import os\n");
        script.append("import json\n\n");

        // Configuración
        script.append("# Configuración\n");
        script.append("pd.set_option('display.max_columns', None)\n");
        script.append("pd.set_option('display.width', None)\n");
        script.append("pd.set_option('display.max_colwidth', 50)\n\n");

        // Configurar estilo de visualización
        script.append("# Configurar estilo de visualización\n");
        script.append("sns.set(style='whitegrid')\n");
        script.append("plt.rcParams['figure.figsize'] = (12, 8)\n\n");

        // Crear directorio para resultados
        script.append("# Crear directorio para resultados\n");
        script.append("output_dir = './data/mining/results'\n");
        script.append("os.makedirs(output_dir, exist_ok=True)\n\n");

        // Función para guardar figuras
        script.append("# Función para guardar figuras\n");
        script.append("def save_fig(fig, name):\n");
        script.append("    fig.savefig(os.path.join(output_dir, f'{name}.png'), bbox_inches='tight')\n");
        script.append("    plt.close(fig)\n\n");

        // Cargar datos
        script.append("# Cargar datos\n");
        script.append("df = pd.read_csv('").append(csvPath).append("')\n\n");

        // Análisis básico
        script.append("# Análisis básico\n");
        script.append("print('\\nResumen estadístico:')\n");
        script.append("print(df.describe())\n\n");

        // Generar visualizaciones según el tipo de datos
        script.append("# Visualizaciones\n");
        script.append("print('\\nGenerando visualizaciones...')\n\n");

        // Distribución de valores por columna
        script.append("# Distribución de valores por columna\n");
        script.append("for column in df.select_dtypes(include=['object']).columns:\n");
        script.append("    fig, ax = plt.subplots()\n");
        script.append("    value_counts = df[column].value_counts()\n");
        script.append("    value_counts.plot(kind='bar', ax=ax)\n");
        script.append("    ax.set_title(f'Distribución de {column}')\n");
        script.append("    ax.set_ylabel('Frecuencia')\n");
        script.append("    plt.xticks(rotation=45, ha='right')\n");
        script.append("    save_fig(fig, f'distribution_{column}')\n\n");

        // Histogramas para columnas numéricas
        script.append("# Histogramas para columnas numéricas\n");
        script.append("for column in df.select_dtypes(include=['int64', 'float64']).columns:\n");
        script.append("    fig, ax = plt.subplots()\n");
        script.append("    sns.histplot(data=df, x=column, ax=ax)\n");
        script.append("    ax.set_title(f'Distribución de {column}')\n");
        script.append("    save_fig(fig, f'histogram_{column}')\n\n");

        // Generar resumen JSON
        script.append("# Generar resumen JSON\n");
        script.append("results = {\n");
        script.append("    'shape': df.shape,\n");
        script.append("    'columns': df.columns.tolist(),\n");
        script.append("    'summary': df.describe().to_dict(),\n");
        script.append("    'null_counts': df.isnull().sum().to_dict(),\n");
        script.append("    'sample_rows': df.head(5).to_dict('records')\n");
        script.append("}\n\n");

        // Guardar resumen
        script.append("# Guardar resumen\n");
        script.append("result_path = os.path.join(output_dir, 'analysis_summary.json')\n");
        script.append("with open(result_path, 'w') as f:\n");
        script.append("    json.dump(results, f, default=str)\n\n");

        script.append("print('\\nResumen guardado en:', result_path)\n");
        script.append("print('Visualizaciones guardadas en:', output_dir)\n");

        return script.toString();
    }
} 