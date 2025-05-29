import os
import json

# Imprimir información del script
print("Script de prueba de Pandas")

# Crear directorios necesarios
results_dir = './data/mining/results'
os.makedirs(results_dir, exist_ok=True)
print("Directorio de resultados:", results_dir)

# Crear un archivo JSON simple
test_data = {
    'test': True,
    'message': 'Prueba de generación de archivos correcta',
    'timestamp': '2025-05-28'
}

# Guardar el archivo JSON
result_path = os.path.join(results_dir, 'analysis_summary.json')
with open(result_path, 'w') as f:
    json.dump(test_data, f)

print("Archivo de prueba creado en:", result_path)
print("Contenido:", test_data)

# Intentar crear un archivo de visualización simple (solo texto)
vis_path = os.path.join(results_dir, 'test_visualization.txt')
with open(vis_path, 'w') as f:
    f.write("Esta es una visualización de prueba")

print("Visualización de prueba creada en:", vis_path) 