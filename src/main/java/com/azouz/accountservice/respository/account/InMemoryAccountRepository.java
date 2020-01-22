package com.azouz.accountservice.respository.account;

import com.azouz.accountservice.domain.Account;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accountMap;

    public InMemoryAccountRepository() {
        this.accountMap = new ConcurrentHashMap<>();
    }

    public InMemoryAccountRepository(final Map<String, Account> accountMap) {
        this.accountMap = accountMap;
    }

    @Override
    public void upsert(@Nonnull @Valid final Account account) {
        this.accountMap.put(account.getId(), account);
    }

    @Override
    public Optional<Account> getById(final String id) {
        return Optional.ofNullable(this.accountMap.get(id));
    }

    @Override
    public List<Account> getAll() {
        return Lists.newArrayList(this.accountMap.values());
    }

}
