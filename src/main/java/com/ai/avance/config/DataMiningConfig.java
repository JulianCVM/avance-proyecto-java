package com.ai.avance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Configuración para el sistema de minería de datos.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataMiningConfig {
    
    private final Environment env;
    
    @Getter
    public static class Paths {
        // Rutas para almacenamiento de datos
        private final String rawDataPath;
        private final String processedDataPath;
        private final String resultsPath;
        
        public Paths(String baseDir) {
            this.rawDataPath = baseDir + "/raw";
            this.processedDataPath = baseDir + "/processed";
            this.resultsPath = baseDir + "/results";
            
            // Crear directorios si no existen
            createDirectoryIfNotExists(rawDataPath);
            createDirectoryIfNotExists(processedDataPath);
            createDirectoryIfNotExists(resultsPath);
        }
        
        private void createDirectoryIfNotExists(String path) {
            File directory = new File(path);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new RuntimeException("No se pudo crear el directorio: " + path);
                }
            }
        }
    }
    
    @Bean
    public Paths dataMiningPaths() {
        String baseDir = env.getProperty("app.data-mining.base-dir", System.getProperty("java.io.tmpdir") + "/avance/data");
        log.info("Configurando directorio base para minería de datos: {}", baseDir);
        return new Paths(baseDir);
    }
    
    @Getter
    public static class PandasOptions {
        private final int maxColumns;
        private final int colWidth;
        private final int chunkSize;
        private final String maxMemoryUsage;
        
        public PandasOptions(Environment env) {
            this.maxColumns = env.getProperty("app.data-mining.pandas.max-columns", Integer.class, 100);
            this.colWidth = env.getProperty("app.data-mining.pandas.col-width", Integer.class, 50);
            this.chunkSize = env.getProperty("app.data-mining.pandas.chunk-size", Integer.class, 10000);
            this.maxMemoryUsage = env.getProperty("app.data-mining.pandas.max-memory", "1GB");
        }
    }
    
    @Bean
    public PandasOptions pandasOptions() {
        return new PandasOptions(env);
    }
} 