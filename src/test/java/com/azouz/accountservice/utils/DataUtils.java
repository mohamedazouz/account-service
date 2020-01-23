package com.azouz.accountservice.utils;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.rest.CreateAccountRequest;
import com.azouz.accountservice.domain.transaction.DepositWithdrawBalanceTransactionRequest;

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

    public static CreateAccountRequest getDummyCreateAccountRequest() {
        return DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100));
    }

    public static CreateAccountRequest getDummyCreateAccountRequest(final BigDecimal amount) {
        return new CreateAccountRequest(UUID.randomUUID().toString(), amount, "EUR");
    }

    public static DepositWithdrawBalanceTransactionRequest getDepositWithdrawRequest(final String accountId,
                                                                                     final BigDecimal amount) {
        return new DepositWithdrawBalanceTransactionRequest(accountId, amount);
    }
}
