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

import static com.azouz.accountservice.domain.transaction.TransactionType.DEPOSIT;
import static com.azouz.accountservice.domain.transaction.TransactionType.WITHDRAW;
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

    public synchronized void accountDeposit(final DepositWithdrawBalanceTransactionRequest request) throws AccountNotFoundException {
        final Account account = this.getAccount(request.getAccountId());
        this.addMoneyToAccount(account, request.getAmount());
    }

    public synchronized void accountWithdraw(final DepositWithdrawBalanceTransactionRequest request) {
        final String id = request.getAccountId();
        final Account account = this.getAccount(id);
        this.addMoneyToAccount(account, request.getAmount().negate());
    }

    public synchronized void accountMoneyTransfer(final AccountMoneyTransferTransactionRequest request) {
        final Account senderAccount = this.getAccount(request.getSenderAccountId());
        final Account receiverAccount = this.getAccount(request.getReceiverAccountId());

        this.addMoneyToAccount(senderAccount, request.getAmount().negate());
        this.addMoneyToAccount(receiverAccount, request.getAmount());
    }

    public List<Transaction> getAllTransactions() {
        return this.transactionRepository.getTransactions();
    }

    public void deleteAccount(final String id) {
        final Account account = getAccount(id);
        this.accountRepository.deleteAccount(account.getId());
    }

    private void addMoneyToAccount(final Account account, final BigDecimal amount) {
        synchronized (this) {
            final BigDecimal newBalance = account.getBalance().add(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientAccountBalanceException(
                        format("Insufficient Balance: Account Id: {0} has no enough balance", account.getId()));
            }
            this.accountRepository.upsert(Account.builder(account).withBalance(newBalance).build());
        }
        this.createTransaction(account, amount);
    }

    private void createTransaction(final Account request, final BigDecimal amount) {
        final TransactionType type = amount.compareTo(BigDecimal.ZERO) >= 0 ? DEPOSIT : WITHDRAW;
        final Transaction transaction = Transaction.builder()
                .withAccountId(request.getId())
                .withAmount(amount)
                .withType(type)
                .withTimestamp(AccountService.getCurrentTimestamp())
                .withId(idProvider.generateTransactionId())
                .build();
        this.transactionRepository.upsert(transaction);
    }

    private static long getCurrentTimestamp() {
        return DateTime.now(DateTimeZone.UTC).getMillis();
    }
}
