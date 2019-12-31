package clients;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import config.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AmazonRekognitionClient {

    @Inject
    private AppProperties appProperties;

    private AmazonRekognition rekognitionClient;

    @PostConstruct
    private void init() {

        AWSCredentials credentials = null;

        try {
            credentials = new BasicAWSCredentials(
                    appProperties.getAccessKey(),
                    appProperties.getSecretKey());

        } catch (Exception e) {
            throw new AmazonClientException("Cannot initialise the credentials.", e);
        }

        rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    public Integer countFaces(byte[] imageBytes) {

        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        DetectFacesRequest detectFacesRequest =
                new DetectFacesRequest().withImage(image);

        DetectFacesResult detectFacesResult = rekognitionClient.detectFaces(detectFacesRequest);

        return detectFacesResult.getFaceDetails().size();

    }

    public List<String> checkForCelebrities(byte[] imageBytes) {

        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        RecognizeCelebritiesRequest recognizeCelebritiesRequest =
                new RecognizeCelebritiesRequest().withImage(image);


        RecognizeCelebritiesResult recognizeCelebritiesResult =
                rekognitionClient.recognizeCelebrities(recognizeCelebritiesRequest);

        return recognizeCelebritiesResult.getCelebrityFaces()
                .stream()
                .map(celebrity -> celebrity.getName())
                .collect(Collectors.toList());

    }

    public List<Label> sceneDetection(byte[] imageBytes) {
        Image image = new Image().withBytes(ByteBuffer.wrap(imageBytes));

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(image)
                .withMaxLabels(10).withMinConfidence(75F);

        DetectLabelsResult result = rekognitionClient.detectLabels(request);

        return result.getLabels();
    }

}
