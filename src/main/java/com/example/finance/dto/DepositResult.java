package com.example.finance.dto;

import java.math.BigDecimal;

public class DepositResult {
    private BigDecimal initialAmount;
    private BigDecimal finalAmount;
    private BigDecimal income;
    private BigDecimal effectiveRatePercent;

    public DepositResult(BigDecimal initialAmount, BigDecimal finalAmount,
                         BigDecimal income, BigDecimal effectiveRatePercent) {
        this.initialAmount = initialAmount;
        this.finalAmount = finalAmount;
        this.income = income;
        this.effectiveRatePercent = effectiveRatePercent;
    }

    public BigDecimal getInitialAmount() { return initialAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getEffectiveRatePercent() { return effectiveRatePercent; }
}