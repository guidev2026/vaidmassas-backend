package com.vaidmassas.api.service;

import com.vaidmassas.api.domain.entity.Category;
import com.vaidmassas.api.domain.entity.Ingredient;
import com.vaidmassas.api.domain.entity.Product;
import com.vaidmassas.api.domain.entity.ProductIngredient;
import com.vaidmassas.api.dto.request.ProductIngredientRequest;
import com.vaidmassas.api.dto.request.ProductRequest;
import com.vaidmassas.api.dto.response.CategoryResponse;
import com.vaidmassas.api.dto.response.ProductIngredientResponse;
import com.vaidmassas.api.dto.response.ProductResponse;
import com.vaidmassas.api.repository.CategoryRepository;
import com.vaidmassas.api.repository.IngredientRepository;
import com.vaidmassas.api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public List<ProductResponse> findAll(Long categoryId) {
        List<Product> products = categoryId != null
                ? productRepository.findByCategoryIdAndActiveTrue(categoryId)
                : productRepository.findByActiveTrue();

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(category)
                .build();

        List<ProductIngredient> ingredients = buildIngredients(request.getIngredients(), product);
        product.setIngredients(ingredients);

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        // limpa a ficha técnica antiga e reconstrói
        product.getIngredients().clear();
        product.getIngredients().addAll(buildIngredients(request.getIngredients(), product));

        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private List<ProductIngredient> buildIngredients(List<ProductIngredientRequest> requests, Product product) {
        return requests.stream().map(req -> {
            Ingredient ingredient = ingredientRepository.findById(req.getIngredientId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Ingrediente não encontrado: id " + req.getIngredientId()));

            return ProductIngredient.builder()
                    .product(product)
                    .ingredient(ingredient)
                    .quantity(req.getQuantity())
                    .unit(req.getUnit())
                    .build();
        }).toList();
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .active(product.getActive())
                .category(CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .description(product.getCategory().getDescription())
                        .active(product.getCategory().getActive())
                        .build())
                .ingredients(product.getIngredients().stream()
                        .map(pi -> ProductIngredientResponse.builder()
                                .ingredientId(pi.getIngredient().getId())
                                .ingredientName(pi.getIngredient().getName())
                                .quantity(pi.getQuantity())
                                .unit(pi.getUnit())
                                .build())
                        .toList())
                .build();
    }
}
