package com.azouz.accountservice.respository.transaction;

import com.azouz.accountservice.domain.transaction.Transaction;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<String, Transaction> transactionMap;

    public InMemoryTransactionRepository() {
        this.transactionMap = new ConcurrentHashMap<>();
    }

    public InMemoryTransactionRepository(final Map<String, Transaction> transactionMap) {
        this.transactionMap = transactionMap;
    }

    @Override
    public void create(final Transaction transaction) {
        this.transactionMap.put(transaction.getId(), transaction);
    }
}
