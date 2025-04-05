package com.suriyaprakhash.springboot_instrumentation;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * THIS DOES NOT WORK - USE ASPECT INSTEAD
 *
 * NOTE - THIS WILL NOT WORK SINCE - SPANS IN THIS CLASS WILL BE DIFFERENT FROM WHAT WOULD BE IN THE CONTROLLER METHOD
 * SO - THE USER_ID WILL NOT BE PROPAGATED
 */
@Slf4j
//@Component
//@RequiredArgsConstructor
public class BaggageInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    private final Tracer tracer;

    public BaggageInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Example: Extract a baggage value from a request header
        String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                Span span = tracer.currentSpan();
                assert span != null;
                try (BaggageInScope baggageInScope = tracer.createBaggageInScope(span.context(), "userId", userId)) {
                    // The baggage "user-id" with the extracted value is now in the current span context
                    log.info("user-id {} added to the trace {}", tracer.getBaggage("userId"), baggageInScope.name());
            }

        }

        return true; // Continue processing the request
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this); // Apply the interceptor to all requests (you can specify paths)
    }
}