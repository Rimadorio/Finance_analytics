package com.example.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MortgageRequest {

    @NotNull(message = "Остаток основного долга обязателен")
    @DecimalMin(value = "1.0", message = "Остаток должен быть больше 0")
    private BigDecimal remainingPrincipal;

    @NotNull(message = "Оставшийся срок обязателен")
    @Min(value = 1, message = "Срок должен быть не меньше 1 месяца")
    private Integer remainingTermMonths;

    @NotNull(message = "Ставка обязательна")
    @DecimalMin(value = "0.01", message = "Ставка должна быть больше 0")
    private BigDecimal annualRatePercent;

    /** Сумма ежемесячного досрочного погашения */
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal extraMonthlyPayment;

    /**
     * Периодичность досрочного погашения в месяцах.
     * 1 = каждый месяц, 2 = раз в 2 месяца, и т.п.
     * Используется, если repaymentPeriodMonths == null или 0.
     */
    @Min(value = 1, message = "Периодичность должна быть >= 1")
    private Integer extraFrequencyMonths;

    /**
     * Период, в течение которого делается досрочное погашение (в месяцах).
     * Если null или 0 — весь оставшийся срок.
     */
    @Min(value = 0)
    private Integer repaymentPeriodMonths;

    /**
     * true  = сокращаем срок (платёж остаётся прежним)
     * false = сокращаем платёж (срок остаётся прежним)
     */
    @NotNull
    private Boolean reduceTerm;

    public BigDecimal getRemainingPrincipal() { return remainingPrincipal; }
    public void setRemainingPrincipal(BigDecimal v) { this.remainingPrincipal = v; }

    public Integer getRemainingTermMonths() { return remainingTermMonths; }
    public void setRemainingTermMonths(Integer v) { this.remainingTermMonths = v; }

    public BigDecimal getAnnualRatePercent() { return annualRatePercent; }
    public void setAnnualRatePercent(BigDecimal v) { this.annualRatePercent = v; }

    public BigDecimal getExtraMonthlyPayment() { return extraMonthlyPayment; }
    public void setExtraMonthlyPayment(BigDecimal v) { this.extraMonthlyPayment = v; }

    public Integer getExtraFrequencyMonths() { return extraFrequencyMonths; }
    public void setExtraFrequencyMonths(Integer v) { this.extraFrequencyMonths = v; }

    public Integer getRepaymentPeriodMonths() { return repaymentPeriodMonths; }
    public void setRepaymentPeriodMonths(Integer v) { this.repaymentPeriodMonths = v; }

    public Boolean getReduceTerm() { return reduceTerm; }
    public void setReduceTerm(Boolean v) { this.reduceTerm = v; }
}