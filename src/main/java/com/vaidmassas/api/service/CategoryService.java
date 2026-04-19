package com.vaidmassas.api.service;

import com.vaidmassas.api.domain.entity.Category;
import com.vaidmassas.api.dto.request.CategoryRequest;
import com.vaidmassas.api.dto.response.CategoryResponse;
import com.vaidmassas.api.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<CategoryResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Já existe uma categoria com esse nome");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return toResponse(repository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toResponse(repository.save(category));
    }

    public void delete(Long id) {
        Category category = findOrThrow(id);
        category.setActive(false);
        repository.save(category);
    }

    private Category findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }
}
