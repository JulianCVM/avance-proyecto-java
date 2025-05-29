package com.ai.avance.data_mining.extractors;

import com.ai.avance.config.DataMiningConfig;
import com.ai.avance.data.entities.ConversationEntities.ChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultChatSessionEntity;
import com.ai.avance.data.entities.ConversationEntities.MessageEntity;
import com.ai.avance.data.entities.ConversationEntities.DefaultMessageEntity;
import com.ai.avance.data.repositories.ChatSessionRepository;
import com.ai.avance.data.repositories.DefaultChatSessionRepository;
import com.ai.avance.data.repositories.MessageRepository;
import com.ai.avance.data.repositories.DefaultMessageRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Extractor de datos para la minería de conversaciones.
 * Esta clase se encarga de extraer datos de las entidades de conversación
 * y convertirlas a formatos que pueden ser procesados por pandas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataExtractor {

    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;
    private final DefaultChatSessionRepository defaultChatSessionRepository;
    private final DefaultMessageRepository defaultMessageRepository;
    private final DataMiningConfig.Paths dataMiningPaths;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Extrae todas las conversaciones de agentes personalizados y las guarda en formato JSON.
     * @return La ruta del archivo JSON generado
     */
    public String extractAgentConversations() {
        List<ChatSessionEntity> sessions = chatSessionRepository.findAll();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/agent_conversations_" + timestamp + ".json";
        
        try {
            objectMapper.writeValue(new File(filePath), sessions);
            log.info("Datos de conversaciones de agentes extraídos y guardados en: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al guardar conversaciones de agentes", e);
            throw new RuntimeException("Error al extraer datos de conversaciones de agentes", e);
        }
    }
    
    /**
     * Extrae todas las conversaciones con el chatbot por defecto y las guarda en formato JSON.
     * @return La ruta del archivo JSON generado
     */
    public String extractDefaultChatbotConversations() {
        List<DefaultChatSessionEntity> sessions = defaultChatSessionRepository.findAll();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/default_conversations_" + timestamp + ".json";
        
        try {
            objectMapper.writeValue(new File(filePath), sessions);
            log.info("Datos de conversaciones por defecto extraídos y guardados en: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al guardar conversaciones por defecto", e);
            throw new RuntimeException("Error al extraer datos de conversaciones por defecto", e);
        }
    }
    
    /**
     * Genera un CSV con mensajes de todas las conversaciones para análisis en pandas.
     * @return La ruta del archivo CSV generado
     */
    public String generateMessagesCSV() {
        List<MessageEntity> messages = messageRepository.findAll();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/messages_" + timestamp + ".csv";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Escribir encabezados
            writer.write("message_id,session_id,agent_id,user_id,role,content,timestamp,token_count,flagged,model_used\n");
            
            // Escribir datos
            for (MessageEntity message : messages) {
                writer.write(String.format("%d,%d,%d,%d,\"%s\",\"%s\",%s,%d,%b,\"%s\"\n",
                        message.getId(),
                        message.getChatSession().getId(),
                        message.getChatSession().getAgent() != null ? message.getChatSession().getAgent().getId() : 0,
                        message.getChatSession().getUser() != null ? message.getChatSession().getUser().getId() : 0,
                        message.getRole(),
                        message.getContent().replace("\"", "\"\""), // Escapar comillas
                        message.getTimestamp(),
                        message.getTokenCount(),
                        message.isFlagged(),
                        message.getModelUsed() != null ? message.getModelUsed() : ""
                ));
            }
            
            log.info("Datos de mensajes extraídos y guardados en CSV: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al generar CSV de mensajes", e);
            throw new RuntimeException("Error al generar CSV de mensajes", e);
        }
    }
    
    /**
     * Genera un CSV con sesiones de chat para análisis en pandas.
     * @return La ruta del archivo CSV generado
     */
    public String generateSessionsCSV() {
        List<ChatSessionEntity> sessions = chatSessionRepository.findAll();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/sessions_" + timestamp + ".csv";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Escribir encabezados
            writer.write("session_id,user_id,agent_id,title,created_at,last_activity,message_count,total_tokens\n");
            
            // Escribir datos
            for (ChatSessionEntity session : sessions) {
                writer.write(String.format("%d,%d,%d,\"%s\",%s,%s,%d,%d\n",
                        session.getId(),
                        session.getUser() != null ? session.getUser().getId() : 0,
                        session.getAgent() != null ? session.getAgent().getId() : 0,
                        session.getTitle() != null ? session.getTitle().replace("\"", "\"\"") : "",
                        session.getCreatedAt(),
                        session.getLastActivity(),
                        session.getMessages().size(),
                        session.getTotalTokensUsed()
                ));
            }
            
            log.info("Datos de sesiones extraídos y guardados en CSV: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al generar CSV de sesiones", e);
            throw new RuntimeException("Error al generar CSV de sesiones", e);
        }
    }
    
    /**
     * Genera un script Python para inicializar el entorno de pandas
     * @return La ruta del script generado
     */
    public String generatePandasInitScript() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/pandas_init_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n\n");
            
            writer.write("# Configuración de pandas\n");
            writer.write("pd.set_option('display.max_columns', None)\n");
            writer.write("pd.set_option('display.width', None)\n");
            writer.write("pd.set_option('display.max_colwidth', 50)\n\n");
            
            writer.write("# Cargar datos\n");
            writer.write("messages_df = pd.read_csv('" + generateMessagesCSV().replace("\\", "\\\\") + "')\n");
            writer.write("sessions_df = pd.read_csv('" + generateSessionsCSV().replace("\\", "\\\\") + "')\n\n");
            
            writer.write("# Convertir columnas de fecha\n");
            writer.write("messages_df['timestamp'] = pd.to_datetime(messages_df['timestamp'])\n");
            writer.write("sessions_df['created_at'] = pd.to_datetime(sessions_df['created_at'])\n");
            writer.write("sessions_df['last_activity'] = pd.to_datetime(sessions_df['last_activity'])\n\n");
            
            writer.write("print('Datos cargados correctamente!')\n");
            writer.write("print(f'Mensajes: {len(messages_df)}')\n");
            writer.write("print(f'Sesiones: {len(sessions_df)}')\n");
            
            log.info("Script de inicialización de pandas generado: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al generar script de inicialización de pandas", e);
            throw new RuntimeException("Error al generar script de inicialización de pandas", e);
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
} 