package com.ai.avance.service;

import com.ai.avance.model.LLMModel;
import com.ai.avance.model.ModelParameter;
import com.ai.avance.model.TrainingData;
import com.ai.avance.repository.LLMModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LLMService {
    private final LLMModelRepository modelRepository;

    @Autowired
    public LLMService(LLMModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Transactional
    public LLMModel createModel(LLMModel model) {
        return modelRepository.save(model);
    }

    @Transactional(readOnly = true)
    public Optional<LLMModel> getModel(Long id) {
        return modelRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<LLMModel> getAllModels() {
        return modelRepository.findAll();
    }

    @Transactional
    public LLMModel updateModel(Long id, LLMModel updatedModel) {
        return modelRepository.findById(id)
                .map(existingModel -> {
                    existingModel.setName(updatedModel.getName());
                    existingModel.setVersion(updatedModel.getVersion());
                    existingModel.setDescription(updatedModel.getDescription());
                    existingModel.setModelType(updatedModel.getModelType());
                    existingModel.setMaxTokens(updatedModel.getMaxTokens());
                    existingModel.setTemperature(updatedModel.getTemperature());
                    return modelRepository.save(existingModel);
                })
                .orElseThrow(() -> new RuntimeException("Modelo no encontrado"));
    }

    @Transactional
    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }

    @Transactional
    public void addTrainingData(Long modelId, TrainingData trainingData) {
        modelRepository.findById(modelId)
                .ifPresent(model -> {
                    model.getTrainingData().add(trainingData);
                    modelRepository.save(model);
                });
    }

    @Transactional
    public void addParameter(Long modelId, ModelParameter parameter) {
        modelRepository.findById(modelId)
                .ifPresent(model -> {
                    model.getParameters().add(parameter);
                    modelRepository.save(model);
                });
    }
} 