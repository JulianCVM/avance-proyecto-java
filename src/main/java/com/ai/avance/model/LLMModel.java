package com.ai.avance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "llm_models")
public class LLMModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String modelType;

    @Column(nullable = false)
    private Integer maxTokens;

    @Column(nullable = false)
    private Double temperature;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<ModelParameter> parameters;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<TrainingData> trainingData;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public List<ModelParameter> getParameters() { return parameters; }
    public void setParameters(List<ModelParameter> parameters) { this.parameters = parameters; }
    public List<TrainingData> getTrainingData() { return trainingData; }
    public void setTrainingData(List<TrainingData> trainingData) { this.trainingData = trainingData; }
} 