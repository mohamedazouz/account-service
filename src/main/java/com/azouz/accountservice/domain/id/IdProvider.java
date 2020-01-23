package com.azouz.accountservice.domain.id;

public interface IdProvider {
    String generateAccountId();

    String generateTransactionId();
}
