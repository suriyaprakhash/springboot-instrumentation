# springboot-instrumentation

The global visualization tool used here is [grafana running in docker](https://github.com/suriyaprakhash/docker-collection/tree/master/services/grafana)

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


Navigate to http://localhost:9090 (not working - need to find why)
Navigate to http://localhost:3000 - grafana to view prometheus metrics