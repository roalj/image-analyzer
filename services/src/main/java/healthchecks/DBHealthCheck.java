package healthchecks;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import config.AppProperties;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

@Readiness
@ApplicationScoped
public class DBHealthCheck implements HealthCheck {

    @Inject
    private AppProperties properties;

    private String url;

    @PostConstruct
    private void init() {
        url = properties.getMongoDBURL();
    }

    @Override
    public HealthCheckResponse call() {
        MongoClientOptions.Builder o = MongoClientOptions.builder().connectTimeout(3000);
        MongoClientURI uri = new MongoClientURI(url);
        MongoClient mongoClient = new MongoClient(uri);

        try {
            mongoClient.listDatabases();
        } catch (Exception e) {
            System.out.println("Mongo is down");
            mongoClient.close();
            return HealthCheckResponse.down(DBHealthCheck.class.getSimpleName());
        }

        return HealthCheckResponse.up(DBHealthCheck.class.getSimpleName());
    }
}
