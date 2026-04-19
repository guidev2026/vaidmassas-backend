package com.vaidmassas.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private CategoryResponse category;
    private Boolean active;
    private List<ProductIngredientResponse> ingredients;
}
