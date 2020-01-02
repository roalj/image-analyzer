package healthchecks;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import config.AppProperties;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class AmazonRekognitionCheck implements HealthCheck {
    @Inject
    private AppProperties properties;

    @Override
    public HealthCheckResponse call() {
        AWSCredentials credentials = null;
        AmazonRekognition rekognitionClient;

        try {
            credentials = new BasicAWSCredentials(
                    properties.getAccessKey(),
                    properties.getSecretKey());

            AmazonRekognitionClientBuilder
                    .standard()
                    .withRegion(Regions.EU_WEST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.down(AmazonRekognitionCheck.class.getSimpleName());
        }

        return HealthCheckResponse.up(AmazonRekognitionCheck.class.getSimpleName());
    }
}
