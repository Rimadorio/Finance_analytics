package com.example.finance.dto;

import java.math.BigDecimal;
import java.util.List;

public class MortgageResult {
    private BigDecimal monthlyPaymentWithoutExtra;   // базовый платёж (без досрочек)
    private BigDecimal monthlyPaymentWithExtra;      // платёж в первом месяце с досрочками
    private int termMonthsWithoutExtra;
    private int termMonthsWithExtra;
    private BigDecimal totalInterestWithoutExtra;
    private BigDecimal totalInterestWithExtra;
    private BigDecimal totalSaved;                   // экономия на процентах
    private BigDecimal totalExtraPaid;               // сумма всех досрочных платежей
    private List<PaymentScheduleRow> scheduleWithoutExtra;
    private List<PaymentScheduleRow> scheduleWithExtra;

    public MortgageResult(BigDecimal monthlyPaymentWithoutExtra, BigDecimal monthlyPaymentWithExtra,
                          int termMonthsWithoutExtra, int termMonthsWithExtra,
                          BigDecimal totalInterestWithoutExtra, BigDecimal totalInterestWithExtra,
                          BigDecimal totalSaved, BigDecimal totalExtraPaid,
                          List<PaymentScheduleRow> scheduleWithoutExtra,
                          List<PaymentScheduleRow> scheduleWithExtra) {
        this.monthlyPaymentWithoutExtra = monthlyPaymentWithoutExtra;
        this.monthlyPaymentWithExtra = monthlyPaymentWithExtra;
        this.termMonthsWithoutExtra = termMonthsWithoutExtra;
        this.termMonthsWithExtra = termMonthsWithExtra;
        this.totalInterestWithoutExtra = totalInterestWithoutExtra;
        this.totalInterestWithExtra = totalInterestWithExtra;
        this.totalSaved = totalSaved;
        this.totalExtraPaid = totalExtraPaid;
        this.scheduleWithoutExtra = scheduleWithoutExtra;
        this.scheduleWithExtra = scheduleWithExtra;
    }

    public BigDecimal getMonthlyPaymentWithoutExtra() { return monthlyPaymentWithoutExtra; }
    public BigDecimal getMonthlyPaymentWithExtra() { return monthlyPaymentWithExtra; }
    public int getTermMonthsWithoutExtra() { return termMonthsWithoutExtra; }
    public int getTermMonthsWithExtra() { return termMonthsWithExtra; }
    public BigDecimal getTotalInterestWithoutExtra() { return totalInterestWithoutExtra; }
    public BigDecimal getTotalInterestWithExtra() { return totalInterestWithExtra; }
    public BigDecimal getTotalSaved() { return totalSaved; }
    public BigDecimal getTotalExtraPaid() { return totalExtraPaid; }
    public List<PaymentScheduleRow> getScheduleWithoutExtra() { return scheduleWithoutExtra; }
    public List<PaymentScheduleRow> getScheduleWithExtra() { return scheduleWithExtra; }
}