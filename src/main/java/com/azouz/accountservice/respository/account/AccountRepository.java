package com.azouz.accountservice.respository.account;

import com.azouz.accountservice.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    void upsert(final Account account);

    Optional<Account> getById(final String id) throws RuntimeException;

    List<Account> getAll();
}
