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

//    @Observed(name = "server.hello-world")
    public String helloWorld() {
        log.info("Logging on server - Hello World - with userId - {}", MDC.get("userId"));
        return "Hello World";
    }

    @NewSpan
    public void helloWorldNewSpan() {
        log.info("Logging on server - Hello World New Span - with userId - {}", MDC.get("userId"));
    }

    /**
     * THIS WILL RESULT IN NEW SPAN
     */
    @Async
    @NewSpan
    public void asyncNewSpanHelloWorld() {
        log.info("Logging on server - Hello World Async New Span -  with userId - {}", MDC.get("userId"));
    }

    /**
     * THIS WILL RESULT IN NEW SPAN - IRRESPECTIVE OF CONTINUE SPAN
     */
    @Async
    @ContinueSpan
    public void asyncContinueSpanHelloWorld() {
        log.info("Logging on server - Hello World Async Continue Span - with userId - {}", MDC.get("userId"));
    }


}
