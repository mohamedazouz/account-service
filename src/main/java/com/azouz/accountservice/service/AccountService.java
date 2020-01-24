package com.azouz.accountservice.service;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.id.IdProvider;
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

import static java.text.MessageFormat.format;

@Singleton
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final IdProvider idProvider;

    @Inject
    public AccountService(final AccountRepository accountRepository,
                          final TransactionRepository transactionRepository,
                          final IdProvider idProvider) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.idProvider = idProvider;
    }

    public Account createAccount(final CreateAccountRequest createAccountRequest) {
        final Account account = Account.builder(idProvider.generateAccountId(), createAccountRequest).build();
        this.accountRepository.upsert(account);
        return account;
    }

    public Account getAccount(final String id) throws AccountNotFoundException {
        final Optional<Account> accountOpt = this.accountRepository.getAccount(id);
        if (!accountOpt.isPresent()) {
            throw new AccountNotFoundException(format("Account with Id: {0} is not found", id));
        }
        return accountOpt.get();
    }

    public List<Account> getAllAccounts() {
        return this.accountRepository.getAccounts();
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
            final String id = request.getAccountId();
            final Account account = this.getAccount(id);
            final BigDecimal newBalance = account.getBalance().add(request.getAmount().negate());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientAccountBalanceException(
                        format("Insufficient Balance: Account Id: {0} has no enough balance", id));
            }
            this.accountRepository.upsert(Account.builder(account).withBalance(newBalance).build());
        }
        this.createTransaction(request, TransactionType.WITHDRAW);
    }

    public synchronized void accountMoneyTransfer(final AccountMoneyTransferTransactionRequest
                                                          accountMoneyTransferTransactionRequest) {
        this.accountWithdraw(new DepositWithdrawBalanceTransactionRequest(
                accountMoneyTransferTransactionRequest.getSenderAccountId(),
                accountMoneyTransferTransactionRequest.getAmount()));

        this.accountDeposit(new DepositWithdrawBalanceTransactionRequest(
                accountMoneyTransferTransactionRequest.getReceiverAccountId(),
                accountMoneyTransferTransactionRequest.getAmount()));
    }

    public List<Transaction> getAllTransactions() {
        return this.transactionRepository.getTransactions();
    }

    private void createTransaction(final DepositWithdrawBalanceTransactionRequest request, final TransactionType type) {
        final Transaction transaction = Transaction.builder()
                .withAccountId(request.getAccountId())
                .withAmount(request.getAmount())
                .withType(type)
                .withTimestamp(AccountService.getCurrentTimestamp())
                .withId(UUID.randomUUID().toString())
                .build();
        this.transactionRepository.upsert(transaction);
    }

    public static long getCurrentTimestamp() {
        return DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public void deleteAccount(final String id) {
        final Account account = getAccount(id);
        this.accountRepository.deleteAccount(account.getId());
    }
}
