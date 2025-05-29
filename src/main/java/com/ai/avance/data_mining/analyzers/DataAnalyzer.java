package com.ai.avance.data_mining.analyzers;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Analizador de datos para la minería de conversaciones.
 * Esta clase implementa algoritmos para analizar datos de conversaciones
 * y extraer insights valiosos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataAnalyzer {

    private final DataMiningConfig.Paths dataMiningPaths;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private Map<String, Object> analysisResults = new HashMap<>();
    
    /**
     * Genera un script Python para realizar análisis exploratorio de datos
     * @param processedMessagesPath ruta al archivo CSV de mensajes procesados
     * @param processedSessionsPath ruta al archivo CSV de sesiones procesadas
     * @return ruta al script generado
     */
    public String generateExploratoryAnalysisScript(String processedMessagesPath, String processedSessionsPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getResultsPath() + "/eda_results_" + timestamp;
        String scriptPath = dataMiningPaths.getResultsPath() + "/exploratory_analysis_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n");
            writer.write("import os\n");
            writer.write("import json\n\n");
            
            writer.write("# Configurar estilo de visualización\n");
            writer.write("sns.set(style='whitegrid')\n");
            writer.write("plt.rcParams['figure.figsize'] = (12, 8)\n\n");
            
            writer.write("# Crear directorio para resultados\n");
            writer.write(String.format("output_dir = '%s'\n", outputPath.replace("\\", "\\\\")));
            writer.write("os.makedirs(output_dir, exist_ok=True)\n\n");
            
            writer.write("# Función para guardar figuras\n");
            writer.write("def save_fig(fig, name):\n");
            writer.write("    fig.savefig(os.path.join(output_dir, f'{name}.png'), bbox_inches='tight')\n");
            writer.write("    plt.close(fig)\n\n");
            
            writer.write("# Cargar datos procesados\n");
            writer.write(String.format("messages_df = pd.read_csv('%s')\n", processedMessagesPath.replace("\\", "\\\\")));
            writer.write(String.format("sessions_df = pd.read_csv('%s')\n\n", processedSessionsPath.replace("\\", "\\\\")));
            
            writer.write("# Convertir columnas de fecha\n");
            writer.write("messages_df['timestamp'] = pd.to_datetime(messages_df['timestamp'])\n");
            writer.write("sessions_df['created_at'] = pd.to_datetime(sessions_df['created_at'])\n");
            writer.write("sessions_df['last_activity'] = pd.to_datetime(sessions_df['last_activity'])\n\n");
            
            writer.write("# 1. Estadísticas descriptivas básicas\n");
            writer.write("print('Generando estadísticas descriptivas...')\n");
            writer.write("msg_stats = messages_df.describe(include='all').to_dict()\n");
            writer.write("session_stats = sessions_df.describe(include='all').to_dict()\n\n");
            
            writer.write("# 2. Análisis de tendencias temporales\n");
            writer.write("print('Analizando tendencias temporales...')\n");
            writer.write("# Mensajes por día\n");
            writer.write("messages_df['date'] = messages_df['timestamp'].dt.date\n");
            writer.write("msgs_by_date = messages_df.groupby('date').size().reset_index(name='count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.lineplot(data=msgs_by_date, x='date', y='count', ax=ax)\n");
            writer.write("ax.set_title('Mensajes por Día')\n");
            writer.write("ax.set_xlabel('Fecha')\n");
            writer.write("ax.set_ylabel('Número de Mensajes')\n");
            writer.write("save_fig(fig, 'messages_per_day')\n\n");
            
            writer.write("# Mensajes por hora del día\n");
            writer.write("msgs_by_hour = messages_df.groupby('hour_of_day').size().reset_index(name='count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.barplot(data=msgs_by_hour, x='hour_of_day', y='count', ax=ax)\n");
            writer.write("ax.set_title('Mensajes por Hora del Día')\n");
            writer.write("ax.set_xlabel('Hora')\n");
            writer.write("ax.set_ylabel('Número de Mensajes')\n");
            writer.write("save_fig(fig, 'messages_per_hour')\n\n");
            
            writer.write("# 3. Análisis de temas de conversación\n");
            writer.write("print('Analizando patrones de conversación...')\n");
            writer.write("# Longitud promedio de mensajes por rol\n");
            writer.write("msg_length_by_role = messages_df.groupby('role')['content_length'].mean().reset_index()\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.barplot(data=msg_length_by_role, x='role', y='content_length', ax=ax)\n");
            writer.write("ax.set_title('Longitud Promedio de Mensajes por Rol')\n");
            writer.write("ax.set_xlabel('Rol')\n");
            writer.write("ax.set_ylabel('Longitud Promedio (caracteres)')\n");
            writer.write("save_fig(fig, 'message_length_by_role')\n\n");
            
            writer.write("# Distribución de longitud de mensajes\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=messages_df, x='content_length', hue='role', bins=30, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Longitud de Mensajes')\n");
            writer.write("ax.set_xlabel('Longitud (caracteres)')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_fig(fig, 'message_length_distribution')\n\n");
            
            writer.write("# 4. Patrones de interacción\n");
            writer.write("print('Analizando patrones de interacción...')\n");
            writer.write("# Número de mensajes por sesión\n");
            writer.write("session_msg_counts = messages_df.groupby('session_id').size().reset_index(name='message_count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=session_msg_counts, x='message_count', bins=20, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Mensajes por Sesión')\n");
            writer.write("ax.set_xlabel('Número de Mensajes')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_fig(fig, 'messages_per_session')\n\n");
            
            writer.write("# Duración de sesiones\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=sessions_df, x='duration_minutes', bins=20, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Duración de Sesiones')\n");
            writer.write("ax.set_xlabel('Duración (minutos)')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_fig(fig, 'session_duration')\n\n");
            
            writer.write("# 5. Guardar resultados como JSON\n");
            writer.write("print('Guardando resultados...')\n");
            writer.write("results = {\n");
            writer.write("    'message_stats': msg_stats,\n");
            writer.write("    'session_stats': session_stats,\n");
            writer.write("    'messages_by_date': msgs_by_date.to_dict('records'),\n");
            writer.write("    'messages_by_hour': msgs_by_hour.to_dict('records'),\n");
            writer.write("    'message_length_by_role': msg_length_by_role.to_dict('records'),\n");
            writer.write("    'messages_per_session': session_msg_counts.describe().to_dict(),\n");
            writer.write("    'session_duration': sessions_df['duration_minutes'].describe().to_dict()\n");
            writer.write("}\n\n");
            
            writer.write("with open(os.path.join(output_dir, 'analysis_results.json'), 'w') as f:\n");
            writer.write("    json.dump(results, f, indent=2, default=str)\n\n");
            
            writer.write("print('Análisis exploratorio completado!')\n");
            writer.write("print(f'Resultados guardados en: {output_dir}')\n");
            
            log.info("Script de análisis exploratorio generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de análisis exploratorio", e);
            throw new RuntimeException("Error al generar script de análisis exploratorio", e);
        }
    }
    
    /**
     * Genera un script Python para realizar clustering de temas de conversación
     * @param processedMessagesPath ruta al archivo CSV de mensajes procesados
     * @return ruta al script generado
     */
    public String generateTopicClusteringScript(String processedMessagesPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getResultsPath() + "/topic_clusters_" + timestamp;
        String scriptPath = dataMiningPaths.getResultsPath() + "/topic_clustering_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n");
            writer.write("from sklearn.feature_extraction.text import TfidfVectorizer\n");
            writer.write("from sklearn.cluster import KMeans, DBSCAN\n");
            writer.write("from sklearn.decomposition import PCA, TruncatedSVD\n");
            writer.write("from sklearn.manifold import TSNE\n");
            writer.write("import os\n");
            writer.write("import json\n");
            writer.write("from collections import Counter\n\n");
            
            writer.write("# Configurar estilo de visualización\n");
            writer.write("sns.set(style='whitegrid')\n");
            writer.write("plt.rcParams['figure.figsize'] = (12, 8)\n\n");
            
            writer.write("# Crear directorio para resultados\n");
            writer.write(String.format("output_dir = '%s'\n", outputPath.replace("\\", "\\\\")));
            writer.write("os.makedirs(output_dir, exist_ok=True)\n\n");
            
            writer.write("# Cargar datos procesados\n");
            writer.write(String.format("messages_df = pd.read_csv('%s')\n\n", processedMessagesPath.replace("\\", "\\\\")));
            
            writer.write("# Filtrar mensajes de usuario (contienen las preguntas/temas)\n");
            writer.write("user_messages = messages_df[messages_df['role'] == 'usuario'].copy()\n\n");
            
            writer.write("# Vectorizar el contenido de los mensajes\n");
            writer.write("print('Vectorizando contenido de mensajes...')\n");
            writer.write("tfidf = TfidfVectorizer(max_features=1000, stop_words='english')\n");
            writer.write("tfidf_matrix = tfidf.fit_transform(user_messages['content'])\n\n");
            
            writer.write("# Reducir dimensionalidad para visualización\n");
            writer.write("print('Reduciendo dimensionalidad...')\n");
            writer.write("svd = TruncatedSVD(n_components=50)\n");
            writer.write("svd_features = svd.fit_transform(tfidf_matrix)\n");
            writer.write("tsne = TSNE(n_components=2, random_state=42)\n");
            writer.write("tsne_features = tsne.fit_transform(svd_features)\n\n");
            
            writer.write("# Clustering de temas\n");
            writer.write("print('Realizando clustering de temas...')\n");
            writer.write("# Determinar número óptimo de clusters\n");
            writer.write("inertia = []\n");
            writer.write("for k in range(2, 21):\n");
            writer.write("    kmeans = KMeans(n_clusters=k, random_state=42)\n");
            writer.write("    kmeans.fit(svd_features)\n");
            writer.write("    inertia.append(kmeans.inertia_)\n\n");
            
            writer.write("# Graficar el método del codo\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("plt.plot(range(2, 21), inertia, marker='o')\n");
            writer.write("plt.title('Método del Codo para Clustering de Temas')\n");
            writer.write("plt.xlabel('Número de Clusters')\n");
            writer.write("plt.ylabel('Inercia')\n");
            writer.write("plt.savefig(os.path.join(output_dir, 'elbow_method.png'), bbox_inches='tight')\n");
            writer.write("plt.close()\n\n");
            
            writer.write("# Aplicar K-means con el número óptimo de clusters (aquí usamos 8 como ejemplo)\n");
            writer.write("n_clusters = 8\n");
            writer.write("kmeans = KMeans(n_clusters=n_clusters, random_state=42)\n");
            writer.write("user_messages['cluster'] = kmeans.fit_predict(svd_features)\n\n");
            
            writer.write("# Visualizar clusters\n");
            writer.write("user_messages['tsne_x'] = tsne_features[:, 0]\n");
            writer.write("user_messages['tsne_y'] = tsne_features[:, 1]\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.scatterplot(data=user_messages, x='tsne_x', y='tsne_y', hue='cluster', palette='viridis', ax=ax)\n");
            writer.write("plt.title('Clustering de Temas de Conversación')\n");
            writer.write("plt.savefig(os.path.join(output_dir, 'topic_clusters.png'), bbox_inches='tight')\n");
            writer.write("plt.close()\n\n");
            
            writer.write("# Extraer palabras clave por cluster\n");
            writer.write("print('Extrayendo palabras clave por cluster...')\n");
            writer.write("feature_names = tfidf.get_feature_names_out()\n");
            writer.write("cluster_keywords = {}\n\n");
            
            writer.write("for i in range(n_clusters):\n");
            writer.write("    cluster_docs = user_messages[user_messages['cluster'] == i]['content']\n");
            writer.write("    if len(cluster_docs) == 0:\n");
            writer.write("        continue\n");
            writer.write("    cluster_vectorizer = TfidfVectorizer(max_features=10)\n");
            writer.write("    cluster_tfidf = cluster_vectorizer.fit_transform(cluster_docs)\n");
            writer.write("    cluster_words = cluster_vectorizer.get_feature_names_out()\n");
            writer.write("    cluster_keywords[f'Cluster {i}'] = cluster_words.tolist()\n\n");
            
            writer.write("# Guardar resultados\n");
            writer.write("results = {\n");
            writer.write("    'cluster_sizes': user_messages['cluster'].value_counts().to_dict(),\n");
            writer.write("    'cluster_keywords': cluster_keywords,\n");
            writer.write("    'inertia_values': dict(zip(range(2, 21), inertia))\n");
            writer.write("}\n\n");
            
            writer.write("user_messages[['message_id', 'content', 'cluster']].to_csv(os.path.join(output_dir, 'clustered_messages.csv'), index=False)\n");
            writer.write("with open(os.path.join(output_dir, 'cluster_results.json'), 'w') as f:\n");
            writer.write("    json.dump(results, f, indent=2)\n\n");
            
            writer.write("print('Clustering de temas completado!')\n");
            writer.write("print(f'Resultados guardados en: {output_dir}')\n");
            
            log.info("Script de clustering de temas generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de clustering de temas", e);
            throw new RuntimeException("Error al generar script de clustering de temas", e);
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
     * Obtiene los resultados del análisis
     * @return mapa con los resultados
     */
    public Map<String, Object> getAnalysisResults() {
        return new HashMap<>(analysisResults);
    }
} 