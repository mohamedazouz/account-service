package com.azouz.accountservice.rest;

import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.rest.CreateAccountRequest;
import com.azouz.accountservice.domain.rest.DepositWithdrawBalanceHttpRequest;
import com.azouz.accountservice.domain.rest.TransferHttpRequest;
import com.azouz.accountservice.domain.transaction.AccountMoneyTransferTransactionRequest;
import com.azouz.accountservice.domain.transaction.DepositWithdrawBalanceTransactionRequest;
import com.azouz.accountservice.service.AccountService;
import io.jooby.annotations.GET;
import io.jooby.annotations.POST;
import io.jooby.annotations.Path;
import io.jooby.annotations.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Path("/v1/accounts")
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Inject
    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    public Account createAccount(@Valid final CreateAccountRequest createAccountRequest) {
        return this.accountService.createAccount(createAccountRequest);
    }

    @GET
    public List<Account> getAll() {
        return this.accountService.getAllAccounts();
    }

    @POST
    @Path("/{id}/deposits")
    public void deposit(@PathParam final String id,
                        final DepositWithdrawBalanceHttpRequest balanceHttpRequest) {
        this.accountService.accountDeposit(new DepositWithdrawBalanceTransactionRequest(id, balanceHttpRequest));
    }

    @POST
    @Path("/{id}/withdraws")
    public void withdraw(@PathParam final String id, final DepositWithdrawBalanceHttpRequest balanceHttpRequest) {
        this.accountService.accountWithdraw(new DepositWithdrawBalanceTransactionRequest(id, balanceHttpRequest));
    }

    @POST
    @Path("/{id}/transfers")
    public void transfer(@PathParam final String id, final TransferHttpRequest transferHttpRequest) {
        this.accountService.accountMoneyTransfer(new AccountMoneyTransferTransactionRequest(id, transferHttpRequest));
    }
}
