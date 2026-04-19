package com.vaidmassas.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductIngredientRequest {

    @NotNull(message = "Ingrediente é obrigatório")
    private Long ingredientId;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private BigDecimal quantity;

    @NotBlank(message = "Unidade é obrigatória")
    private String unit;
}
