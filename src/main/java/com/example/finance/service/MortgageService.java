package com.example.finance.service;

import com.example.finance.dto.MortgageRequest;
import com.example.finance.dto.MortgageResult;
import com.example.finance.dto.PaymentScheduleRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class MortgageService {

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final int MAX_MONTHS = 1200; // защита от бесконечного цикла (100 лет)

    public MortgageResult simulate(MortgageRequest req) {
        BigDecimal principal = req.getRemainingPrincipal();
        int termMonths = req.getRemainingTermMonths();
        BigDecimal monthlyRate = req.getAnnualRatePercent()
                .divide(HUNDRED, MC)
                .divide(TWELVE, MC);

        BigDecimal basePayment = calculateAnnuityPayment(principal, monthlyRate, termMonths);

        // --- 1. Базовый график без досрочек ---
        SimulationResult base = simulateSchedule(
                principal, basePayment, monthlyRate,
                BigDecimal.ZERO, 0, 0, true, termMonths);

        // --- 2. График с досрочками ---
        BigDecimal extra = req.getExtraMonthlyPayment() == null
                ? BigDecimal.ZERO : req.getExtraMonthlyPayment();

        int frequency = req.getExtraFrequencyMonths() == null
                ? 1 : req.getExtraFrequencyMonths();

        int repaymentPeriod = req.getRepaymentPeriodMonths() == null
                ? 0 : req.getRepaymentPeriodMonths();

        SimulationResult withExtra = simulateSchedule(
                principal, basePayment, monthlyRate,
                extra, frequency, repaymentPeriod,
                Boolean.TRUE.equals(req.getReduceTerm()), termMonths);

        BigDecimal saved = base.totalInterest
                .subtract(withExtra.totalInterest)
                .setScale(2, RoundingMode.HALF_UP);

        return new MortgageResult(
                basePayment.setScale(2, RoundingMode.HALF_UP),
                withExtra.firstMonthPayment.setScale(2, RoundingMode.HALF_UP),
                base.months,
                withExtra.months,
                base.totalInterest.setScale(2, RoundingMode.HALF_UP),
                withExtra.totalInterest.setScale(2, RoundingMode.HALF_UP),
                saved,
                withExtra.totalExtraPaid.setScale(2, RoundingMode.HALF_UP),
                base.schedule,
                withExtra.schedule
        );
    }

    /**
     * Аннуитетный платёж:
     * P = S * r / (1 - (1+r)^(-n))
     */
    private BigDecimal calculateAnnuityPayment(BigDecimal debt, BigDecimal monthlyRate, int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return debt.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusR.pow(months, MC);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(pow, MC), MC);
        return debt.multiply(monthlyRate, MC).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    /**
     * Симуляция графика платежей по месяцам.
     */
    private SimulationResult simulateSchedule(BigDecimal initialDebt,
                                              BigDecimal initialPayment,
                                              BigDecimal monthlyRate,
                                              BigDecimal extraAmount,
                                              int frequency,
                                              int repaymentPeriod,
                                              boolean reduceTerm,
                                              int originalTerm) {

        BigDecimal debt = initialDebt;
        BigDecimal payment = initialPayment;
        int months = 0;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalExtra = BigDecimal.ZERO;
        BigDecimal firstMonthPayment = initialPayment;
        List<PaymentScheduleRow> schedule = new ArrayList<>();

        // если пользователь не указал период досрочек - работаем весь срок
        int extraWindow = repaymentPeriod <= 0 ? originalTerm : repaymentPeriod;

        while (debt.compareTo(BigDecimal.ZERO) > 0 && months < MAX_MONTHS) {
            months++;

            BigDecimal interest = debt.multiply(monthlyRate, MC).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPart = payment.subtract(interest);

            // последний месяц - гасим остаток
            if (principalPart.compareTo(debt) >= 0) {
                principalPart = debt;
                payment = principalPart.add(interest);
            }

            debt = debt.subtract(principalPart);

            // Досрочное погашение
            BigDecimal extraThisMonth = BigDecimal.ZERO;
            boolean isExtraMonth = extraAmount.compareTo(BigDecimal.ZERO) > 0
                    && months <= extraWindow
                    && (months % frequency == 0);

            if (isExtraMonth && debt.compareTo(BigDecimal.ZERO) > 0) {
                extraThisMonth = extraAmount.min(debt);
                debt = debt.subtract(extraThisMonth);
                totalExtra = totalExtra.add(extraThisMonth);
            }

            if (months == 1) {
                firstMonthPayment = payment.add(extraThisMonth);
            }

            totalInterest = totalInterest.add(interest);

            schedule.add(new PaymentScheduleRow(
                    months,
                    payment.setScale(2, RoundingMode.HALF_UP),
                    interest,
                    principalPart.setScale(2, RoundingMode.HALF_UP),
                    extraThisMonth.setScale(2, RoundingMode.HALF_UP),
                    debt.setScale(2, RoundingMode.HALF_UP)
            ));

            if (debt.compareTo(BigDecimal.ZERO) <= 0) break;

            // Логика пересчёта:
            if (isExtraMonth && extraThisMonth.compareTo(BigDecimal.ZERO) > 0) {
                if (!reduceTerm) {
                    // Сокращаем платёж: пересчитываем на оставшийся срок
                    int remainingMonths = originalTerm - months;
                    if (remainingMonths <= 0) remainingMonths = 1;
                    payment = calculateAnnuityPayment(debt, monthlyRate, remainingMonths);
                }
                // Если reduceTerm == true — платёж не меняется, просто закроемся раньше
            }
        }

        return new SimulationResult(
                months, totalInterest, totalExtra, firstMonthPayment, schedule
        );
    }

    private static class SimulationResult {
        final int months;
        final BigDecimal totalInterest;
        final BigDecimal totalExtraPaid;
        final BigDecimal firstMonthPayment;
        final List<PaymentScheduleRow> schedule;

        SimulationResult(int months, BigDecimal totalInterest, BigDecimal totalExtraPaid,
                         BigDecimal firstMonthPayment, List<PaymentScheduleRow> schedule) {
            this.months = months;
            this.totalInterest = totalInterest;
            this.totalExtraPaid = totalExtraPaid;
            this.firstMonthPayment = firstMonthPayment;
            this.schedule = schedule;
        }
    }
}