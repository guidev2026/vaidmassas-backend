package com.vaidmassas.api.controller;

import com.vaidmassas.api.dto.request.SaleRequest;
import com.vaidmassas.api.dto.request.SaleUpdateRequest;
import com.vaidmassas.api.dto.response.SaleDetailResponse;
import com.vaidmassas.api.dto.response.SaleHistoryResponse;
import com.vaidmassas.api.dto.response.SaleResponse;
import com.vaidmassas.api.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService service;

    @PostMapping
    public ResponseEntity<SaleResponse> create(@RequestBody @Valid SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> update(@PathVariable Long id,
                                               @RequestBody @Valid SaleUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<SaleHistoryResponse>> getHistory(
            @RequestParam(defaultValue = "day") String period) {
        return ResponseEntity.ok(service.getHistory(period));
    }

    @GetMapping("/history/detailed")
    public ResponseEntity<List<SaleDetailResponse>> getDetailedHistory(
            @RequestParam(defaultValue = "day") String period) {
        return ResponseEntity.ok(service.getDetailedHistory(period));
    }
}