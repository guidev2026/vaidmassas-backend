package com.vaidmassas.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class IngredientResponse {

    private Long id;
    private String name;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal minStock;
    private Boolean active;
    private LocalDateTime createdAt;
}