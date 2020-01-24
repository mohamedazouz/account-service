package com.azouz.accountservice.domain.transaction;

import com.azouz.accountservice.domain.rest.TransferHttpRequest;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountMoneyTransferTransactionRequest {
    @NotNull
    private String senderAccountId;

    @NotNull
    private String receiverAccountId;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal amount;


    public AccountMoneyTransferTransactionRequest(final String senderAccountId, final String receiverAccountId, final BigDecimal amount) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
    }

    public AccountMoneyTransferTransactionRequest(final String senderAccountId, final TransferHttpRequest transferHttpRequest) {
        this(senderAccountId, transferHttpRequest.getReceiverAccountId(), transferHttpRequest.getAmount());
    }

    public AccountMoneyTransferTransactionRequest() {
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(final String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(final String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
