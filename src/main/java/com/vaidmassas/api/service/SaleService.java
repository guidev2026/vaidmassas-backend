package com.vaidmassas.api.service;

import com.vaidmassas.api.domain.entity.Ingredient;
import com.vaidmassas.api.domain.entity.Product;
import com.vaidmassas.api.domain.entity.ProductIngredient;
import com.vaidmassas.api.domain.entity.Sale;
import com.vaidmassas.api.dto.request.SaleRequest;
import com.vaidmassas.api.dto.request.SaleUpdateRequest;
import com.vaidmassas.api.dto.response.SaleDetailResponse;
import com.vaidmassas.api.dto.response.SaleHistoryResponse;
import com.vaidmassas.api.dto.response.SaleResponse;
import com.vaidmassas.api.repository.IngredientRepository;
import com.vaidmassas.api.repository.ProductRepository;
import com.vaidmassas.api.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public SaleResponse create(SaleRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        validateAndDeductStock(product, request.getQuantity());

        BigDecimal totalPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Sale sale = Sale.builder()
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .totalPrice(totalPrice)
                .saleDate(LocalDate.now())
                .build();

        return toResponse(saleRepository.save(sale));
    }

    public List<SaleHistoryResponse> getHistory(String period) {
        LocalDate today = LocalDate.now();

        return switch (period) {
            case "day" -> saleRepository.findDailyHistory(today);
            case "week" -> saleRepository.findByPeriod(
                    today.minusDays(6), today);
            case "month" -> saleRepository.findByPeriod(
                    today.withDayOfMonth(1), today);
            default -> throw new IllegalArgumentException(
                    "Período inválido. Use: day, week ou month");
        };
    }

    private void validateAndDeductStock(Product product, Integer quantity) {
        for (ProductIngredient pi : product.getIngredients()) {
            Ingredient ingredient = pi.getIngredient();

            BigDecimal totalNeeded = pi.getQuantity()
                    .multiply(BigDecimal.valueOf(quantity));

            if (ingredient.getQuantity().compareTo(totalNeeded) < 0) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para: " + ingredient.getName()
                                + ". Disponível: " + ingredient.getQuantity()
                                + " " + ingredient.getUnit()
                                + " | Necessário: " + totalNeeded
                                + " " + ingredient.getUnit()
                );
            }

            ingredient.setQuantity(ingredient.getQuantity().subtract(totalNeeded));
            ingredientRepository.save(ingredient);
        }
    }

    private SaleResponse toResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .productName(sale.getProduct().getName())
                .quantity(sale.getQuantity())
                .unitPrice(sale.getUnitPrice())
                .totalPrice(sale.getTotalPrice())
                .saleDate(sale.getSaleDate())
                .createdAt(sale.getCreatedAt())
                .build();
    }
    public List<SaleDetailResponse> getDetailedHistory(String period) {
        LocalDate today = LocalDate.now();
        List<Sale> sales = switch (period) {
            case "day"   -> saleRepository.findDetailedByDay(today);
            case "week"  -> saleRepository.findDetailedByPeriod(today.minusDays(6), today);
            case "month" -> saleRepository.findDetailedByPeriod(today.withDayOfMonth(1), today);
            default -> throw new IllegalArgumentException("Período inválido. Use: day, week ou month");
        };
        return sales.stream().map(this::toDetailResponse).toList();
    }

    @Transactional
    public SaleResponse update(Long id, SaleUpdateRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));

        int diff = request.getQuantity() - sale.getQuantity();

        for (ProductIngredient pi : sale.getProduct().getIngredients()) {
            Ingredient ingredient = pi.getIngredient();
            BigDecimal adjustment = pi.getQuantity().multiply(BigDecimal.valueOf(Math.abs(diff)));

            if (diff > 0) {
                if (ingredient.getQuantity().compareTo(adjustment) < 0) {
                    throw new IllegalArgumentException("Estoque insuficiente para: " + ingredient.getName());
                }
                ingredient.setQuantity(ingredient.getQuantity().subtract(adjustment));
            } else if (diff < 0) {
                ingredient.setQuantity(ingredient.getQuantity().add(adjustment));
            }
            ingredientRepository.save(ingredient);
        }

        sale.setQuantity(request.getQuantity());
        sale.setTotalPrice(sale.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));

        for (ProductIngredient pi : sale.getProduct().getIngredients()) {
            Ingredient ingredient = pi.getIngredient();
            BigDecimal toReturn = pi.getQuantity().multiply(BigDecimal.valueOf(sale.getQuantity()));
            ingredient.setQuantity(ingredient.getQuantity().add(toReturn));
            ingredientRepository.save(ingredient);
        }

        saleRepository.delete(sale);
    }

    private SaleDetailResponse toDetailResponse(Sale sale) {
        return SaleDetailResponse.builder()
                .id(sale.getId())
                .productName(sale.getProduct().getName())
                .quantity(sale.getQuantity())
                .unitPrice(sale.getUnitPrice())
                .totalPrice(sale.getTotalPrice())
                .saleDate(sale.getSaleDate())
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
