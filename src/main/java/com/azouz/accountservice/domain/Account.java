package com.azouz.accountservice.domain;

import com.azouz.accountservice.domain.rest.CreateAccountRequest;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonDeserialize(builder = Account.Builder.class)
public class Account {

    @NotNull
    private final String id;

    @NotNull
    private final String customerId;

    @NotNull
    private final BigDecimal balance;

    @NotNull
    private final String currency;

    private Account(final Account.Builder builder) {
        this.balance = builder.balance;
        this.currency = builder.currency;
        this.id = builder.id;
        this.customerId = builder.customerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final Account account) {
        return new Builder(account.id, account.customerId, account.balance, account.currency);
    }

    public static Builder builder(final String accountId, final CreateAccountRequest accountRequest) {
        return new Builder(accountId, accountRequest.getCustomerId(),
                accountRequest.getBalance(), accountRequest.getCurrency());
    }


    public String getId() {
        return this.id;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Account account = (Account) o;
        return Objects.equal(this.id, account.id) &&
                Objects.equal(this.balance, account.balance) &&
                Objects.equal(this.currency, account.currency) &&
                Objects.equal(this.customerId, account.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.balance, this.currency);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("balance", this.balance)
                .add("currency", this.currency)
                .add("customerId", this.customerId)
                .toString();
    }

    public static class Builder {

        private String id;
        private String customerId;
        private BigDecimal balance;
        private String currency;

        public Builder() {
        }

        public Builder(final String id, final String customerId, final BigDecimal balance, final String currency) {
            this.balance = balance;
            this.currency = currency;
            this.id = id;
            this.customerId = customerId;
        }

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }


        public Builder withBalance(final BigDecimal amount) {
            this.balance = amount;
            return this;
        }

        public Builder withCurrency(final String currency) {
            this.currency = currency;
            return this;
        }

        public Builder withCustomerId(final String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }


}

