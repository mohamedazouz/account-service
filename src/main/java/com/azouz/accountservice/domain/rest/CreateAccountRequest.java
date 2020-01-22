package com.azouz.accountservice.domain.rest;

import java.math.BigDecimal;

public class CreateAccountRequest {
    private String customerId;

    private BigDecimal balance;

    private String currency;

    public CreateAccountRequest() {
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}
