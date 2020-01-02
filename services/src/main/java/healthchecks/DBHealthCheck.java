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

    private static final Logger LOG = Logger.getLogger(DBHealthCheck.class.getName());

    @Inject
    private AppProperties properties;

    private String url;

    @PostConstruct
    private void init() {
        url = properties.getMongoDBURL();
    }

    @Override
    public HealthCheckResponse call() {
        MongoClient mongoClient = null;
        LOG.info("MONGODB URL: " + url);

        try {
            MongoClientURI uri = new MongoClientURI(url);
            mongoClient = new MongoClient(uri);

            if (databaseExist(mongoClient, uri.getDatabase())) {
                return HealthCheckResponse.up(DBHealthCheck.class.getSimpleName());
            } else {
                LOG.severe("NO DB FOUND");
                return HealthCheckResponse.down(DBHealthCheck.class.getSimpleName());
            }
        } catch (Exception e) {
            LOG.severe("NO connection established");
            return HealthCheckResponse.down(DBHealthCheck.class.getSimpleName());
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    private Boolean databaseExist(MongoClient mongoClient, String databaseName) {

        if (mongoClient != null && databaseName != null) {

            for (String s : mongoClient.listDatabaseNames()) {
                if (s.equals(databaseName))
                    return true;
            }
        }

        return false;
    }
}
