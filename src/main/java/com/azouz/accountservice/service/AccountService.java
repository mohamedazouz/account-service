package com.azouz.accountservice.service;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.rest.CreateAccountRequest;
import com.azouz.accountservice.domain.transaction.AccountMoneyTransferTransactionRequest;
import com.azouz.accountservice.domain.transaction.DepositWithdrawBalanceTransactionRequest;
import com.azouz.accountservice.domain.transaction.Transaction;
import com.azouz.accountservice.domain.transaction.TransactionType;
import com.azouz.accountservice.exception.AccountNotFoundException;
import com.azouz.accountservice.exception.InsufficientAccountBalanceException;
import com.azouz.accountservice.respository.account.AccountRepository;
import com.azouz.accountservice.respository.transaction.TransactionRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Inject
    public AccountService(final AccountRepository accountRepository, final TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(final CreateAccountRequest createAccountRequest) {
        final Account account = Account.builder(UUID.randomUUID().toString(), createAccountRequest).build();
        this.accountRepository.upsert(account);
        return account;
    }

    public Account getAccount(final String id) throws AccountNotFoundException {
        final Optional<Account> accountOpt = this.accountRepository.getById(id);
        if (!accountOpt.isPresent()) {
            throw new AccountNotFoundException("Account Not Found");
        }
        return accountOpt.get();
    }

    public List<Account> getAllAccounts() {
        return this.accountRepository.getAll();
    }

    public void accountDeposit(final DepositWithdrawBalanceTransactionRequest request) throws AccountNotFoundException {
        synchronized (this) {
            final Account account = this.getAccount(request.getAccountId());
            final BigDecimal newBalance = account.getBalance().add(request.getAmount());
            this.accountRepository.upsert(Account.builder(account).withBalance(newBalance).build());
        }
        this.createTransaction(request, TransactionType.DEPOSIT);
    }

    public void accountWithdraw(final DepositWithdrawBalanceTransactionRequest request) {
        synchronized (this) {
            final Account account = this.getAccount(request.getAccountId());
            final BigDecimal newBalance = account.getBalance().add(request.getAmount().negate());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientAccountBalanceException("Insufficient Account Balance Exception");
            }
            this.accountRepository.upsert(Account.builder(account).withBalance(newBalance).build());
        }
        this.createTransaction(request, TransactionType.WITHDRAW);
    }

    public void accountMoneyTransfer(final AccountMoneyTransferTransactionRequest
                                             accountMoneyTransferTransactionRequest) {
        synchronized (this) {
            this.accountWithdraw(new DepositWithdrawBalanceTransactionRequest(accountMoneyTransferTransactionRequest.getSenderAccountId(),
                    accountMoneyTransferTransactionRequest.getAmount()));
            this.accountDeposit(new DepositWithdrawBalanceTransactionRequest(accountMoneyTransferTransactionRequest.getReceiverAccountId(),
                    accountMoneyTransferTransactionRequest.getAmount()));
        }
    }


    private void createTransaction(final DepositWithdrawBalanceTransactionRequest request, final TransactionType type) {
        final Transaction transaction = Transaction.builder()
                .withAccountId(request.getAccountId())
                .withAmount(request.getAmount())
                .withType(type)
                .withTimestamp(AccountService.getCurrentTimestamp())
                .withId(UUID.randomUUID().toString())
                .build();
        this.transactionRepository.create(transaction);
    }

    public static long getCurrentTimestamp() {
        return DateTime.now(DateTimeZone.UTC).getMillis();
    }
}
