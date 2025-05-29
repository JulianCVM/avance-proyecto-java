package com.ai.avance.data_mining.testing;

import com.ai.avance.config.DataMiningConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generador de datos de prueba para el sistema de minería de datos.
 * Esta clase genera datos sintéticos que simulan conversaciones de chatbot
 * para demostrar las capacidades de análisis de Pandas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataGenerator {

    private final DataMiningConfig.Paths dataMiningPaths;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Random RANDOM = new Random();
    
    // Temas posibles para las conversaciones
    private static final List<String> TOPICS = Arrays.asList(
        "Soporte técnico", "Información de productos", "Ventas", "Devoluciones", 
        "Dudas generales", "Quejas", "Sugerencias", "Seguimiento de pedidos"
    );
    
    // Palabras clave por tema para generar contenido realista
    private static final List<List<String>> TOPIC_KEYWORDS = Arrays.asList(
        Arrays.asList("error", "problema", "no funciona", "actualizar", "instalar", "ayuda", "reiniciar", "contraseña"),
        Arrays.asList("características", "especificaciones", "comparar", "diferencias", "versión", "modelos", "disponibilidad"),
        Arrays.asList("precio", "descuento", "comprar", "pagar", "oferta", "tarjeta", "factura", "promoción"),
        Arrays.asList("devolver", "reembolso", "insatisfecho", "cambiar", "garantía", "defectuoso", "dañado"),
        Arrays.asList("cómo", "dónde", "cuándo", "quién", "información", "explicar", "detalles"),
        Arrays.asList("insatisfecho", "molesto", "decepcionado", "mal", "servicio", "atención", "espera"),
        Arrays.asList("sugerir", "mejorar", "idea", "propuesta", "añadir", "función", "característica"),
        Arrays.asList("pedido", "envío", "entrega", "estado", "seguimiento", "número", "cuándo", "recibir")
    );
    
    /**
     * Genera un dataset sintético de mensajes de conversación
     * @param numSessions número de sesiones de chat a generar
     * @param messagesPerSession rango de mensajes por sesión (min-max)
     * @return ruta al archivo CSV generado
     */
    public String generateMessageData(int numSessions, int[] messagesPerSession) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/generated_messages_" + timestamp + ".csv";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Escribir encabezados
            writer.write("message_id,session_id,agent_id,user_id,role,content,timestamp,token_count,flagged,model_used,topic\n");
            
            int messageId = 1;
            
            // Generar datos para cada sesión
            for (int sessionId = 1; sessionId <= numSessions; sessionId++) {
                // Seleccionar tema aleatorio para la sesión
                int topicIndex = RANDOM.nextInt(TOPICS.size());
                String topic = TOPICS.get(topicIndex);
                List<String> keywords = TOPIC_KEYWORDS.get(topicIndex);
                
                // Determinar número de mensajes para esta sesión
                int numMessages = RANDOM.nextInt(messagesPerSession[1] - messagesPerSession[0] + 1) + messagesPerSession[0];
                
                // ID de usuario y agente para esta sesión
                int userId = RANDOM.nextInt(50) + 1;
                int agentId = RANDOM.nextInt(10) + 1;
                
                // Hora base para la sesión (entre las últimas 2 semanas)
                LocalDateTime sessionStartTime = LocalDateTime.now().minusDays(RANDOM.nextInt(14)).minusHours(RANDOM.nextInt(24));
                
                // Generar mensajes alternando entre usuario y asistente
                for (int i = 0; i < numMessages; i++) {
                    String role = (i % 2 == 0) ? "usuario" : "asistente";
                    
                    // Generar contenido del mensaje basado en el tema
                    String content = generateMessageContent(role, keywords, topic);
                    
                    // Calcular timestamp del mensaje (incrementando minutos)
                    LocalDateTime messageTime = sessionStartTime.plusMinutes(i * 2 + RANDOM.nextInt(3));
                    
                    // Calcular tokens aproximados (4 tokens por palabra + margen aleatorio)
                    int words = content.split("\\s+").length;
                    int tokens = words * 4 + RANDOM.nextInt(20);
                    
                    // Determinar si el mensaje está marcado (bajo probabilidad)
                    boolean flagged = RANDOM.nextDouble() < 0.05;
                    String flagReason = flagged ? "contenido_inapropiado" : "";
                    
                    // Modelo usado (solo para mensajes del asistente)
                    String modelUsed = (role.equals("asistente")) ? "gpt-3.5-turbo" : "";
                    
                    // Escribir el mensaje
                    writer.write(String.format("%d,%d,%d,%d,\"%s\",\"%s\",%s,%d,%b,\"%s\",\"%s\"\n",
                            messageId++,
                            sessionId,
                            agentId,
                            userId,
                            role,
                            content.replace("\"", "\"\""), // Escapar comillas
                            messageTime,
                            tokens,
                            flagged,
                            modelUsed,
                            topic
                    ));
                }
            }
            
            log.info("Datos de mensajes sintéticos generados en: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al generar datos sintéticos de mensajes", e);
            throw new RuntimeException("Error al generar datos sintéticos", e);
        }
    }
    
    /**
     * Genera un dataset sintético de sesiones de chat
     * @param numSessions número de sesiones a generar
     * @return ruta al archivo CSV generado
     */
    public String generateSessionData(int numSessions) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String filePath = dataMiningPaths.getRawDataPath() + "/generated_sessions_" + timestamp + ".csv";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Escribir encabezados
            writer.write("session_id,user_id,agent_id,title,created_at,last_activity,message_count,total_tokens,duration_minutes,topic\n");
            
            // Generar datos para cada sesión
            for (int sessionId = 1; sessionId <= numSessions; sessionId++) {
                // Seleccionar tema aleatorio para la sesión
                String topic = TOPICS.get(RANDOM.nextInt(TOPICS.size()));
                
                // ID de usuario y agente para esta sesión
                int userId = RANDOM.nextInt(50) + 1;
                int agentId = RANDOM.nextInt(10) + 1;
                
                // Título de la sesión basado en el tema
                String title = "Consulta sobre " + topic;
                
                // Tiempos de la sesión
                LocalDateTime createdAt = LocalDateTime.now().minusDays(RANDOM.nextInt(14)).minusHours(RANDOM.nextInt(24));
                
                // Duración de la sesión en minutos (entre 5 y 60 minutos)
                int durationMinutes = RANDOM.nextInt(56) + 5;
                LocalDateTime lastActivity = createdAt.plusMinutes(durationMinutes);
                
                // Número de mensajes en la sesión (entre 4 y 20)
                int messageCount = RANDOM.nextInt(17) + 4;
                
                // Tokens totales (aproximadamente 30-50 tokens por mensaje)
                int totalTokens = messageCount * (RANDOM.nextInt(21) + 30);
                
                // Escribir la sesión
                writer.write(String.format("%d,%d,%d,\"%s\",%s,%s,%d,%d,%d,\"%s\"\n",
                        sessionId,
                        userId,
                        agentId,
                        title.replace("\"", "\"\""), // Escapar comillas
                        createdAt,
                        lastActivity,
                        messageCount,
                        totalTokens,
                        durationMinutes,
                        topic
                ));
            }
            
            log.info("Datos de sesiones sintéticas generadas en: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Error al generar datos sintéticos de sesiones", e);
            throw new RuntimeException("Error al generar datos sintéticos", e);
        }
    }
    
    /**
     * Genera un script Python para generar datos sintéticos directamente con Pandas
     * @param numSessions número de sesiones a generar
     * @return ruta al script generado
     */
    public String generatePandasDataGenerationScript(int numSessions) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputDir = dataMiningPaths.getRawDataPath();
        String scriptPath = dataMiningPaths.getRawDataPath() + "/pandas_data_generator_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("from datetime import datetime, timedelta\n");
            writer.write("import random\n");
            writer.write("import os\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n");
            writer.write("import json\n\n");
            
            writer.write("# Configuración\n");
            writer.write(String.format("num_sessions = %d\n", numSessions));
            writer.write(String.format("output_dir = '%s'\n", outputDir.replace("\\", "\\\\")));
            writer.write("os.makedirs(output_dir, exist_ok=True)\n\n");
            
            writer.write("# Semilla para reproducibilidad\n");
            writer.write("random.seed(42)\n");
            writer.write("np.random.seed(42)\n\n");
            
            writer.write("# Listas de posibles valores\n");
            writer.write("topics = ['Soporte técnico', 'Información de productos', 'Ventas', 'Devoluciones', 'Dudas generales', 'Quejas', 'Sugerencias', 'Seguimiento de pedidos']\n");
            writer.write("user_ids = list(range(1, 51))\n");
            writer.write("agent_ids = list(range(1, 11))\n");
            writer.write("roles = ['usuario', 'asistente']\n");
            writer.write("models = ['gpt-3.5-turbo', 'gpt-4', '']\n\n");
            
            writer.write("# Generar sesiones\n");
            writer.write("print('Generando datos de sesiones...')\n");
            writer.write("sessions_data = []\n");
            writer.write("for session_id in range(1, num_sessions + 1):\n");
            writer.write("    topic = random.choice(topics)\n");
            writer.write("    user_id = random.choice(user_ids)\n");
            writer.write("    agent_id = random.choice(agent_ids)\n");
            writer.write("    title = 'Consulta sobre ' + topic\n");
            writer.write("    created_at = datetime.now() - timedelta(days=random.randint(0, 14), hours=random.randint(0, 24))\n");
            writer.write("    duration_minutes = random.randint(5, 60)\n");
            writer.write("    last_activity = created_at + timedelta(minutes=duration_minutes)\n");
            writer.write("    message_count = random.randint(4, 20)\n");
            writer.write("    total_tokens = message_count * random.randint(30, 50)\n");
            writer.write("    sessions_data.append({\n");
            writer.write("        'session_id': session_id,\n");
            writer.write("        'user_id': user_id,\n");
            writer.write("        'agent_id': agent_id,\n");
            writer.write("        'title': title,\n");
            writer.write("        'created_at': created_at,\n");
            writer.write("        'last_activity': last_activity,\n");
            writer.write("        'message_count': message_count,\n");
            writer.write("        'total_tokens': total_tokens,\n");
            writer.write("        'duration_minutes': duration_minutes,\n");
            writer.write("        'topic': topic\n");
            writer.write("    })\n\n");
            
            writer.write("# Crear DataFrame de sesiones\n");
            writer.write("sessions_df = pd.DataFrame(sessions_data)\n\n");
            
            writer.write("# Generar mensajes\n");
            writer.write("print('Generando datos de mensajes...')\n");
            writer.write("messages_data = []\n");
            writer.write("message_id = 1\n");
            writer.write("for _, session in sessions_df.iterrows():\n");
            writer.write("    session_id = session['session_id']\n");
            writer.write("    user_id = session['user_id']\n");
            writer.write("    agent_id = session['agent_id']\n");
            writer.write("    topic = session['topic']\n");
            writer.write("    num_messages = session['message_count']\n");
            writer.write("    start_time = session['created_at']\n");
            
            writer.write("    # Generar mensajes para esta sesión\n");
            writer.write("    for i in range(num_messages):\n");
            writer.write("        role = roles[i % 2]  # Alternar entre usuario y asistente\n");
            writer.write("        content_length = random.randint(10, 100)\n");
            writer.write("        content = 'Mensaje de ' + role + ' sobre ' + topic + ' ' + ' '.join(['palabra'] * content_length)\n");
            writer.write("        timestamp = start_time + timedelta(minutes=i*2)\n");
            writer.write("        token_count = len(content.split()) * 4 + random.randint(0, 20)\n");
            writer.write("        flagged = random.random() < 0.05\n");
            writer.write("        model_used = random.choice(models) if role == 'asistente' else ''\n");
            
            writer.write("        messages_data.append({\n");
            writer.write("            'message_id': message_id,\n");
            writer.write("            'session_id': session_id,\n");
            writer.write("            'agent_id': agent_id,\n");
            writer.write("            'user_id': user_id,\n");
            writer.write("            'role': role,\n");
            writer.write("            'content': content,\n");
            writer.write("            'timestamp': timestamp,\n");
            writer.write("            'token_count': token_count,\n");
            writer.write("            'flagged': flagged,\n");
            writer.write("            'model_used': model_used,\n");
            writer.write("            'topic': topic\n");
            writer.write("        })\n");
            writer.write("        message_id += 1\n\n");
            
            writer.write("# Crear DataFrame de mensajes\n");
            writer.write("messages_df = pd.DataFrame(messages_data)\n\n");
            
            writer.write("# Guardar datos generados\n");
            writer.write("output_sessions = os.path.join(output_dir, 'pandas_generated_sessions.csv')\n");
            writer.write("output_messages = os.path.join(output_dir, 'pandas_generated_messages.csv')\n");
            writer.write("sessions_df.to_csv(output_sessions, index=False)\n");
            writer.write("messages_df.to_csv(output_messages, index=False)\n\n");
            
            writer.write("print('Generados ' + str(len(sessions_df)) + ' sesiones y ' + str(len(messages_df)) + ' mensajes')\n");
            writer.write("print('Archivos guardados en:\\n' + output_sessions + '\\n' + output_messages)\n\n");
            
            writer.write("# Análisis exploratorio básico\n");
            writer.write("print('\\nAnálisis Exploratorio Básico:')\n");
            writer.write("print('\\nDistribución de temas:')\n");
            writer.write("topic_counts = messages_df['topic'].value_counts()\n");
            writer.write("print(topic_counts)\n\n");
            
            writer.write("print('\\nMensajes por rol:')\n");
            writer.write("role_counts = messages_df['role'].value_counts()\n");
            writer.write("print(role_counts)\n\n");
            
            writer.write("print('\\nEstadísticas de tokens:')\n");
            writer.write("token_stats = messages_df['token_count'].describe()\n");
            writer.write("print(token_stats)\n\n");
            
            writer.write("print('\\nEstadísticas de duración de sesiones (minutos):')\n");
            writer.write("duration_stats = sessions_df['duration_minutes'].describe()\n");
            writer.write("print(duration_stats)\n\n");
            
            // Agregar código para guardar visualizaciones automáticamente
            writer.write("# Generar visualizaciones automáticas\n");
            writer.write("print('\\nGenerando visualizaciones...')\n");
            writer.write("results_dir = os.path.join(os.path.dirname(output_dir), 'results')\n");
            writer.write("os.makedirs(results_dir, exist_ok=True)\n\n");
            
            writer.write("# Función para guardar figuras\n");
            writer.write("def save_viz(fig, name):\n");
            writer.write("    fig.savefig(os.path.join(results_dir, name + '.png'), bbox_inches='tight')\n");
            writer.write("    plt.close(fig)\n\n");
            
            writer.write("# 1. Distribución de mensajes por rol\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.countplot(data=messages_df, x='role', ax=ax)\n");
            writer.write("ax.set_title('Distribución de Mensajes por Rol')\n");
            writer.write("save_viz(fig, 'role_distribution')\n\n");
            
            writer.write("# 2. Distribución de mensajes por tema\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("topic_counts = messages_df['topic'].value_counts()\n");
            writer.write("topic_counts.plot(kind='bar', ax=ax)\n");
            writer.write("ax.set_title('Distribución de Mensajes por Tema')\n");
            writer.write("ax.set_ylabel('Número de Mensajes')\n");
            writer.write("ax.set_xlabel('Tema')\n");
            writer.write("plt.xticks(rotation=45, ha='right')\n");
            writer.write("save_viz(fig, 'topic_distribution')\n\n");
            
            writer.write("# 3. Distribución de duración de sesiones\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=sessions_df, x='duration_minutes', bins=20, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Duración de Sesiones')\n");
            writer.write("ax.set_xlabel('Duración (minutos)')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_viz(fig, 'session_duration')\n\n");
            
            writer.write("# 4. Distribución de mensajes por sesión\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=sessions_df, x='message_count', bins=15, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Mensajes por Sesión')\n");
            writer.write("ax.set_xlabel('Número de Mensajes')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_viz(fig, 'messages_per_session')\n\n");
            
            writer.write("# 5. Distribución de sesiones por tema\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("topic_counts = sessions_df['topic'].value_counts()\n");
            writer.write("topic_counts.plot(kind='bar', ax=ax)\n");
            writer.write("ax.set_title('Distribución de Sesiones por Tema')\n");
            writer.write("ax.set_ylabel('Número de Sesiones')\n");
            writer.write("ax.set_xlabel('Tema')\n");
            writer.write("plt.xticks(rotation=45, ha='right')\n");
            writer.write("save_viz(fig, 'sessions_by_topic')\n\n");
            
            writer.write("# 6. Relación entre duración y número de mensajes\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.scatterplot(data=sessions_df, x='message_count', y='duration_minutes', ax=ax)\n");
            writer.write("ax.set_title('Relación entre Duración y Número de Mensajes')\n");
            writer.write("ax.set_xlabel('Número de Mensajes')\n");
            writer.write("ax.set_ylabel('Duración (minutos)')\n");
            writer.write("save_viz(fig, 'duration_vs_messages')\n\n");
            
            writer.write("# Generar resumen JSON para la interfaz web\n");
            writer.write("results_summary = {\n");
            writer.write("    'shape_messages': messages_df.shape,\n");
            writer.write("    'shape_sessions': sessions_df.shape,\n");
            writer.write("    'columns_messages': messages_df.columns.tolist(),\n");
            writer.write("    'columns_sessions': sessions_df.columns.tolist(),\n");
            writer.write("    'summary_messages': messages_df.describe().to_dict(),\n");
            writer.write("    'summary_sessions': sessions_df.describe().to_dict(),\n");
            writer.write("    'null_counts_messages': messages_df.isnull().sum().to_dict(),\n");
            writer.write("    'null_counts_sessions': sessions_df.isnull().sum().to_dict(),\n");
            writer.write("    'sample_messages': messages_df.head(5).to_dict('records'),\n");
            writer.write("    'sample_sessions': sessions_df.head(5).to_dict('records')\n");
            writer.write("}\n\n");
            
            writer.write("# Guardar resumen\n");
            writer.write("result_path = os.path.join(results_dir, 'analysis_summary.json')\n");
            writer.write("with open(result_path, 'w') as f:\n");
            writer.write("    json.dump(results_summary, f, default=str)\n\n");
            
            writer.write("print('\\nResumen guardado en: ' + result_path)\n");
            writer.write("print('Visualizaciones guardadas en: ' + results_dir)\n");
            
            log.info("Script de generación de datos con Pandas generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de generación de datos con Pandas", e);
            throw new RuntimeException("Error al generar script de generación de datos", e);
        }
    }
    
    /**
     * Genera contenido de mensaje realista basado en el tema y rol
     * @param role rol del mensaje (usuario o asistente)
     * @param keywords palabras clave relacionadas con el tema
     * @param topic tema de la conversación
     * @return contenido generado para el mensaje
     */
    private String generateMessageContent(String role, List<String> keywords, String topic) {
        StringBuilder content = new StringBuilder();
        
        if (role.equals("usuario")) {
            // Generar pregunta o comentario del usuario
            String[] starters = {
                "Hola, tengo una duda sobre ",
                "Necesito ayuda con ",
                "¿Podrían ayudarme con ",
                "Tengo un problema con ",
                "¿Me pueden dar información sobre ",
                "Quisiera saber más detalles de "
            };
            
            content.append(starters[RANDOM.nextInt(starters.length)]);
            content.append(topic.toLowerCase()).append(". ");
            
            // Añadir detalles usando palabras clave
            int numKeywords = RANDOM.nextInt(3) + 1;
            for (int i = 0; i < numKeywords; i++) {
                String keyword = keywords.get(RANDOM.nextInt(keywords.size()));
                
                String[] details = {
                    "Específicamente sobre " + keyword + ". ",
                    "Me interesa saber más sobre " + keyword + ". ",
                    "¿Cómo funciona lo de " + keyword + "? ",
                    "¿Qué opciones hay para " + keyword + "? "
                };
                
                content.append(details[RANDOM.nextInt(details.length)]);
            }
            
        } else {
            // Generar respuesta del asistente
            String[] starters = {
                "¡Hola! Gracias por contactarnos. ",
                "Claro, puedo ayudarte con eso. ",
                "Entiendo tu consulta sobre " + topic.toLowerCase() + ". ",
                "Con gusto te asisto con tu pregunta. ",
                "Gracias por tu mensaje. "
            };
            
            content.append(starters[RANDOM.nextInt(starters.length)]);
            
            // Generar respuesta usando palabras clave
            int numKeywords = RANDOM.nextInt(4) + 2;
            for (int i = 0; i < numKeywords; i++) {
                String keyword = keywords.get(RANDOM.nextInt(keywords.size()));
                
                String[] responses = {
                    "Respecto a " + keyword + ", te comento que es importante considerar... ",
                    "Para " + keyword + ", te recomendamos seguir estos pasos... ",
                    "En cuanto a " + keyword + ", nuestra política es... ",
                    "Sobre " + keyword + ", debes tener en cuenta que... "
                };
                
                content.append(responses[RANDOM.nextInt(responses.length)]);
            }
            
            // Añadir cierre
            String[] closings = {
                "¿Hay algo más en lo que pueda ayudarte?",
                "¿Necesitas información adicional?",
                "Espero haber resuelto tu duda.",
                "Quedo atento a tus comentarios."
            };
            
            content.append(closings[RANDOM.nextInt(closings.length)]);
        }
        
        return content.toString();
    }
    
    /**
     * Crea directorios necesarios para los datos generados
     */
    public void ensureDirectoriesExist() {
        String rawPath = dataMiningPaths.getRawDataPath();
        File directory = new File(rawPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("No se pudo crear el directorio: {}", rawPath);
                throw new RuntimeException("Error al crear directorio para datos generados");
            }
        }
    }
} 