package com.azouz.accountservice.utils;

import com.azouz.accountservice.domain.Account;

import java.math.BigDecimal;
import java.util.UUID;

public class DataUtils {

    public static Account getDummyAccount() {
        return Account.builder()
                .withId(UUID.randomUUID().toString())
                .withCustomerId(UUID.randomUUID().toString())
                .withBalance(BigDecimal.valueOf(Math.random()))
                .withCurrency("EUR").build();
    }
}
