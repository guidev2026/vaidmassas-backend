package com.vaidmassas.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ProductRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoryId;

    @NotNull(message = "Ficha técnica é obrigatória")
    private List<ProductIngredientRequest> ingredients;
}