package com.suriyaprakhash.springboot_instrumentation;

import com.suriyaprakhash.springboot_instrumentation.config.baggage.AddBaggage;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@EnableAsync
@RestController
@SpringBootApplication
@Slf4j
public class ServerApplication {

	private final HandlerService handlerService;

    public ServerApplication(HandlerService handlerService) {
        this.handlerService = handlerService;
    }

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@GetMapping
	public String helloWorldInstrumentation() {
		log.info("First instrumentation logging message");
		return "Hello World!";
	}

	@GetMapping("/server/trace/basic")
	public String traceBasic() {
		// Note here the MDC contains the userId value
		log.info("Logging on server - trace basic");
		String res = handlerService.helloWorldBasic();
		return res;
	}

	@AddBaggage
	@GetMapping("/server/trace")
	public String trace() {
		// Note here the MDC contains the userId value
		log.info("Logging on server - trace - with userId - {}", MDC.get("userId"));
		String res = handlerService.helloWorld();
		handlerService.helloWorldNewSpan();
		handlerService.asyncNewSpanHelloWorld();
		handlerService.asyncContinueSpanHelloWorld();
		return res;
	}

	@AddBaggage
	@GetMapping("/server/observe")
	@Observed(name = "observe.controller.method",
			contextualName = "controller-observe-method")
	public String observe(@RequestHeader(name = "X-Tenant-Id") String tenantId) {
		// Note here the MDC contains the userId value
		log.info("Logging on server - observe - with userId - {}", MDC.get("userId"));
		handlerService.observeServiceMethod();
		handlerService.observeFineControlServiceMethod(tenantId);
		return "Hello World! Observe";
	}
}
