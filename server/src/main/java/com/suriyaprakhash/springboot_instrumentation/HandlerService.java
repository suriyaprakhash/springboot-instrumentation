package com.suriyaprakhash.springboot_instrumentation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
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

    private final ObservationRegistry observationRegistry;

    public HandlerService(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public String helloWorldBasic() {
        log.info("Hello World Basic");
        return "Hello World";
    }

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

    @Observed(name = "observe.service.basic-method",
            contextualName = "service-observe-basic-method")
    public void observeServiceMethod() {
        log.info("ObserveServiceMethod - with userId - {}", MDC.get("userId"));
    }


    public void observeFineControlServiceMethod(String tenantId) {
        String data = Observation.createNotStarted("observe.service.fine-control-method", this.observationRegistry)
                .contextualName("service-observe-fine-ctrl-method")
                .lowCardinalityKeyValue("tenantId", tenantId)
                .highCardinalityKeyValue("user.id", MDC.get("userId"))
                .observe(() -> {
                    log.info("ObserveServiceMethod - fine controlled with userId - {}", MDC.get("userId"));
                    return "Processed: " ;
                });
        log.info("ObserveServiceMethod(outside) - fine controlled with userId - {}", MDC.get("userId"));
    }

}
