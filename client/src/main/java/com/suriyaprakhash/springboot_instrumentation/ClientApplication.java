package com.suriyaprakhash.springboot_instrumentation;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@SpringBootApplication
@Slf4j
public class ClientApplication {

	private RestClient restClient;

	public ClientApplication(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl("http://localhost:8081").build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	/**
	 * NOTE: THIS WILL NOT WORK - restClient needs to be wired as RestClient.Builder/RestTemplateBuilder/WebClient.Builder
	 * @return
	 */
	public RestClient myRestClient() {
		return RestClient.builder()
				.baseUrl("http://localhost:8080")
				.build();
	}

	@GetMapping
	public String helloWorldInstrumentation() {
		log.info("First instrumentation logging message");
		return "Hello World!";
	}

	@GetMapping("/client/trace/basic")
	public String client() {
		log.info("Logging on client");
		return restClient.get().uri("/server/trace/basic").retrieve().body(String.class);
	}

	@GetMapping("/client/trace")
	public String clientTrace(@RequestParam(name = "user", required = false, defaultValue = "default-user") String userId) {
		log.info("Logging on client");
		return restClient.get().uri("/server/trace")
				.header("X-User-Id", userId).retrieve().body(String.class);
	}

	@GetMapping("/client/observe")
	public String clientObserve(@RequestParam(name = "user", required = false, defaultValue = "default-user") String userId,
			@RequestParam(name = "tenant", required = false, defaultValue = "client-default-user") String tenantId) {
		try {
			log.info("Logging on client");
			return restClient.get().uri("/server/observe")
					.header("X-User-Id", userId)
					.header("X-Tenant-Id", tenantId)
					.retrieve().body(String.class);
		} catch (Exception e) {
			log.error("Error while calling server", e);
		}
		return "Something went wrong. Reach out to the admin using the this traceId - " + MDC.get("traceId");
	}

}
