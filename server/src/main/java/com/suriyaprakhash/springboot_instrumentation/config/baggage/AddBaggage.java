package com.suriyaprakhash.springboot_instrumentation.config.baggage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this addBaggage annotation where you'd need to attach the baggage to MDC
 */
@Target(ElementType.METHOD) // This annotation can only be applied to methods
@Retention(RetentionPolicy.RUNTIME) // The annotation will be available at runtime
public @interface AddBaggage {
    boolean enabled() default true;
}