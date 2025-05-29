import os
import sys

print("Python version:", sys.version)
print("Working directory:", os.getcwd())

# Intentar importar módulos
modules = ["pandas", "numpy", "matplotlib", "seaborn", "json"]
for module in modules:
    try:
        __import__(module)
        print(f"✓ Módulo {module} importado correctamente")
    except ImportError as e:
        print(f"✗ Error al importar {module}: {e}")

# Crear directorios
try:
    os.makedirs('./data/mining/results', exist_ok=True)
    print("✓ Directorio results creado correctamente")
except Exception as e:
    print(f"✗ Error al crear directorio: {e}")

# Intentar crear un archivo simple
try:
    with open('./data/mining/results/test.json', 'w') as f:
        f.write('{"test": true}')
    print("✓ Archivo JSON creado correctamente")
except Exception as e:
    print(f"✗ Error al crear archivo: {e}")

print("Script finalizado") 