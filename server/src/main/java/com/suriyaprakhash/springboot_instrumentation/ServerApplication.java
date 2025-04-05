package com.suriyaprakhash.springboot_instrumentation;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.ContinueSpan;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@Slf4j
public class ServerApplication {

	private final Tracer tracer;

    public ServerApplication(Tracer tracer) {
        this.tracer = tracer;
    }

    public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@GetMapping
	public String helloWorldInstrumentation() {
		log.info("First instrumentation logging message");
		return "Hello World!";
	}

	@AddBaggage
	@GetMapping("/server")
	public String server() {
		// Note here the MDC contains the userId value
		log.info("Logging on server with userId - {}", MDC.get("userId"));
		return "Hello World!";
	}
}
