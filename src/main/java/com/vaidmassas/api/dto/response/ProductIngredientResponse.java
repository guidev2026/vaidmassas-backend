package com.vaidmassas.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductIngredientResponse {

    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
}