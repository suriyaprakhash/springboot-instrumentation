package com.suriyaprakhash.springboot_instrumentation.config.async;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
public class TracingAsyncConfiguration implements AsyncConfigurer {

    private final ThreadPoolTaskExecutor myAsyncExecutor;

    public TracingAsyncConfiguration(ThreadPoolTaskExecutor myAsyncExecutor) {
        this.myAsyncExecutor = myAsyncExecutor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return ContextExecutorService.wrap(myAsyncExecutor.getThreadPoolExecutor(), ContextSnapshotFactory.builder().build()::captureAll);
    }

}