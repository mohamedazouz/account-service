package com.azouz.accountservice.domain.rest;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateAccountRequest {
    @NotNull
    private String customerId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal balance;

    @NotNull
    private String currency;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(@NotNull final String customerId,
                                @NotNull @DecimalMin(value = "0.0", inclusive = false) final BigDecimal balance,
                                @NotNull final String currency) {
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
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
