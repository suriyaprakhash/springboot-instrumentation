package com.suriyaprakhash.springboot_instrumentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@SpringBootApplication
@Slf4j
public class SpringbootInstrumentationApplication {

	private RestClient restClient;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootInstrumentationApplication.class, args);
	}

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

	@GetMapping("/client")
	public String client() {
		log.info("Logging on client");
		return myRestClient().get().uri("/server").retrieve().body(String.class);
	}

	@GetMapping("/server")
	public String server() {
		log.info("Logging on server");
		return "Hello World!";
	}
}
