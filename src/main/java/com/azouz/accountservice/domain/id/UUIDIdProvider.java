package com.azouz.accountservice.domain.id;

import java.util.UUID;

public class UUIDIdProvider implements IdProvider {

    @Override
    public String generateAccountId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
