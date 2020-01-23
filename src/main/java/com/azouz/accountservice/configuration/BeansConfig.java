package com.azouz.accountservice.configuration;

import com.azouz.accountservice.respository.account.AccountRepository;
import com.azouz.accountservice.respository.account.InMemoryAccountRepository;
import com.azouz.accountservice.respository.transaction.InMemoryTransactionRepository;
import com.azouz.accountservice.respository.transaction.TransactionRepository;
import com.azouz.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class BeansConfig extends AbstractModule {

    @Override
    protected void configure() {

        bind(AccountRepository.class).to(InMemoryAccountRepository.class);
        bind(TransactionRepository.class).to(InMemoryTransactionRepository.class);
        bind(AccountService.class);
    }

    @Provides
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
