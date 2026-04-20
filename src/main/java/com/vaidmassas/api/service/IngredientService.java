package com.vaidmassas.api.service;

import com.vaidmassas.api.domain.entity.Ingredient;
import com.vaidmassas.api.dto.request.IngredientRequest;
import com.vaidmassas.api.dto.response.IngredientResponse;
import com.vaidmassas.api.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository repository;

    public List<IngredientResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public IngredientResponse findById(Long id) {
        Ingredient ingredient = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));
        return toResponse(ingredient);
    }

    public IngredientResponse create(IngredientRequest request) {
        if (repository.existsByNameIgnoreCaseAndActiveTrue(request.getName())) {
            throw new IllegalArgumentException("Já existe um ingrediente com esse nome");
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .minStock(request.getMinStock())
                .build();

        return toResponse(repository.save(ingredient));
    }

    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setMinStock(request.getMinStock());

        return toResponse(repository.save(ingredient));
    }

    public void delete(Long id) {
        Ingredient ingredient = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

        ingredient.setActive(false);
        repository.save(ingredient);
    }

    private IngredientResponse toResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .minStock(ingredient.getMinStock())
                .active(ingredient.getActive())
                .createdAt(ingredient.getCreatedAt())
                .build();
    }
}