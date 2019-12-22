package producer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import config.AppProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class MongoDBProducer {

    @Inject
    private AppProperties appProperties;

    @Produces
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(appProperties.getMongoDBURL()));
    }
}
