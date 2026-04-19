package com.vaidmassas.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SaleHistoryResponse {

    private LocalDate date;
    private Long totalOrders;
    private BigDecimal totalRevenue;
}