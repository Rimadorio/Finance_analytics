package com.example.finance.dto;

import java.math.BigDecimal;

public class PaymentScheduleRow {
    private int month;
    private BigDecimal payment;
    private BigDecimal interestPart;
    private BigDecimal principalPart;
    private BigDecimal extraPayment;
    private BigDecimal remainingDebt;

    public PaymentScheduleRow(int month, BigDecimal payment, BigDecimal interestPart,
                              BigDecimal principalPart, BigDecimal extraPayment,
                              BigDecimal remainingDebt) {
        this.month = month;
        this.payment = payment;
        this.interestPart = interestPart;
        this.principalPart = principalPart;
        this.extraPayment = extraPayment;
        this.remainingDebt = remainingDebt;
    }

    public int getMonth() { return month; }
    public BigDecimal getPayment() { return payment; }
    public BigDecimal getInterestPart() { return interestPart; }
    public BigDecimal getPrincipalPart() { return principalPart; }
    public BigDecimal getExtraPayment() { return extraPayment; }
    public BigDecimal getRemainingDebt() { return remainingDebt; }
}