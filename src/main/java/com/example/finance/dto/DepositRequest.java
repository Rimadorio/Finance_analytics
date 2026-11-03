package com.example.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DepositRequest {

    @NotNull(message = "Сумма вклада обязательна")
    @DecimalMin(value = "1.0", message = "Сумма вклада должна быть больше 0")
    private BigDecimal amount;

    @NotNull(message = "Срок обязателен")
    @Min(value = 1, message = "Срок должен быть не меньше 1 месяца")
    private Integer termMonths;

    @NotNull(message = "Ставка обязательна")
    @DecimalMin(value = "0.01", message = "Ставка должна быть больше 0")
    private BigDecimal annualRatePercent;

    @NotNull(message = "Признак капитализации обязателен")
    private Boolean capitalization;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }

    public BigDecimal getAnnualRatePercent() { return annualRatePercent; }
    public void setAnnualRatePercent(BigDecimal annualRatePercent) { this.annualRatePercent = annualRatePercent; }

    public Boolean getCapitalization() { return capitalization; }
    public void setCapitalization(Boolean capitalization) { this.capitalization = capitalization; }
}