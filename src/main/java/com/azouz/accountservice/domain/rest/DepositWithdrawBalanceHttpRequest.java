package com.azouz.accountservice.domain.rest;

import java.math.BigDecimal;

public class DepositWithdrawBalanceHttpRequest {
    private BigDecimal amount;

    public DepositWithdrawBalanceHttpRequest() {
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}
