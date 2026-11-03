package com.example.finance.service;

import com.example.finance.dto.DepositRequest;
import com.example.finance.dto.DepositResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class DepositService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);

    public DepositResult calculate(DepositRequest req) {
        BigDecimal amount = req.getAmount();
        BigDecimal rate = req.getAnnualRatePercent().divide(HUNDRED, MC);
        int months = req.getTermMonths();

        BigDecimal finalAmount;

        if (Boolean.TRUE.equals(req.getCapitalization())) {
            // Сложные проценты с ежемесячной капитализацией:
            // FV = P * (1 + r/12)^n
            BigDecimal monthlyRate = rate.divide(TWELVE, MC);
            BigDecimal base = BigDecimal.ONE.add(monthlyRate);
            BigDecimal factor = base.pow(months, MC);
            finalAmount = amount.multiply(factor, MC);
        } else {
            // Простые проценты:
            // FV = P + P * r * (months/12)
            BigDecimal years = BigDecimal.valueOf(months).divide(TWELVE, MC);
            BigDecimal interest = amount.multiply(rate, MC).multiply(years, MC);
            finalAmount = amount.add(interest);
        }

        finalAmount = finalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal income = finalAmount.subtract(amount).setScale(2, RoundingMode.HALF_UP);

        // Эффективная годовая ставка (для сравнения)
        BigDecimal years = BigDecimal.valueOf(months).divide(TWELVE, MC);
        BigDecimal effectiveRate = BigDecimal.ZERO;
        if (years.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(BigDecimal.ZERO) > 0) {
            // ((FV/P)^(1/years) - 1) * 100
            BigDecimal ratio = finalAmount.divide(amount, MC);
            double exp = 1.0 / years.doubleValue();
            double ratioD = ratio.doubleValue();
            double eff = (Math.pow(ratioD, exp) - 1.0) * 100.0;
            effectiveRate = BigDecimal.valueOf(eff).setScale(2, RoundingMode.HALF_UP);
        }

        return new DepositResult(amount, finalAmount, income, effectiveRate);
    }
}