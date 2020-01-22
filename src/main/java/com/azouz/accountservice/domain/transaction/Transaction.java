package com.azouz.accountservice.domain.transaction;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

public class Transaction {
    private final String id;
    private final String accountId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Long timestamp;

    public Transaction(final Builder builder) {
        this.id = builder.id;
        this.accountId = builder.accountId;
        this.type = builder.type;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return this.id;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public TransactionType getType() {
        return this.type;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Transaction that = (Transaction) o;
        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.accountId, that.accountId) &&
                this.type == that.type &&
                Objects.equal(this.amount, that.amount) &&
                Objects.equal(this.timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.accountId, this.type, this.amount, this.timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("accountId", this.accountId)
                .add("type", this.type)
                .add("amount", this.amount)
                .add("timestamp", this.timestamp)
                .toString();
    }

    public static class Builder {

        private String id;
        private String accountId;
        private TransactionType type;
        private BigDecimal amount;
        private Long timestamp;

        public Builder() {
        }

        public Builder(final String id, final String accountId, final TransactionType type, final BigDecimal amount, final Long timestamp) {
            this.id = id;
            this.accountId = accountId;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }


        public Builder withAccountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder withType(final TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder withAmount(final BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withTimestamp(final Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

}
