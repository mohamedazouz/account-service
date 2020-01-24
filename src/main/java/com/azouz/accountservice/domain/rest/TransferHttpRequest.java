package com.azouz.accountservice.domain.rest;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferHttpRequest {
    @NotNull
    private String receiverAccountId;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal amount;

    public TransferHttpRequest() {
    }

    public TransferHttpRequest(@NotNull final String receiverAccountId,
                               @NotNull @DecimalMin(value = "0.0") final BigDecimal amount) {
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
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
