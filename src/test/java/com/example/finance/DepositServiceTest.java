package com.example.finance;

import com.example.finance.dto.DepositRequest;
import com.example.finance.dto.DepositResult;
import com.example.finance.service.DepositService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DepositServiceTest {

    private final DepositService service = new DepositService();

    @Test
    void testWithoutCapitalization() {
        DepositRequest req = new DepositRequest();
        req.setAmount(new BigDecimal("100000"));
        req.setTermMonths(12);
        req.setAnnualRatePercent(new BigDecimal("12"));
        req.setCapitalization(false);

        DepositResult result = service.calculate(req);

        // 100 000 + (100 000 * 0.12) = 112 000
        assertEquals(new BigDecimal("112000.00"), result.getFinalAmount());
        assertEquals(new BigDecimal("12000.00"), result.getIncome());
    }

    @Test
    void testWithCapitalization() {
        DepositRequest req = new DepositRequest();
        req.setAmount(new BigDecimal("100000"));
        req.setTermMonths(12);
        req.setAnnualRatePercent(new BigDecimal("12"));
        req.setCapitalization(true);

        DepositResult result = service.calculate(req);

        // Формула сложного процента: 100000 * (1 + 0.12/12)^12 = 112682.50
        assertEquals(new BigDecimal("112682.50"), result.getFinalAmount());
    }
}