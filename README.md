# springboot-instrumentation

The global visualization tool used here is [grafana running in docker](https://github.com/suriyaprakhash/docker-collection/tree/master/services/grafana)

## Run

- Run the [Grafana docker compose](https://github.com/suriyaprakhash/docker-collection/blob/master/services/grafana/docker-compose.yml)
- Run the ClientApplication and ServerApplication
- Navigate to http://localhost:8080/swagger-ui/index.html

## Logs

### Loki

Refer here for the [loki configuration](https://loki4j.github.io/loki-logback-appender/docs/configuration)

- Needs the **loki dependency**
- Needs the **loki-logback-appender**
- Need the loki config **logback-spring.xml** file
    - Format/Pattern
    - Labels
    - HTTP url and cred to ship

Navigate to Grafana Loki on http://localhost:3000 to visualize

## Tracing

### Zipkin-brave

Zipkin is the data backend and Brave is the tracing instrumentation lib

- Needs the **micrometer-tracing-bridge-brave dependency** for converting micrometer instrument to brave
- Needs the **zipkin-reporter-brave** for send the data to zipkin
- Needs the app config - management prop for pointing to the zipkin server
- Need the client to call as RestClient.Builder/RestTemplateBuilder/WebClient.Builder to work - auto instrument

Navigate to ZipKin on http://localhost:9411 to visualize
Navigate to Grafana Loki on http://localhost:3000 to visualize

### Zipkin-otel

- Needs the **micrometer-tracing-bridge-otel dependency** for converting micrometer instrument to brave
- Needs the **opentelemetry-exporter-zipkin** for send the data to zipkin

## Metrics

- Need the **micrometer-registry-prometheus** for micrometer to convert metrics to prometheus format - so it can scrape them
- **micrometer-registry-otlp** - is optional only needed when sending the logs, traces and metrics via otel-collector (separate service)
- Needs the app config - management prop for enabling metrics and promethus endpoints
- The promethes needs the following config from docker-compose
```
global:
  scrape_interval: 10s
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['host.docker.internal:8080', 'host.docker.internal:8081'] # Replace with your application's host and port
```

Navigate to http://localhost:9090 (not working - need to find why)
Navigate to http://localhost:3000 - grafana to view prometheus metrics


## Takeaway

### Trace

- Baggage is not getting copied over - by default
  - We cannot set the userId baggage in interceptor because, the interceptor will have different Span, and when it comes to controller method - it will be in differnt span - however within the same trace
  - Need to add **AddBaggage** annotation and manually copy and have the controller method execute in the scope try block
  - Strangely, it only works for **micrometer-tracing-bridge-otel** and not for **micrometer-tracing-bridge-brave**
- @Async thread - @NewSpan and @ContinueSpan dose not work by default - however it works ok for calling different methods within the main thread
  - @Async needs custom ExecutorTaskPool defined and the context copied over
    - Note - the above isn't tracable with using @ContinueSpan in the Zipkin grafana
    - Also the baggage isn't getting carried to async thread

### Observe

- Add custom metrics - http://localhost:8081/actuator/metrics/service.handler-svc.observe.custom?tag=tenantId:aws
