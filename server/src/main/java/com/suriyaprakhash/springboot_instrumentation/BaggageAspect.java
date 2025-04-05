package com.suriyaprakhash.springboot_instrumentation;


import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@Slf4j
public class BaggageAspect {

    private final Tracer tracer;

    public BaggageAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("@annotation(addBaggage)")
    public Object addBaggage(ProceedingJoinPoint joinPoint, AddBaggage addBaggage) throws Throwable {
        Object result = null;
        String userId = "default-user";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            userId = request.getHeader("X-User-Id");
            log.debug("Intercepted method: {}", joinPoint.getSignature().toShortString());
            log.debug("X-User-Id: {}", userId);
        } else {
            log.warn("Not in a web request context, cannot access HTTP headers.");
        }
        Span span = tracer.currentSpan();
        assert span != null;
        try (BaggageInScope baggageInScope = tracer.createBaggageInScope(span.context(), "userId", userId)) {
            // The baggage "user-id" with the extracted value is now in the current span context
            log.info("user-id {} added to the trace {}", tracer.getBaggage("userId"), baggageInScope.name());
            result = joinPoint.proceed();
        }
        return result;
    }
}
