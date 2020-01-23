package com.azouz.accountservice.respository.transaction;

import com.azouz.accountservice.domain.transaction.Transaction;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import java.util.List;
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
    public void upsert(final Transaction transaction) {
        this.transactionMap.put(transaction.getId(), transaction);
    }

    @Override
    public List<Transaction> getTransactions() {
        return Lists.newArrayList(this.transactionMap.values());
    }
}
