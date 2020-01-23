package com.azouz.accountservice.domain.rest;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferHttpRequest {
    @NotNull
    private String receiverAccountId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    public TransferHttpRequest() {
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setReceiverAccountId(final String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
