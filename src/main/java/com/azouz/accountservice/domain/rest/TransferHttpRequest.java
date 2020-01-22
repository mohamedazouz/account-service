package com.azouz.accountservice.domain.rest;

import java.math.BigDecimal;

public class TransferHttpRequest {
    private String receiverAccountId;
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
