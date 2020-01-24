package com.azouz.accountservice.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelExecutionUtil {

    public static void executeInConcurrentEnv(final Callable<Void> callable, final int excutableNumber) throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(5);

        final List<Callable<Void>> callables = IntStream.range(0, excutableNumber)
                .mapToObj(i -> callable)
                .collect(Collectors.toList());

        executorService.invokeAll(callables);
    }
}
