package com.azouz.accountservice.service;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.id.UUIDIdProvider;
import com.azouz.accountservice.domain.transaction.DepositWithdrawBalanceTransactionRequest;
import com.azouz.accountservice.domain.transaction.TransactionType;
import com.azouz.accountservice.exception.AccountNotFoundException;
import com.azouz.accountservice.exception.InsufficientAccountBalanceException;
import com.azouz.accountservice.respository.account.InMemoryAccountRepository;
import com.azouz.accountservice.respository.transaction.InMemoryTransactionRepository;
import com.azouz.accountservice.utils.DataUtils;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountServiceTest {

    private AccountService accountService;

    @Before
    public void setup() {
        this.accountService =
                new AccountService(new InMemoryAccountRepository(),
                        new InMemoryTransactionRepository(), new UUIDIdProvider());
    }

    @Test
    public void createNewAccountTest() {
        final Account account = this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest());
        assertNotNull(account);
        assertGetAllAccounts(Lists.newArrayList(account));
    }

    @Test
    public void getExistingAccountTest() {
        final Account createdAccount = this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest());
        final Account getAccount = this.accountService.getAccount(createdAccount.getId());
        assertNotNull(getAccount);
        assertEquals(createdAccount, getAccount);
        assertGetAllAccounts(Lists.newArrayList(getAccount));
    }

    @Test(expected = AccountNotFoundException.class)
    public void getNonExistingAccountTest() {
        assertGetAllAccounts(Lists.newArrayList());
        this.accountService.getAccount("non-existing-account-id");
        throw new RuntimeException("test must not come at this line");
    }

    public void deteAccountTest() {
        final Account account = this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest());
        assertNotNull(account);
        assertGetAllAccounts(Lists.newArrayList(account));

        this.accountService.deleteAccount(account.getId());
        assertGetAllAccounts(Lists.newArrayList(account));
    }

    @Test
    public void createMultipleNewAccountsTest() throws InterruptedException {
        final List<Account> accounts = Lists.newArrayList();
        final int sizeOfNewAccounts = 10;
        executeInConcurrentEnv(() -> {
            accounts.add(this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest()));
            return null;
        }, sizeOfNewAccounts);
        assertEquals(accounts.size(), sizeOfNewAccounts);
        assertGetAllAccounts(accounts);
    }

    @Test(expected = AccountNotFoundException.class)
    public void depositNonExistingAccountTest() {
        assertGetAllAccounts(Lists.newArrayList());
        this.accountService
                .accountDeposit(DataUtils.getDepositWithdrawRequest("not-id", BigDecimal.valueOf(100)));
        throw new RuntimeException("test must not come at this line");
    }

    @Test
    public void depositExistingAccountTest() {
        final Account createdAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        final BigDecimal depositAmount = BigDecimal.valueOf(100);
        this.accountService.accountDeposit(DataUtils.getDepositWithdrawRequest(createdAccount.getId(), depositAmount));

        final Account expectedAccount = Account.builder(createdAccount).withBalance(createdAccount
                .getBalance().add(depositAmount)).build();

        final Account actualAccount = this.accountService.getAccount(createdAccount.getId());

        assertEquals(expectedAccount, actualAccount);

        assertGetAllAccounts(Lists.newArrayList(expectedAccount));
        assertGetAllTransactions(TransactionType.DEPOSIT, createdAccount.getId(), 1);
    }

    @Test
    public void depositExistingAccountWithMultipleThreadsWorkingInParallel() throws InterruptedException {
        final Account createdAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        executeInConcurrentEnv(() -> {
            final DepositWithdrawBalanceTransactionRequest request =
                    DataUtils.getDepositWithdrawRequest(createdAccount.getId(), BigDecimal.valueOf(1));
            this.accountService.accountDeposit(request);
            return null;
        }, 100);
        final Account expectedAccount = Account.builder(createdAccount).withBalance(createdAccount
                .getBalance().add(BigDecimal.valueOf(100))).build();

        final Account actualAccount = this.accountService.getAccount(createdAccount.getId());

        assertEquals(expectedAccount, actualAccount);

        assertGetAllAccounts(Lists.newArrayList(expectedAccount));
        assertGetAllTransactions(TransactionType.DEPOSIT, createdAccount.getId(), 100);
    }

    @Test(expected = AccountNotFoundException.class)
    public void withdrawNonExistingAccountTest() {
        assertGetAllAccounts(Lists.newArrayList());
        this.accountService.accountWithdraw(DataUtils.getDepositWithdrawRequest("not-id", BigDecimal.valueOf(100)));
        throw new RuntimeException("test must not come at this line");
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void withdrawExistingAccountInsufficientBalanceTest() {
        final Account createdAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        this.accountService
                .accountWithdraw(DataUtils.getDepositWithdrawRequest(createdAccount.getId(), BigDecimal.valueOf(1)));
        throw new RuntimeException("test must not come at this line");
    }

    @Test
    public void withdrawExistingAccountTest() {
        final Account createdAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));

        final BigDecimal withdrawAmount = BigDecimal.valueOf(99.5);
        this.accountService.accountWithdraw(DataUtils.getDepositWithdrawRequest(createdAccount.getId(), withdrawAmount));

        final Account expectedAccount = Account.builder(createdAccount).withBalance(createdAccount
                .getBalance().subtract(withdrawAmount)).build();

        final Account actualAccount = this.accountService.getAccount(createdAccount.getId());

        assertEquals(expectedAccount, actualAccount);

        assertGetAllAccounts(Lists.newArrayList(expectedAccount));
        assertGetAllTransactions(TransactionType.WITHDRAW, createdAccount.getId(), 1);
    }

    @Test
    public void withdrawExistingAccountWithMultipleThreadsWorkingInParallel() throws InterruptedException {
        final Account createdAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));

        executeInConcurrentEnv(() -> {
            final DepositWithdrawBalanceTransactionRequest request =
                    DataUtils.getDepositWithdrawRequest(createdAccount.getId(), BigDecimal.valueOf(1));
            this.accountService.accountWithdraw(request);
            return null;
        }, 100);
        final Account expectedAccount = Account.builder(createdAccount).withBalance(createdAccount
                .getBalance().subtract(BigDecimal.valueOf(100))).build();

        final Account actualAccount = this.accountService.getAccount(createdAccount.getId());

        assertEquals(expectedAccount, actualAccount);

        assertGetAllAccounts(Lists.newArrayList(expectedAccount));
        assertGetAllTransactions(TransactionType.WITHDRAW, createdAccount.getId(), 100);
    }

    @Test(expected = AccountNotFoundException.class)
    public void transferNonExistingSenderAccountTest() {
        assertGetAllAccounts(Lists.newArrayList());
        this.accountService
                .accountMoneyTransfer(DataUtils
                        .getTransferRequest("id", "2", BigDecimal.valueOf(1)));
        throw new RuntimeException("test must not come at this line");
    }


    @Test(expected = AccountNotFoundException.class)
    public void transferToNonExistingReceiverAccountTest() {
        final Account senderAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));
        this.accountService
                .accountMoneyTransfer(DataUtils
                        .getTransferRequest(senderAccount.getId(), "2", BigDecimal.valueOf(1)));
        throw new RuntimeException("test must not come at this line");
    }

    @Test(expected = InsufficientAccountBalanceException.class)
    public void transferFromExistingAccountSenderInsufficientBalanceTest() {
        final Account senderAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(1)));
        final Account receiverAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(1)));

        this.accountService
                .accountMoneyTransfer(DataUtils
                        .getTransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(2)));
        throw new RuntimeException("test must not come at this line");
    }

    @Test
    public void transferMoneyTest() {
        final Account senderAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));
        final Account receiverAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        final BigDecimal transferAmount = BigDecimal.valueOf(50);
        this.accountService
                .accountMoneyTransfer(DataUtils
                        .getTransferRequest(senderAccount.getId(), receiverAccount.getId(), transferAmount));

        final Account updatedSenderAccount = Account
                .builder(senderAccount)
                .withBalance(senderAccount.getBalance().subtract(transferAmount)).build();

        final Account updateReceiverAccount = Account
                .builder(receiverAccount)
                .withBalance(receiverAccount.getBalance().add(transferAmount)).build();

        assertGetAllAccounts(Lists.newArrayList(updatedSenderAccount, updateReceiverAccount));

        assertGetAllTransactions(TransactionType.WITHDRAW, senderAccount.getId(), 1);
        assertGetAllTransactions(TransactionType.DEPOSIT, receiverAccount.getId(), 1);
    }


    @Test
    public void transferMoneyTestWithMultipleThreadsWorkingInParallel() throws InterruptedException {
        final Account senderAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));
        final Account receiverAccount =
                this.accountService.createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        executeInConcurrentEnv(() -> {
            this.accountService
                    .accountMoneyTransfer(DataUtils
                            .getTransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(1)));
            return null;
        }, 100);

        final BigDecimal transferAmount = BigDecimal.valueOf(100);
        final Account updatedSenderAccount = Account
                .builder(senderAccount)
                .withBalance(senderAccount.getBalance().subtract(transferAmount)).build();

        final Account updateReceiverAccount = Account
                .builder(receiverAccount)
                .withBalance(receiverAccount.getBalance().add(transferAmount)).build();

        assertGetAllAccounts(Lists.newArrayList(updatedSenderAccount, updateReceiverAccount));

        assertGetAllTransactions(TransactionType.WITHDRAW, senderAccount.getId(), 100);
        assertGetAllTransactions(TransactionType.DEPOSIT, receiverAccount.getId(), 100);
    }


    private void executeInConcurrentEnv(final Callable<Void> callable, final int excutableNumber) throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(5);

        final List<Callable<Void>> callables = IntStream.range(0, excutableNumber)
                .mapToObj(i -> callable)
                .collect(Collectors.toList());

        executorService.invokeAll(callables);
    }


    private void assertGetAllAccounts(final Collection<Account> accounts) {
        final List<Account> expectedAccounts = this.accountService.getAllAccounts();
        assertThat(expectedAccounts, containsInAnyOrder(accounts.toArray()));
    }

    private void assertGetAllTransactions(final TransactionType type,
                                          final String accountId,
                                          final int expectedSize) {
        final long actualSize =
                this.accountService.getAllTransactions()
                        .stream()
                        .filter(transaction -> transaction.getType() == type &&
                                transaction.getAccountId().equals(accountId))
                        .count();
        assertEquals(expectedSize, actualSize);
    }
}
