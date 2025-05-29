import os
import json
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from datetime import datetime, timedelta

print("Script completo de prueba de Pandas")

# Crear directorios necesarios
raw_dir = './data/mining/raw'
results_dir = './data/mining/results'
os.makedirs(raw_dir, exist_ok=True)
os.makedirs(results_dir, exist_ok=True)
print("Directorios creados")

# Crear datos de prueba
print("Generando datos de prueba...")

# Crear datos de sesiones
sessions_data = []
for session_id in range(1, 51):
    user_id = np.random.randint(1, 51)
    agent_id = np.random.randint(1, 11)
    topic = np.random.choice(['Soporte técnico', 'Información de productos', 'Ventas', 'Devoluciones'])
    title = 'Consulta sobre ' + topic
    created_at = datetime.now() - timedelta(days=np.random.randint(0, 14))
    duration_minutes = np.random.randint(5, 60)
    last_activity = created_at + timedelta(minutes=duration_minutes)
    message_count = np.random.randint(4, 20)
    total_tokens = message_count * np.random.randint(30, 50)
    
    sessions_data.append({
        'session_id': session_id,
        'user_id': user_id,
        'agent_id': agent_id,
        'title': title,
        'created_at': created_at,
        'last_activity': last_activity,
        'message_count': message_count,
        'total_tokens': total_tokens,
        'duration_minutes': duration_minutes,
        'topic': topic
    })

# Crear DataFrame de sesiones
sessions_df = pd.DataFrame(sessions_data)

# Crear datos de mensajes
messages_data = []
message_id = 1
for _, session in sessions_df.iterrows():
    session_id = session['session_id']
    user_id = session['user_id']
    agent_id = session['agent_id']
    topic = session['topic']
    num_messages = session['message_count']
    start_time = session['created_at']
    
    for i in range(num_messages):
        role = 'usuario' if i % 2 == 0 else 'asistente'
        content_length = np.random.randint(10, 100)
        content = 'Mensaje de ' + role + ' sobre ' + topic + ' ' + ' '.join(['palabra'] * content_length)
        timestamp = start_time + timedelta(minutes=i*2)
        token_count = len(content.split()) * 4 + np.random.randint(0, 20)
        flagged = np.random.random() < 0.05
        model_used = np.random.choice(['gpt-3.5-turbo', 'gpt-4', '']) if role == 'asistente' else ''
        
        messages_data.append({
            'message_id': message_id,
            'session_id': session_id,
            'agent_id': agent_id,
            'user_id': user_id,
            'role': role,
            'content': content,
            'timestamp': timestamp,
            'token_count': token_count,
            'flagged': flagged,
            'model_used': model_used,
            'topic': topic
        })
        message_id += 1

# Crear DataFrame de mensajes
messages_df = pd.DataFrame(messages_data)

# Guardar datos generados
output_sessions = os.path.join(raw_dir, 'pandas_generated_sessions.csv')
output_messages = os.path.join(raw_dir, 'pandas_generated_messages.csv')
sessions_df.to_csv(output_sessions, index=False)
messages_df.to_csv(output_messages, index=False)

print("Datos generados y guardados en:")
print(output_sessions)
print(output_messages)

# Generar visualizaciones
print("Generando visualizaciones...")

# Configurar estilo
sns.set(style='whitegrid')
plt.rcParams['figure.figsize'] = (10, 6)

# Función para guardar figuras
def save_viz(fig, name):
    output_path = os.path.join(results_dir, name + '.png')
    fig.savefig(output_path, bbox_inches='tight')
    plt.close(fig)
    return output_path

# 1. Distribución de mensajes por rol
fig, ax = plt.subplots()
sns.countplot(data=messages_df, x='role', ax=ax)
ax.set_title('Distribución de Mensajes por Rol')
role_dist_path = save_viz(fig, 'role_distribution')

# 2. Distribución de mensajes por tema
fig, ax = plt.subplots()
topic_counts = messages_df['topic'].value_counts()
topic_counts.plot(kind='bar', ax=ax)
ax.set_title('Distribución de Mensajes por Tema')
ax.set_ylabel('Número de Mensajes')
ax.set_xlabel('Tema')
plt.xticks(rotation=45, ha='right')
topic_dist_path = save_viz(fig, 'topic_distribution')

# 3. Distribución de duración de sesiones
fig, ax = plt.subplots()
sns.histplot(data=sessions_df, x='duration_minutes', bins=20, ax=ax)
ax.set_title('Distribución de Duración de Sesiones')
ax.set_xlabel('Duración (minutos)')
ax.set_ylabel('Frecuencia')
duration_path = save_viz(fig, 'session_duration')

# 4. Distribución de mensajes por sesión
fig, ax = plt.subplots()
sns.histplot(data=sessions_df, x='message_count', bins=15, ax=ax)
ax.set_title('Distribución de Mensajes por Sesión')
ax.set_xlabel('Número de Mensajes')
ax.set_ylabel('Frecuencia')
msg_count_path = save_viz(fig, 'messages_per_session')

# 5. Distribución de sesiones por tema
fig, ax = plt.subplots()
topic_counts = sessions_df['topic'].value_counts()
topic_counts.plot(kind='bar', ax=ax)
ax.set_title('Distribución de Sesiones por Tema')
ax.set_ylabel('Número de Sesiones')
ax.set_xlabel('Tema')
plt.xticks(rotation=45, ha='right')
sessions_topic_path = save_viz(fig, 'sessions_by_topic')

# 6. Relación entre duración y número de mensajes
fig, ax = plt.subplots()
sns.scatterplot(data=sessions_df, x='message_count', y='duration_minutes', ax=ax)
ax.set_title('Relación entre Duración y Número de Mensajes')
ax.set_xlabel('Número de Mensajes')
ax.set_ylabel('Duración (minutos)')
duration_msgs_path = save_viz(fig, 'duration_vs_messages')

print("Visualizaciones generadas y guardadas en:", results_dir)

# Generar resumen JSON para la interfaz web
results_summary = {
    'shape': messages_df.shape,
    'columns': messages_df.columns.tolist(),
    'summary': messages_df.describe().to_dict(),
    'null_counts': messages_df.isnull().sum().to_dict(),
    'sample_rows': messages_df.head(5).to_dict('records')
}

# Guardar resumen
result_path = os.path.join(results_dir, 'analysis_summary.json')
with open(result_path, 'w') as f:
    json.dump(results_summary, f, default=str)

print("Resumen guardado en:", result_path)
print("Script completado exitosamente") 