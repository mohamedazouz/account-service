package com.azouz.accountservice.repository;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.respository.account.InMemoryAccountRepository;
import com.azouz.accountservice.utils.DataUtils;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InMemoryAccountRepositoryTest {

    private InMemoryAccountRepository accountRepository;

    @Before
    public void setup() {
        this.accountRepository = new InMemoryAccountRepository();
    }

    @Test
    public void createNewAccount() {
        final Account account = this.createAccount();
        this.assertGetAllAccounts(Lists.newArrayList(account));
    }

    @Test
    public void getAccountById() {
        final Account account = this.createAccount();
        final Optional<Account> expectedAccountOpt = this.accountRepository.getById(account.getId());
        assertEquals(expectedAccountOpt.get(), account);
    }

    public void getAccountByIdWithEmptyResponse() {
        this.assertGetAllAccounts(Lists.newArrayList());
        final Optional<Account> expectedAccountOpt = this.accountRepository.getById("1234");
        assertFalse(expectedAccountOpt.isPresent());
    }

    @Test
    public void updateAccountBalance() {
        final Account account = this.createAccount();
        final Account newUpdatedAccount = Account.builder(account).withBalance(BigDecimal.valueOf(100)).build();
        this.accountRepository.upsert(newUpdatedAccount);
        final Optional<Account> expectedAccountOpt = this.accountRepository.getById(account.getId());
        assertEquals(expectedAccountOpt.get(), newUpdatedAccount);
        this.assertGetAllAccounts(Lists.newArrayList(newUpdatedAccount));
    }


    @Test
    public void createListOfAccounts() {
        final List<Account> accounts = Lists.newArrayList();
        IntStream.range(0, 10).parallel().forEach(n -> {
            accounts.add(this.createAccount());
        });
        this.assertGetAllAccounts(accounts);
    }

    private void assertGetAllAccounts(final Collection<Account> accounts) {
        final List<Account> expectedAccounts = this.accountRepository.getAll();
        assertThat(expectedAccounts, containsInAnyOrder(expectedAccounts.toArray()));
    }

    private Account createAccount() {
        final Account account = DataUtils.getDummyAccount();
        this.accountRepository.upsert(account);
        return account;
    }

}
