package com.example.finance.controller;

import com.example.finance.dto.MortgageRequest;
import com.example.finance.dto.MortgageResult;
import com.example.finance.service.MortgageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mortgage")
public class MortgageController {

    private final MortgageService mortgageService;

    public MortgageController(MortgageService mortgageService) {
        this.mortgageService = mortgageService;
    }

    @PostMapping("/calculate")
    public MortgageResult calculate(@Valid @RequestBody MortgageRequest req) {
        return mortgageService.simulate(req);
    }
}