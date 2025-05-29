package com.ai.avance.data_mining.processors;

import com.ai.avance.config.DataMiningConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Procesador de datos para la minería de conversaciones.
 * Esta clase implementa algoritmos de limpieza y transformación de datos
 * para preparar las conversaciones para el análisis.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataProcessor {

    private final DataMiningConfig.Paths dataMiningPaths;
    private final DataMiningConfig.PandasOptions pandasOptions;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final List<String> transformationLog = new ArrayList<>();
    
    /**
     * Genera un script Python para limpiar y transformar los datos de conversaciones.
     * @param messagesCSVPath ruta al archivo CSV de mensajes
     * @param sessionsCSVPath ruta al archivo CSV de sesiones
     * @return ruta al script generado
     */
    public String generateCleaningScript(String messagesCSVPath, String sessionsCSVPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getProcessedDataPath() + "/cleaned_data_" + timestamp;
        String scriptPath = dataMiningPaths.getProcessedDataPath() + "/cleaning_script_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("from datetime import datetime\n\n");
            
            writer.write("# Configuración de pandas\n");
            writer.write("pd.set_option('display.max_columns', None)\n");
            writer.write("pd.set_option('display.width', None)\n");
            writer.write("pd.set_option('display.max_colwidth', 50)\n\n");
            
            writer.write("# Función para registrar transformaciones\n");
            writer.write("transformation_log = []\n");
            writer.write("def log_transform(name, original_shape, new_shape):\n");
            writer.write("    transformation_log.append(f\"{name}: {original_shape} -> {new_shape}\")\n\n");
            
            writer.write("# Cargar datos\n");
            writer.write("print('Cargando datos...')\n");
            writer.write(String.format("messages_df = pd.read_csv('%s')\n", messagesCSVPath.replace("\\", "\\\\")));
            writer.write(String.format("sessions_df = pd.read_csv('%s')\n\n", sessionsCSVPath.replace("\\", "\\\\")));
            
            writer.write("# Convertir tipos de datos\n");
            writer.write("print('Convirtiendo tipos de datos...')\n");
            writer.write("messages_df['timestamp'] = pd.to_datetime(messages_df['timestamp'])\n");
            writer.write("sessions_df['created_at'] = pd.to_datetime(sessions_df['created_at'])\n");
            writer.write("sessions_df['last_activity'] = pd.to_datetime(sessions_df['last_activity'])\n\n");
            
            writer.write("# Eliminar filas duplicadas\n");
            writer.write("print('Eliminando duplicados...')\n");
            writer.write("original_shape = messages_df.shape\n");
            writer.write("messages_df = messages_df.drop_duplicates()\n");
            writer.write("log_transform('Eliminar duplicados en mensajes', original_shape, messages_df.shape)\n\n");
            
            writer.write("# Manejar valores nulos\n");
            writer.write("print('Manejando valores nulos...')\n");
            writer.write("messages_df['model_used'] = messages_df['model_used'].fillna('unknown')\n");
            writer.write("messages_df['token_count'] = messages_df['token_count'].fillna(0)\n\n");
            
            writer.write("# Crear características de fecha\n");
            writer.write("print('Creando características de fecha...')\n");
            writer.write("messages_df['day_of_week'] = messages_df['timestamp'].dt.dayofweek\n");
            writer.write("messages_df['hour_of_day'] = messages_df['timestamp'].dt.hour\n");
            writer.write("messages_df['month'] = messages_df['timestamp'].dt.month\n");
            writer.write("messages_df['year'] = messages_df['timestamp'].dt.year\n\n");
            
            writer.write("# Calcular duración de sesiones\n");
            writer.write("print('Calculando duración de sesiones...')\n");
            writer.write("sessions_df['duration_minutes'] = (sessions_df['last_activity'] - sessions_df['created_at']).dt.total_seconds() / 60\n\n");
            
            writer.write("# Crear características de longitud de mensaje\n");
            writer.write("print('Analizando longitud de mensajes...')\n");
            writer.write("messages_df['content_length'] = messages_df['content'].str.len()\n");
            writer.write("messages_df['word_count'] = messages_df['content'].str.split().str.len()\n\n");
            
            writer.write("# Guardar datos procesados\n");
            writer.write(String.format("output_messages = '%s_messages.csv'\n", outputPath.replace("\\", "\\\\")));
            writer.write(String.format("output_sessions = '%s_sessions.csv'\n", outputPath.replace("\\", "\\\\")));
            writer.write("print(f'Guardando datos procesados en {output_messages} y {output_sessions}')\n");
            writer.write("messages_df.to_csv(output_messages, index=False)\n");
            writer.write("sessions_df.to_csv(output_sessions, index=False)\n\n");
            
            writer.write("# Guardar log de transformaciones\n");
            writer.write(String.format("with open('%s_transform_log.txt', 'w') as f:\n", outputPath.replace("\\", "\\\\")));
            writer.write("    for log in transformation_log:\n");
            writer.write("        f.write(log + '\\n')\n\n");
            
            writer.write("print('Procesamiento completado!')\n");
            writer.write("print(f'Mensajes procesados: {len(messages_df)}')\n");
            writer.write("print(f'Sesiones procesadas: {len(sessions_df)}')\n");
            
            log.info("Script de limpieza generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de limpieza", e);
            throw new RuntimeException("Error al generar script de limpieza", e);
        }
    }
    
    /**
     * Genera un script Python para la ingeniería de características
     * @param cleanedMessagesPath ruta al archivo CSV de mensajes limpios
     * @param cleanedSessionsPath ruta al archivo CSV de sesiones limpias
     * @return ruta al script generado
     */
    public String generateFeatureEngineeringScript(String cleanedMessagesPath, String cleanedSessionsPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getProcessedDataPath() + "/featured_data_" + timestamp;
        String scriptPath = dataMiningPaths.getProcessedDataPath() + "/feature_engineering_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("from sklearn.preprocessing import StandardScaler, OneHotEncoder\n");
            writer.write("from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer\n\n");
            
            writer.write("# Cargar datos limpios\n");
            writer.write(String.format("messages_df = pd.read_csv('%s')\n", cleanedMessagesPath.replace("\\", "\\\\")));
            writer.write(String.format("sessions_df = pd.read_csv('%s')\n\n", cleanedSessionsPath.replace("\\", "\\\\")));
            
            writer.write("# Convertir fechas\n");
            writer.write("messages_df['timestamp'] = pd.to_datetime(messages_df['timestamp'])\n");
            writer.write("sessions_df['created_at'] = pd.to_datetime(sessions_df['created_at'])\n");
            writer.write("sessions_df['last_activity'] = pd.to_datetime(sessions_df['last_activity'])\n\n");
            
            writer.write("# 1. Características de los usuarios\n");
            writer.write("print('Generando características de usuarios...')\n");
            writer.write("user_message_counts = messages_df.groupby('user_id').size().reset_index(name='message_count')\n");
            writer.write("user_token_usage = messages_df.groupby('user_id')['token_count'].sum().reset_index(name='total_tokens')\n");
            writer.write("user_session_counts = sessions_df.groupby('user_id').size().reset_index(name='session_count')\n");
            writer.write("user_features = user_message_counts.merge(user_token_usage, on='user_id')\n");
            writer.write("user_features = user_features.merge(user_session_counts, on='user_id', how='left')\n");
            writer.write("user_features['session_count'] = user_features['session_count'].fillna(0)\n");
            writer.write("user_features['avg_tokens_per_message'] = user_features['total_tokens'] / user_features['message_count']\n\n");
            
            writer.write("# 2. Características de las sesiones\n");
            writer.write("print('Generando características de sesiones...')\n");
            writer.write("session_message_counts = messages_df.groupby('session_id').size().reset_index(name='message_count')\n");
            writer.write("session_user_msgs = messages_df[messages_df['role'] == 'usuario'].groupby('session_id').size().reset_index(name='user_messages')\n");
            writer.write("session_assistant_msgs = messages_df[messages_df['role'] == 'asistente'].groupby('session_id').size().reset_index(name='assistant_messages')\n");
            writer.write("session_features = sessions_df.merge(session_message_counts, on='session_id')\n");
            writer.write("session_features = session_features.merge(session_user_msgs, on='session_id', how='left')\n");
            writer.write("session_features = session_features.merge(session_assistant_msgs, on='session_id', how='left')\n");
            writer.write("session_features['user_messages'] = session_features['user_messages'].fillna(0)\n");
            writer.write("session_features['assistant_messages'] = session_features['assistant_messages'].fillna(0)\n");
            writer.write("session_features['conversation_ratio'] = session_features['assistant_messages'] / session_features['user_messages'].replace(0, 1)\n\n");
            
            writer.write("# 3. Características de los mensajes\n");
            writer.write("print('Generando características de contenido de mensajes...')\n");
            writer.write("# Normalizar longitud y recuento de palabras\n");
            writer.write("scaler = StandardScaler()\n");
            writer.write("numeric_features = ['content_length', 'word_count', 'token_count']\n");
            writer.write("messages_df[numeric_features] = scaler.fit_transform(messages_df[numeric_features])\n\n");
            
            writer.write("# Codificación one-hot para roles\n");
            writer.write("role_dummies = pd.get_dummies(messages_df['role'], prefix='role')\n");
            writer.write("messages_df = pd.concat([messages_df, role_dummies], axis=1)\n\n");
            
            writer.write("# 4. Analizar patrones temporales\n");
            writer.write("print('Analizando patrones temporales...')\n");
            writer.write("time_patterns = messages_df.groupby(['hour_of_day', 'day_of_week']).size().reset_index(name='message_count')\n");
            writer.write("time_patterns_pivot = time_patterns.pivot(index='day_of_week', columns='hour_of_day', values='message_count').fillna(0)\n\n");
            
            writer.write("# 5. Guardar los datos procesados\n");
            writer.write(String.format("user_features.to_csv('%s_user_features.csv', index=False)\n", outputPath.replace("\\", "\\\\")));
            writer.write(String.format("session_features.to_csv('%s_session_features.csv', index=False)\n", outputPath.replace("\\", "\\\\")));
            writer.write(String.format("messages_df.to_csv('%s_messages_features.csv', index=False)\n", outputPath.replace("\\", "\\\\")));
            writer.write(String.format("time_patterns.to_csv('%s_time_patterns.csv', index=False)\n", outputPath.replace("\\", "\\\\")));
            
            writer.write("print('Ingeniería de características completada!')\n");
            
            log.info("Script de ingeniería de características generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de ingeniería de características", e);
            throw new RuntimeException("Error al generar script de ingeniería de características", e);
        }
    }
    
    /**
     * Ejecuta un script Python usando Jython
     * @param scriptPath ruta al script Python
     * @return resultado de la ejecución
     */
    public String executePythonScript(String scriptPath) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("python");
            
            if (engine == null) {
                log.error("Motor de Python (Jython) no disponible");
                return "Error: Motor de Python no disponible";
            }
            
            return engine.eval(new java.io.FileReader(scriptPath)).toString();
        } catch (ScriptException | IOException e) {
            log.error("Error al ejecutar script Python", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Obtiene el log de transformaciones realizadas
     * @return lista de transformaciones
     */
    public List<String> getTransformationLog() {
        return new ArrayList<>(transformationLog);
    }
    
    /**
     * Registra una transformación en el log
     * @param name nombre de la transformación
     * @param originalShape forma original de los datos
     * @param newShape nueva forma de los datos
     */
    private void logTransformation(String name, String originalShape, String newShape) {
        String logEntry = String.format("%s: %s -> %s", name, originalShape, newShape);
        transformationLog.add(logEntry);
        log.info("Transformación: {}", logEntry);
    }
} 