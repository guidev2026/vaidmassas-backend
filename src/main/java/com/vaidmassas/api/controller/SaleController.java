package com.vaidmassas.api.controller;

import com.vaidmassas.api.dto.request.SaleRequest;
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

    @GetMapping("/history")
    public ResponseEntity<List<SaleHistoryResponse>> getHistory(
            @RequestParam(defaultValue = "day") String period) {
        return ResponseEntity.ok(service.getHistory(period));
    }
}
