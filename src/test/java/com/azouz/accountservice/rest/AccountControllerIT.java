package com.azouz.accountservice.rest;

import com.azouz.accountservice.Application;
import com.azouz.accountservice.domain.Account;
import com.azouz.accountservice.domain.rest.CreateAccountRequest;
import com.azouz.accountservice.domain.rest.TransferHttpRequest;
import com.azouz.accountservice.utils.DataUtils;
import com.azouz.accountservice.utils.ParallelExecutionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.jooby.JoobyTest;
import io.jooby.StatusCode;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;


@JoobyTest(Application.class)
public class AccountControllerIT {
    private final String HOST = "http://localhost:8911";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;


    @Inject
    public AccountControllerIT() {
        client = new OkHttpClient();
        objectMapper = objectMapper();
    }

    @Test
    public void notFoundURLExpect404() throws IOException {
        final Request req = new Request.Builder()
                .url(getURL("random/path"))
                .build();
        final Response rsp = client.newCall(req).execute();
        assertEquals(StatusCode.NOT_FOUND_CODE, rsp.code());
    }

    @Test
    public void createARequestWithoutValidMediaType() throws IOException {
        final MediaType JSON = MediaType.parse("invalid/MediaType;");
        final RequestBody body = RequestBody.create(JSON, "");
        final Request req = new Request.Builder()
                .url(getURL("v1/accounts"))
                .post(body)
                .build();
        final Response rsp = client.newCall(req).execute();
        assertEquals(StatusCode.UNSUPPORTED_MEDIA_TYPE_CODE, rsp.code());
    }

    @Test
    public void createAccountWithinValidRequestExcept400() throws IOException {
        final Response rsp = createPostRequest("v1/accounts", new CreateAccountRequest());
        assertEquals(StatusCode.BAD_REQUEST_CODE, rsp.code());
    }

    @Test
    public void createAccountWithValidRequest() throws IOException {
        final Account createResponseAccount = createAccount(DataUtils.getDummyCreateAccountRequest());
        assertGetAllAccounts(Lists.newArrayList(createResponseAccount));
        deleteAccounts(Lists.newArrayList(createResponseAccount));
    }

    @Test
    public void transferBetween2ExistingAccount() throws IOException {
        final Account senderAccount = createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));
        final Account receiverAccount = createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        final BigDecimal transferAmount = BigDecimal.valueOf(70);
        transferMoney(senderAccount.getId(), new TransferHttpRequest(receiverAccount.getId(), transferAmount));

        final Account updatedSenderAccount = Account
                .builder(senderAccount)
                .withBalance(senderAccount.getBalance().subtract(transferAmount)).build();

        final Account updateReceiverAccount = Account
                .builder(receiverAccount)
                .withBalance(receiverAccount.getBalance().add(transferAmount)).build();

        assertGetAllAccounts(Lists.newArrayList(updatedSenderAccount, updateReceiverAccount));
        deleteAccounts(Lists.newArrayList(senderAccount, receiverAccount));
    }


    @Test
    public void transferMoneyTestWithMultipleThreadsWorkingInParallel() throws IOException, InterruptedException {
        final Account senderAccount = createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(100)));
        final Account receiverAccount = createAccount(DataUtils.getDummyCreateAccountRequest(BigDecimal.valueOf(0)));

        ParallelExecutionUtil.executeInConcurrentEnv(() -> {
            transferMoney(senderAccount.getId(), new TransferHttpRequest(receiverAccount.getId(), BigDecimal.valueOf(1)));
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
        deleteAccounts(Lists.newArrayList(senderAccount, receiverAccount));
    }

    private Account createAccount(final CreateAccountRequest createAccountRequest) throws IOException {
        final Response createResponse = createPostRequest("v1/accounts", createAccountRequest);
        assertEquals(StatusCode.OK_CODE, createResponse.code());

        return toObject(createResponse.body().string(), new TypeReference<Account>() {
        });
    }

    private void deleteAccounts(final List<Account> accounts) throws IOException {
        for (final Account account : accounts) {
            deleteAccount(account);
        }
    }

    private void deleteAccount(final Account account) throws IOException {
        final Request req = new Request.Builder()
                .url(getURL(MessageFormat.format("v1/accounts/{0}", account.getId())))
                .delete()
                .build();
        final Response deleteResponse = client.newCall(req).execute();
        assertEquals(StatusCode.NO_CONTENT_CODE, deleteResponse.code());
    }


    private void transferMoney(final String senderAccountId, final TransferHttpRequest request) throws IOException {
        final Response createResponse =
                createPostRequest(MessageFormat.format("v1/accounts/{0}/transfers", senderAccountId), request);
        assertEquals(StatusCode.NO_CONTENT_CODE, createResponse.code());
    }

    private void assertGetAllAccounts(final Collection<Account> accounts) throws IOException {
        final Response allAccountsResponse = createGetRequest("v1/accounts");
        assertEquals(StatusCode.OK_CODE, allAccountsResponse.code());

        final List<Account> expectedAccounts = toObject(allAccountsResponse.body().string(), new TypeReference<List<Account>>() {
        });

        assertThat(expectedAccounts, containsInAnyOrder(accounts.toArray()));
    }

    private String getURL(final String path) {
        return MessageFormat.format("{0}/{1}", HOST, path);
    }

    private <T> Response createPostRequest(final String path, final T value) throws IOException {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final RequestBody body = RequestBody.create(JSON, toJsonString(value));
        final Request req = new Request.Builder()
                .url(getURL(path))
                .post(body)
                .build();
        return client.newCall(req).execute();
    }

    private Response createGetRequest(final String path) throws IOException {
        final Request req = new Request.Builder()
                .url(getURL(path))
                .get()
                .build();
        return client.newCall(req).execute();
    }

    private ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private <T> String toJsonString(final T value) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(value);
    }

    private <T> T toObject(final String jsonString, final TypeReference<T> reference) throws JsonProcessingException {
        return this.objectMapper.readValue(jsonString, reference);
    }
}
