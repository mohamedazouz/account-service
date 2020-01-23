package com.azouz.accountservice;

import com.azouz.accountservice.configuration.BeansConfig;
import com.azouz.accountservice.middleware.ErrorMiddleware;
import com.azouz.accountservice.middleware.ValidationMiddleware;
import com.azouz.accountservice.rest.AccountController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.jooby.Jooby;
import io.jooby.MediaType;
import io.jooby.json.JacksonModule;

public class Application extends Jooby {

    {
        final Injector injector = Guice.createInjector(new BeansConfig());

        install(new JacksonModule());

        decoder(MediaType.json, injector.getInstance(ValidationMiddleware.class));

        error(injector.getInstance(ErrorMiddleware.class));

        mvc(injector.getInstance(AccountController.class));
    }


    public static void main(final String[] args) {
        Jooby.runApp(args, Application::new);
    }
}
