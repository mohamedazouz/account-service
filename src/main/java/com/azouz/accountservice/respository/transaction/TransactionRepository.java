package com.azouz.accountservice.respository.transaction;

import com.azouz.accountservice.domain.transaction.Transaction;

public interface TransactionRepository {

    void create(final Transaction transaction);
}
