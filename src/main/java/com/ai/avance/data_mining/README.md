# Sistema de Minería de Datos para Conversaciones

Este módulo implementa un sistema completo de minería de datos para analizar conversaciones entre usuarios y chatbots. Utiliza Pandas y herramientas de ciencia de datos de Python integradas con Spring Boot para proporcionar análisis avanzados.

## Arquitectura

El sistema se compone de cuatro componentes principales:

1. **Extractores de Datos**: Extraen datos de la base de datos y los convierten a formatos adecuados para su procesamiento.
2. **Procesadores de Datos**: Limpian, transforman y preparan los datos para el análisis.
3. **Analizadores de Datos**: Implementan algoritmos para descubrir patrones y obtener insights.
4. **Visualizadores de Datos**: Generan visualizaciones y dashboards interactivos.

## Configuración

La configuración del sistema se encuentra en `application.properties`:

```properties
# Configuración para el sistema de minería de datos
app.data-mining.base-dir=./data/mining
app.data-mining.pandas.max-columns=100
app.data-mining.pandas.col-width=50
app.data-mining.pandas.chunk-size=10000
app.data-mining.pandas.max-memory=1GB
```

## Requisitos

Para ejecutar el sistema se requiere:

1. Java 21 o superior
2. Python 3.8 o superior
3. Pandas, NumPy, Matplotlib, Seaborn, Scikit-learn y otras bibliotecas de Python (ver scripts generados)

## Uso a través de la API REST

El sistema expone las siguientes rutas de API:

### Pipeline Completo

```
POST /api/data-mining/full-pipeline
```

Ejecuta todo el proceso de minería de datos, desde la extracción hasta la visualización.

### Extracción y Limpieza

```
POST /api/data-mining/extract-clean
```

Ejecuta solo las fases de extracción y limpieza de datos.

### Análisis y Visualización

```
POST /api/data-mining/analyze-visualize?messagesPath={path}&sessionsPath={path}
```

Ejecuta las fases de análisis y visualización con los datos ya procesados.

### Generación de Dashboard

```
POST /api/data-mining/generate-dashboard?messagesPath={path}&sessionsPath={path}
```

Genera un script de dashboard interactivo para los datos procesados.

## Estructura de Directorios

Los datos generados se organizan en tres subdirectorios:

- **raw/**: Datos extraídos sin procesar
- **processed/**: Datos después de limpieza y transformación
- **results/**: Resultados de análisis y visualizaciones

## Análisis Disponibles

El sistema ofrece varios tipos de análisis:

1. **Análisis Exploratorio**: Estadísticas descriptivas, distribuciones y tendencias.
2. **Análisis Temporal**: Patrones de uso por hora, día y mes.
3. **Clustering de Temas**: Agrupación de conversaciones por temas similares.
4. **Análisis de Interacción**: Estudio de patrones de interacción entre usuarios y chatbots.

## Visualizaciones

Las visualizaciones generadas incluyen:

1. **Gráficos Estáticos**: Gráficos de líneas, barras, dispersión y mapas de calor.
2. **Dashboard Interactivo**: Panel con filtros y gráficos dinámicos basados en Dash.

## Ejecución de Scripts Python

Los scripts generados pueden ejecutarse manualmente con Python:

```bash
python script_path.py
```

Para el dashboard interactivo:

```bash
pip install dash plotly
python dashboard_script.py
```

## Limitaciones

- El sistema depende de Jython para la ejecución de scripts Python desde Java.
- Para análisis complejos, se recomienda ejecutar los scripts generados directamente con Python.
- El dashboard interactivo requiere instalación manual de dependencias adicionales (Dash, Plotly).

## Ejemplos de Uso

### Java

```java
@Autowired
private DataMiningService dataMiningService;

// Ejecutar pipeline completo
Map<String, Object> results = dataMiningService.runFullDataMiningPipeline();

// Ejecutar solo extracción y limpieza
Map<String, Object> cleanResults = dataMiningService.extractAndCleanData();
```

### Python (con scripts generados)

```python
import pandas as pd
import matplotlib.pyplot as plt

# Cargar datos procesados
messages_df = pd.read_csv('ruta_al_archivo_de_mensajes_procesados.csv')
sessions_df = pd.read_csv('ruta_al_archivo_de_sesiones_procesadas.csv')

# Análisis personalizado
# ...
``` 