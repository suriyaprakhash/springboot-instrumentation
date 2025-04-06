package com.suriyaprakhash.springboot_instrumentation;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.ContinueSpan;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandlerService {

    // by default its ContinueSpan
    public String helloWorld() {
        log.info("Hello World - with userId - {}", MDC.get("userId"));
        return "Hello World";
    }

    @NewSpan
    public void helloWorldNewSpan() {
        log.info("Hello World - New Span - with userId - {}", MDC.get("userId"));
    }

    @Async
    @NewSpan
    public void asyncNewSpanHelloWorld() {
        log.info("Hello World - Async New Span -  with userId - {}", MDC.get("userId"));
    }

    @Async
    @ContinueSpan
    public void asyncContinueSpanHelloWorld() {
        log.info("Hello World - Async Continue Span - with userId - {}", MDC.get("userId"));
    }


    /// OBSERVE ///

    @Observed(name = "observe.service.method",
            contextualName = "service-observe-method")
    public void observeServiceMethod() {
        // Method logic
        log.info("ObserveServiceMethod - with userId - {}", MDC.get("userId"));
    }

}
