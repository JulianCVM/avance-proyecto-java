package com.ai.avance.data_mining.visualizers;

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

/**
 * Visualizador de datos para la minería de conversaciones.
 * Esta clase genera visualizaciones a partir de los datos analizados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataVisualizer {

    private final DataMiningConfig.Paths dataMiningPaths;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Genera un script Python para crear un dashboard interactivo
     * @param processedMessagesPath ruta al archivo CSV de mensajes procesados
     * @param processedSessionsPath ruta al archivo CSV de sesiones procesadas
     * @return ruta al script generado
     */
    public String generateDashboardScript(String processedMessagesPath, String processedSessionsPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getResultsPath() + "/dashboard_" + timestamp;
        String scriptPath = dataMiningPaths.getResultsPath() + "/dashboard_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import plotly.express as px\n");
            writer.write("import plotly.graph_objects as go\n");
            writer.write("from plotly.subplots import make_subplots\n");
            writer.write("import dash\n");
            writer.write("from dash import dcc, html\n");
            writer.write("from dash.dependencies import Input, Output\n");
            writer.write("import os\n\n");
            
            writer.write("# Crear directorio para resultados\n");
            writer.write(String.format("output_dir = '%s'\n", outputPath.replace("\\", "\\\\")));
            writer.write("os.makedirs(output_dir, exist_ok=True)\n\n");
            
            writer.write("# Cargar datos procesados\n");
            writer.write(String.format("messages_df = pd.read_csv('%s')\n", processedMessagesPath.replace("\\", "\\\\")));
            writer.write(String.format("sessions_df = pd.read_csv('%s')\n\n", processedSessionsPath.replace("\\", "\\\\")));
            
            writer.write("# Convertir columnas de fecha\n");
            writer.write("messages_df['timestamp'] = pd.to_datetime(messages_df['timestamp'])\n");
            writer.write("sessions_df['created_at'] = pd.to_datetime(sessions_df['created_at'])\n");
            writer.write("sessions_df['last_activity'] = pd.to_datetime(sessions_df['last_activity'])\n\n");
            
            writer.write("# Crear la aplicación Dash\n");
            writer.write("app = dash.Dash(__name__)\n\n");
            
            writer.write("# Definir el layout del dashboard\n");
            writer.write("app.layout = html.Div([\n");
            writer.write("    html.H1('Dashboard de Análisis de Conversaciones', style={'textAlign': 'center'}),\n");
            writer.write("    \n");
            writer.write("    html.Div([\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Métricas Clave'),\n");
            writer.write("            html.Div(id='key-metrics'),\n");
            writer.write("        ], className='container'),\n");
            writer.write("        \n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Filtros'),\n");
            writer.write("            html.Label('Rango de Fechas:'),\n");
            writer.write("            dcc.DatePickerRange(\n");
            writer.write("                id='date-range',\n");
            writer.write("                min_date_allowed=messages_df['timestamp'].min().date(),\n");
            writer.write("                max_date_allowed=messages_df['timestamp'].max().date(),\n");
            writer.write("                start_date=messages_df['timestamp'].min().date(),\n");
            writer.write("                end_date=messages_df['timestamp'].max().date()\n");
            writer.write("            ),\n");
            writer.write("            html.Label('Rol:'),\n");
            writer.write("            dcc.Dropdown(\n");
            writer.write("                id='role-filter',\n");
            writer.write("                options=[{'label': role, 'value': role} for role in messages_df['role'].unique()],\n");
            writer.write("                value=None,\n");
            writer.write("                multi=True\n");
            writer.write("            )\n");
            writer.write("        ], className='container'),\n");
            writer.write("    ], style={'display': 'flex', 'justify-content': 'space-between'}),\n");
            writer.write("    \n");
            writer.write("    html.Div([\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Tendencia de Mensajes en el Tiempo'),\n");
            writer.write("            dcc.Graph(id='time-trend-chart')\n");
            writer.write("        ], className='chart-container'),\n");
            writer.write("        \n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Distribución de Mensajes por Hora'),\n");
            writer.write("            dcc.Graph(id='hour-distribution-chart')\n");
            writer.write("        ], className='chart-container')\n");
            writer.write("    ], style={'display': 'flex', 'flex-wrap': 'wrap'}),\n");
            writer.write("    \n");
            writer.write("    html.Div([\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Longitud de Mensajes por Rol'),\n");
            writer.write("            dcc.Graph(id='message-length-chart')\n");
            writer.write("        ], className='chart-container'),\n");
            writer.write("        \n");
            writer.write("        html.Div([\n");
            writer.write("            html.H3('Duración de Sesiones'),\n");
            writer.write("            dcc.Graph(id='session-duration-chart')\n");
            writer.write("        ], className='chart-container')\n");
            writer.write("    ], style={'display': 'flex', 'flex-wrap': 'wrap'}),\n");
            writer.write("])\n\n");
            
            writer.write("# Definir callbacks para actualizar el dashboard\n");
            writer.write("@app.callback(\n");
            writer.write("    Output('key-metrics', 'children'),\n");
            writer.write("    [Input('date-range', 'start_date'),\n");
            writer.write("     Input('date-range', 'end_date'),\n");
            writer.write("     Input('role-filter', 'value')]\n");
            writer.write(")\n");
            writer.write("def update_key_metrics(start_date, end_date, roles):\n");
            writer.write("    filtered_df = filter_data(messages_df, start_date, end_date, roles)\n");
            writer.write("    \n");
            writer.write("    total_messages = len(filtered_df)\n");
            writer.write("    avg_length = filtered_df['content_length'].mean()\n");
            writer.write("    total_sessions = filtered_df['session_id'].nunique()\n");
            writer.write("    \n");
            writer.write("    return html.Div([\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H4(f'{total_messages}'),\n");
            writer.write("            html.P('Total de Mensajes')\n");
            writer.write("        ], className='metric-card'),\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H4(f'{total_sessions}'),\n");
            writer.write("            html.P('Total de Sesiones')\n");
            writer.write("        ], className='metric-card'),\n");
            writer.write("        html.Div([\n");
            writer.write("            html.H4(f'{avg_length:.1f}'),\n");
            writer.write("            html.P('Longitud Media de Mensajes')\n");
            writer.write("        ], className='metric-card'),\n");
            writer.write("    ], style={'display': 'flex', 'justify-content': 'space-between'})\n\n");
            
            writer.write("@app.callback(\n");
            writer.write("    Output('time-trend-chart', 'figure'),\n");
            writer.write("    [Input('date-range', 'start_date'),\n");
            writer.write("     Input('date-range', 'end_date'),\n");
            writer.write("     Input('role-filter', 'value')]\n");
            writer.write(")\n");
            writer.write("def update_time_trend(start_date, end_date, roles):\n");
            writer.write("    filtered_df = filter_data(messages_df, start_date, end_date, roles)\n");
            writer.write("    filtered_df['date'] = filtered_df['timestamp'].dt.date\n");
            writer.write("    msgs_by_date = filtered_df.groupby('date').size().reset_index(name='count')\n");
            writer.write("    \n");
            writer.write("    fig = px.line(msgs_by_date, x='date', y='count', title='Mensajes por Día')\n");
            writer.write("    return fig\n\n");
            
            writer.write("@app.callback(\n");
            writer.write("    Output('hour-distribution-chart', 'figure'),\n");
            writer.write("    [Input('date-range', 'start_date'),\n");
            writer.write("     Input('date-range', 'end_date'),\n");
            writer.write("     Input('role-filter', 'value')]\n");
            writer.write(")\n");
            writer.write("def update_hour_distribution(start_date, end_date, roles):\n");
            writer.write("    filtered_df = filter_data(messages_df, start_date, end_date, roles)\n");
            writer.write("    msgs_by_hour = filtered_df.groupby('hour_of_day').size().reset_index(name='count')\n");
            writer.write("    \n");
            writer.write("    fig = px.bar(msgs_by_hour, x='hour_of_day', y='count', title='Mensajes por Hora del Día')\n");
            writer.write("    return fig\n\n");
            
            writer.write("@app.callback(\n");
            writer.write("    Output('message-length-chart', 'figure'),\n");
            writer.write("    [Input('date-range', 'start_date'),\n");
            writer.write("     Input('date-range', 'end_date'),\n");
            writer.write("     Input('role-filter', 'value')]\n");
            writer.write(")\n");
            writer.write("def update_message_length(start_date, end_date, roles):\n");
            writer.write("    filtered_df = filter_data(messages_df, start_date, end_date, roles)\n");
            writer.write("    msg_length_by_role = filtered_df.groupby('role')['content_length'].mean().reset_index()\n");
            writer.write("    \n");
            writer.write("    fig = px.bar(msg_length_by_role, x='role', y='content_length', title='Longitud Promedio de Mensajes por Rol')\n");
            writer.write("    return fig\n\n");
            
            writer.write("@app.callback(\n");
            writer.write("    Output('session-duration-chart', 'figure'),\n");
            writer.write("    [Input('date-range', 'start_date'),\n");
            writer.write("     Input('date-range', 'end_date')]\n");
            writer.write(")\n");
            writer.write("def update_session_duration(start_date, end_date):\n");
            writer.write("    start_date = pd.to_datetime(start_date)\n");
            writer.write("    end_date = pd.to_datetime(end_date)\n");
            writer.write("    filtered_df = sessions_df[\n");
            writer.write("        (sessions_df['created_at'] >= start_date) &\n");
            writer.write("        (sessions_df['created_at'] <= end_date)\n");
            writer.write("    ]\n");
            writer.write("    \n");
            writer.write("    fig = px.histogram(filtered_df, x='duration_minutes', nbins=20, title='Distribución de Duración de Sesiones')\n");
            writer.write("    return fig\n\n");
            
            writer.write("# Función auxiliar para filtrar datos\n");
            writer.write("def filter_data(df, start_date, end_date, roles):\n");
            writer.write("    start_date = pd.to_datetime(start_date)\n");
            writer.write("    end_date = pd.to_datetime(end_date)\n");
            writer.write("    filtered_df = df[\n");
            writer.write("        (df['timestamp'] >= start_date) &\n");
            writer.write("        (df['timestamp'] <= end_date)\n");
            writer.write("    ]\n");
            writer.write("    \n");
            writer.write("    if roles and len(roles) > 0:\n");
            writer.write("        filtered_df = filtered_df[filtered_df['role'].isin(roles)]\n");
            writer.write("    \n");
            writer.write("    return filtered_df\n\n");
            
            writer.write("# Ejecutar la aplicación\n");
            writer.write("if __name__ == '__main__':\n");
            writer.write("    print(f'Iniciando dashboard en http://127.0.0.1:8050/')\n");
            writer.write("    app.run_server(debug=True)\n");
            
            log.info("Script de dashboard generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de dashboard", e);
            throw new RuntimeException("Error al generar script de dashboard", e);
        }
    }
    
    /**
     * Genera un script Python para exportar gráficos estáticos
     * @param processedMessagesPath ruta al archivo CSV de mensajes procesados
     * @param processedSessionsPath ruta al archivo CSV de sesiones procesadas
     * @return ruta al script generado
     */
    public String generateStaticVisualizationsScript(String processedMessagesPath, String processedSessionsPath) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String outputPath = dataMiningPaths.getResultsPath() + "/visualizations_" + timestamp;
        String scriptPath = dataMiningPaths.getResultsPath() + "/static_visualizations_" + timestamp + ".py";
        
        try (FileWriter writer = new FileWriter(scriptPath)) {
            writer.write("import pandas as pd\n");
            writer.write("import numpy as np\n");
            writer.write("import matplotlib.pyplot as plt\n");
            writer.write("import seaborn as sns\n");
            writer.write("import os\n\n");
            
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
            
            writer.write("# 1. Gráfico de tendencia temporal\n");
            writer.write("print('Generando gráfico de tendencia temporal...')\n");
            writer.write("messages_df['date'] = messages_df['timestamp'].dt.date\n");
            writer.write("msgs_by_date = messages_df.groupby('date').size().reset_index(name='count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.lineplot(data=msgs_by_date, x='date', y='count', ax=ax)\n");
            writer.write("ax.set_title('Mensajes por Día')\n");
            writer.write("ax.set_xlabel('Fecha')\n");
            writer.write("ax.set_ylabel('Número de Mensajes')\n");
            writer.write("save_fig(fig, 'messages_per_day')\n\n");
            
            writer.write("# 2. Distribución por hora del día\n");
            writer.write("print('Generando distribución por hora del día...')\n");
            writer.write("msgs_by_hour = messages_df.groupby('hour_of_day').size().reset_index(name='count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.barplot(data=msgs_by_hour, x='hour_of_day', y='count', ax=ax)\n");
            writer.write("ax.set_title('Mensajes por Hora del Día')\n");
            writer.write("ax.set_xlabel('Hora')\n");
            writer.write("ax.set_ylabel('Número de Mensajes')\n");
            writer.write("save_fig(fig, 'messages_per_hour')\n\n");
            
            writer.write("# 3. Mapa de calor de actividad\n");
            writer.write("print('Generando mapa de calor de actividad...')\n");
            writer.write("messages_df['day_of_week'] = messages_df['timestamp'].dt.dayofweek\n");
            writer.write("heat_data = messages_df.groupby(['day_of_week', 'hour_of_day']).size().unstack(fill_value=0)\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.heatmap(heat_data, cmap='viridis', ax=ax)\n");
            writer.write("ax.set_title('Mapa de Calor de Actividad por Día y Hora')\n");
            writer.write("ax.set_ylabel('Día de la Semana')\n");
            writer.write("ax.set_xlabel('Hora del Día')\n");
            writer.write("ax.set_yticklabels(['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'])\n");
            writer.write("save_fig(fig, 'activity_heatmap')\n\n");
            
            writer.write("# 4. Longitud de mensajes por rol\n");
            writer.write("print('Generando gráfico de longitud de mensajes por rol...')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.boxplot(data=messages_df, x='role', y='content_length', ax=ax)\n");
            writer.write("ax.set_title('Distribución de Longitud de Mensajes por Rol')\n");
            writer.write("ax.set_xlabel('Rol')\n");
            writer.write("ax.set_ylabel('Longitud (caracteres)')\n");
            writer.write("save_fig(fig, 'message_length_boxplot')\n\n");
            
            writer.write("# 5. Distribución de duración de sesiones\n");
            writer.write("print('Generando distribución de duración de sesiones...')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=sessions_df, x='duration_minutes', bins=20, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Duración de Sesiones')\n");
            writer.write("ax.set_xlabel('Duración (minutos)')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_fig(fig, 'session_duration_histogram')\n\n");
            
            writer.write("# 6. Número de mensajes por sesión\n");
            writer.write("print('Generando distribución de mensajes por sesión...')\n");
            writer.write("session_msg_counts = messages_df.groupby('session_id').size().reset_index(name='message_count')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.histplot(data=session_msg_counts, x='message_count', bins=20, ax=ax)\n");
            writer.write("ax.set_title('Distribución de Mensajes por Sesión')\n");
            writer.write("ax.set_xlabel('Número de Mensajes')\n");
            writer.write("ax.set_ylabel('Frecuencia')\n");
            writer.write("save_fig(fig, 'messages_per_session_histogram')\n\n");
            
            writer.write("# 7. Relación entre longitud de mensaje y tokens\n");
            writer.write("print('Generando gráfico de relación longitud-tokens...')\n");
            writer.write("fig, ax = plt.subplots()\n");
            writer.write("sns.scatterplot(data=messages_df, x='content_length', y='token_count', hue='role', ax=ax)\n");
            writer.write("ax.set_title('Relación entre Longitud de Mensaje y Tokens')\n");
            writer.write("ax.set_xlabel('Longitud (caracteres)')\n");
            writer.write("ax.set_ylabel('Tokens')\n");
            writer.write("save_fig(fig, 'length_tokens_relation')\n\n");
            
            writer.write("print('Visualizaciones estáticas generadas!')\n");
            writer.write("print(f'Archivos guardados en: {output_dir}')\n");
            
            log.info("Script de visualizaciones estáticas generado: {}", scriptPath);
            return scriptPath;
        } catch (IOException e) {
            log.error("Error al generar script de visualizaciones estáticas", e);
            throw new RuntimeException("Error al generar script de visualizaciones estáticas", e);
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