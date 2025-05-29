import os
import sys
import json
import random
from datetime import datetime, timedelta

print("Python version:", sys.version)
print("Working directory:", os.getcwd())

# Crear directorios necesarios
raw_dir = './data/mining/raw'
results_dir = './data/mining/results'
os.makedirs(raw_dir, exist_ok=True)
os.makedirs(results_dir, exist_ok=True)
print("Directorios creados")

# Crear datos de prueba manualmente
print("Generando datos de prueba...")

# Crear datos de sesiones
sessions_data = []
for session_id in range(1, 51):
    user_id = random.randint(1, 50)
    agent_id = random.randint(1, 10)
    topics = ['Soporte técnico', 'Información de productos', 'Ventas', 'Devoluciones']
    topic = random.choice(topics)
    title = 'Consulta sobre ' + topic
    created_at = datetime.now() - timedelta(days=random.randint(0, 14))
    duration_minutes = random.randint(5, 60)
    last_activity = created_at + timedelta(minutes=duration_minutes)
    message_count = random.randint(4, 20)
    total_tokens = message_count * random.randint(30, 50)
    
    sessions_data.append({
        'session_id': session_id,
        'user_id': user_id,
        'agent_id': agent_id,
        'title': title,
        'created_at': created_at.strftime('%Y-%m-%d %H:%M:%S'),
        'last_activity': last_activity.strftime('%Y-%m-%d %H:%M:%S'),
        'message_count': message_count,
        'total_tokens': total_tokens,
        'duration_minutes': duration_minutes,
        'topic': topic
    })

# Crear datos de mensajes
messages_data = []
message_id = 1
for session in sessions_data:
    session_id = session['session_id']
    user_id = session['user_id']
    agent_id = session['agent_id']
    topic = session['topic']
    num_messages = session['message_count']
    start_time = datetime.strptime(session['created_at'], '%Y-%m-%d %H:%M:%S')
    
    for i in range(num_messages):
        role = 'usuario' if i % 2 == 0 else 'asistente'
        content_length = random.randint(10, 100)
        content = 'Mensaje de ' + role + ' sobre ' + topic + ' ' + ' '.join(['palabra'] * content_length)
        timestamp = start_time + timedelta(minutes=i*2)
        token_count = len(content.split()) * 4 + random.randint(0, 20)
        flagged = random.random() < 0.05
        model_used = random.choice(['gpt-3.5-turbo', 'gpt-4', '']) if role == 'asistente' else ''
        
        messages_data.append({
            'message_id': message_id,
            'session_id': session_id,
            'agent_id': agent_id,
            'user_id': user_id,
            'role': role,
            'content': content,
            'timestamp': timestamp.strftime('%Y-%m-%d %H:%M:%S'),
            'token_count': token_count,
            'flagged': flagged,
            'model_used': model_used,
            'topic': topic
        })
        message_id += 1

# Guardar datos generados en formato CSV manual
def save_as_csv(data, file_path):
    with open(file_path, 'w', encoding='utf-8') as f:
        # Escribir encabezados
        if data:
            headers = ','.join([f'"{k}"' for k in data[0].keys()]) + '\n'
            f.write(headers)
            
            # Escribir datos
            for row in data:
                values = []
                for v in row.values():
                    if isinstance(v, str):
                        v = f'"{v.replace('"', '""')}"'
                    elif isinstance(v, bool):
                        v = str(v).lower()
                    else:
                        v = str(v)
                    values.append(v)
                f.write(','.join(values) + '\n')
    return file_path

output_sessions = os.path.join(raw_dir, 'pandas_generated_sessions.csv')
output_messages = os.path.join(raw_dir, 'pandas_generated_messages.csv')

save_as_csv(sessions_data, output_sessions)
save_as_csv(messages_data, output_messages)

print("Datos generados y guardados en:")
print(output_sessions)
print(output_messages)

# Crear un archivo de resumen para la interfaz web
results_summary = {
    'shape': [len(messages_data), len(messages_data[0]) if messages_data else 0],
    'columns': list(messages_data[0].keys()) if messages_data else [],
    'sample_rows': messages_data[:5]
}

# Guardar resumen
result_path = os.path.join(results_dir, 'analysis_summary.json')
with open(result_path, 'w') as f:
    json.dump(results_summary, f, default=str)

print("Resumen guardado en:", result_path)

# Crear un archivo de texto que simule una visualización
def create_text_visualization(name, description):
    vis_path = os.path.join(results_dir, name + '.txt')
    with open(vis_path, 'w') as f:
        f.write(description)
    return vis_path

# Crear archivos de "visualización" en texto
create_text_visualization('role_distribution', 'Distribución de Mensajes por Rol')
create_text_visualization('topic_distribution', 'Distribución de Mensajes por Tema')
create_text_visualization('session_duration', 'Distribución de Duración de Sesiones')
create_text_visualization('messages_per_session', 'Distribución de Mensajes por Sesión')
create_text_visualization('sessions_by_topic', 'Distribución de Sesiones por Tema')
create_text_visualization('duration_vs_messages', 'Relación entre Duración y Número de Mensajes')

print("Archivos de visualización creados en:", results_dir)
print("Script completado exitosamente") 