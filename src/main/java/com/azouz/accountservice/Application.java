package com.azouz.accountservice;

import com.azouz.accountservice.configuration.BeansConfig;
import com.azouz.accountservice.rest.AccountController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.jooby.Jooby;
import io.jooby.json.JacksonModule;

public class Application extends Jooby {

    {
        install(new JacksonModule(new ObjectMapper()));
        final Injector injector = Guice.createInjector(new BeansConfig());
        final AccountController accountController = injector.getInstance(AccountController.class);
        mvc(accountController);
    }

    public static void main(final String[] args) {
        Jooby.runApp(args, Application::new);
    }
}
