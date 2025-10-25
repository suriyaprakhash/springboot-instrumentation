import com.github.javafaker.Faker;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class GatlingTestSimulation extends Simulation {

    private static final Faker FAKER = new Faker();


    public GatlingTestSimulation() {
//        setUp(
//                user.injectOpen(atOnceUsers(1050))
//        ).protocols(GatlingUtil.httpProtocol);
        setUp(
                user.injectOpen(
                        rampUsers(1000).during(Duration.ofSeconds(30))
                )
        ).protocols(GatlingUtil.httpProtocol);
    }

    ChainBuilder getAllInventory = exec(
            http("Get inventory item list")
                    .get("client/observe/baggage")
                    .queryParam("user", "#{user}")
                    .queryParam("tenant", "#{tenant}")
                    .check(status().is(200))
    );

//    Iterator<Map<String, Object>> userTenantFeeder =
//            Stream.generate(
//                            () -> {
//                                return Map.of(
//                                        "user", faker.name().username(),
//                                        "tenant", (Object) faker.company().name()
//                                );
//                            })
//                    .iterator();




    List<Map<String, Object>> userData = new ArrayList<>();
    int numUsers = 100;
    int numTenants = 10;

    {
        for (int i = 1; i <= numUsers; i++) {
            // Distribute 100 users across 10 tenants
            int tenantId = (i - 1) % numTenants + 1;
            userData.add(Map.of(
                    "user", i + "_" + FAKER.name().username(),
                    "tenant", tenantId + "_" + FAKER.company().name()));
        }
    }

    // Create the in-memory feeder with a circular strategy
    // The circular() strategy ensures the data is reused once exhausted
    FeederBuilder<Object> userTenantFeeder = listFeeder(userData).circular();

    ScenarioBuilder user = scenario("User")
            .feed(userTenantFeeder)
            .exec(getAllInventory);

}
