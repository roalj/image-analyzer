package config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("app-properties")
@ApplicationScoped
public class AppProperties {

    @ConfigValue(value = "rekognition.access-key", watch = true)
    private String accessKey;

    @ConfigValue(value = "rekognition.secret-key", watch = true)
    private String secretKey;

    @ConfigValue(value = "image-analyzer.mongodb-url", watch = true)
    private String mongoDBURL;

    public String getMongoDBURL() {
        return mongoDBURL;
    }

    public void setMongoDBURL(String mongoDBURL) {
        this.mongoDBURL = mongoDBURL;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;

    }
}
