package com.example.finance.controller;

import com.example.finance.dto.DepositRequest;
import com.example.finance.dto.DepositResult;
import com.example.finance.service.DepositService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposit")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/calculate")
    public DepositResult calculate(@Valid @RequestBody DepositRequest req) {
        return depositService.calculate(req);
    }
}