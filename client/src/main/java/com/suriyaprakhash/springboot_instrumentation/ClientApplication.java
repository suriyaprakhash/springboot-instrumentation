package com.suriyaprakhash.springboot_instrumentation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Random;

@RestController
@SpringBootApplication
@Slf4j
public class ClientApplication {

    private static final Random random = new Random();
    private final RestClient restClient;
    private final ObservationRegistry observationRegistry;

    public ClientApplication(RestClient.Builder restClientBuilder, ObservationRegistry observationRegistry) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:8081").build();
        this.observationRegistry = observationRegistry;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    /**
     * NOTE: THIS WILL NOT WORK - restClient needs to be wired as RestClient.Builder/RestTemplateBuilder/WebClient.Builder
     *
     * @return
     */
    public RestClient myRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    /**
     * Demonstrates the basic logging - to be transmitted to LOKI
     *
     * @return
     */
    @Operation(summary = "Step 1", description = "Demonstrates the basic logging - to be transmitted to LOKI")
    @GetMapping
    public String helloWorldInstrumentation() {
        log.info("First instrumentation logging message");
        return "Hello World!";
    }

    /**
     * Demonstrates tracing - calls the Server endpoint - transmits the traceId to the server app, also transmits traces to ZipKin.
     *
     * @return
     */
    @Operation(summary = "Step 2",
            description = """
                        Demonstrates tracing - calls the Server endpoint - transmits the traceId to the server app, 
                        also transmits traces to ZipKin.
                    """)
    @GetMapping("/client/trace/basic")
    public String client() {
        log.info("Logging on client");
        return restClient.get().uri("/server/trace/basic").retrieve().body(String.class);
    }

    /**
     * Demonstrates passing a custom UserId as part of header, and on the server app using a custom annotation to unwrap the baggage
     *
     * @param userId
     * @return
     */
    @Operation(summary = "Step 3",
            description = """
                    Demonstrates passing a custom UserId as part of header,
                     and on the server app using a custom annotation to unwrap the baggage
                    """)
    @GetMapping("/client/trace")
    public String clientTrace(@RequestParam(name = "user", required = false, defaultValue = "default-user") String userId) {
        log.info("Logging on client");
        return restClient.get().uri("/server/trace")
                .header("X-User-Id", userId).retrieve().body(String.class);
    }


    /**
     * Demonstrates Observe - along with the userId passes lowCardinality tenantId and the server sets them as span baggage, establishes new metric to prometheus
     *
     * @param userId
     * @param tenantId
     * @return
     */
    @Operation(summary = "Step 4",
            description = """
                    Demonstrates Observe - along with the userId passes lowCardinality tenantId and the server
                     sets them as span baggage, establishes new metric to prometheus
                    """)
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


    /**
     * Demonstrates Observe - passes userId in the header and tenantId as the baggage - server reads the userId from the header as same,
     * and the tenantId is read from the trace.getBaggage("tenantId") instead of using custom annotation
     *
     * @param userId
     * @param tenantId
     * @return
     */
    @Operation(summary = "Step 5",
            description = """ 
                    Demonstrates Observe - passes userId in the header and tenantId as the baggage - server reads the userId from the header as same, 
                     and the tenantId is read from the trace.getBaggage(\"tenantId\") instead of using custom annotation
                    """)
    @GetMapping("/client/observe/baggage")
    public String clientObserveWithBaggage(@RequestParam(name = "user", required = false, defaultValue = "default-user") String userId,
                                           @RequestParam(name = "tenant", required = false, defaultValue = "client-default-user") String tenantId) {
        try {
            log.info("Logging on client");
            String data = Observation.createNotStarted("client.observe.baggage", this.observationRegistry)
                    .contextualName("client-observe-with-baggage")
                    .lowCardinalityKeyValue("tenantId", tenantId) // this will be part of the baggage
                    .observe(() -> restClient.get().uri("/server/observe/baggage")
                            .header("X-User-Id", userId)
                            .retrieve().body(String.class));
            log.info("Returned String {}", data);
            return data;
        } catch (Exception e) {
            log.error("Error while calling server", e);
        }
        return "Something went wrong. Reach out to the admin using the this traceId - " + MDC.get("traceId");
    }

}
