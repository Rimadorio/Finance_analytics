package com.example.finance;

import com.example.finance.dto.MortgageRequest;
import com.example.finance.dto.MortgageResult;
import com.example.finance.service.MortgageService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MortgageServiceTest {

    private final MortgageService service = new MortgageService();

    @Test
    void testMortgageWithoutExtraPayments() {
        MortgageRequest req = new MortgageRequest();
        req.setRemainingPrincipal(new BigDecimal("1000000"));
        req.setRemainingTermMonths(12);
        req.setAnnualRatePercent(new BigDecimal("12"));
        req.setExtraMonthlyPayment(BigDecimal.ZERO);
        req.setExtraFrequencyMonths(1);
        req.setRepaymentPeriodMonths(0);
        req.setReduceTerm(true);

        MortgageResult result = service.simulate(req);

        // Если досрочек нет, экономия должна быть 0
        assertEquals(0, result.getTotalSaved().compareTo(BigDecimal.ZERO));
        assertTrue(result.getTermMonthsWithExtra() > 0);
    }
}