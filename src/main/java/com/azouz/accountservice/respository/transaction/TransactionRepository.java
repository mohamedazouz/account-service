package com.azouz.accountservice.respository.transaction;

import com.azouz.accountservice.domain.transaction.Transaction;

import java.util.List;

public interface TransactionRepository {

    void upsert(final Transaction transaction);

    List<Transaction> getTransactions();
}
