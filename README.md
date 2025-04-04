# springboot-instrumentation

The global visualization tool used here is [grafana running in docker]()

## Logs

### Loki

Refer here for the [loki configuration](https://loki4j.github.io/loki-logback-appender/docs/configuration)

- Needs the **loki dependency**
- Needs the **loki-logback-appender**
- Need the loki config **logback-spring.xml** file
    - Format/Pattern
    - Labels
    - HTTP to ship

Navigate to Grafana Loki on http://localhost:3000 to visualize

## Tracing

### Zipkin-brave

Zipkin is the data backend and Brave is the tracing instrumentation lib

- Needs the **micrometer-tracing-bridge-brave dependency** for converting micrometer instrument to brave
- Needs the **zipkin-reporter-brave** for send the data to zipkin
- Needs the app config - management prop for pointing to the zipkin server

Navigate to ZipKin on http://localhost:9411 to visualize
Navigate to Grafana Loki on http://localhost:3000 to visualize

### xxx-otel

## Metrics

- Need the **micrometer-registry-prometheus** for micrometer to convert metrics to prometheus format - so it can scrape them
- **micrometer-registry-otlp** - is optional only needed when sending the logs, traces and metrics via otel-collector (separate service)
- Needs the app config - management prop for enabling metrics and promethus endpoints
