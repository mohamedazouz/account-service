package com.azouz.accountservice.domain.transaction;

import com.azouz.accountservice.domain.rest.DepositWithdrawBalanceHttpRequest;

import java.math.BigDecimal;

public class DepositWithdrawBalanceTransactionRequest {
    private String accountId;
    private BigDecimal amount;

    public DepositWithdrawBalanceTransactionRequest() {
    }

    public DepositWithdrawBalanceTransactionRequest(final String accountId, final BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public DepositWithdrawBalanceTransactionRequest(final String accountId, final DepositWithdrawBalanceHttpRequest httpRequest) {
        this(accountId, httpRequest.getAmount());
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}
