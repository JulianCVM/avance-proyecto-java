package com.ai.avance.repository;

import com.ai.avance.model.LLMModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LLMModelRepository extends JpaRepository<LLMModel, Long> {
    Optional<LLMModel> findByNameAndVersion(String name, String version);
    Optional<LLMModel> findByModelType(String modelType);
} 