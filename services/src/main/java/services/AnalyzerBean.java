package services;

import clients.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Label;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.AnalysisEntity;
import org.apache.commons.codec.binary.Base64;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class AnalyzerBean {
    private Logger log = Logger.getLogger(AnalyzerBean.class.getName());

    private Client httpClient;

    @Inject
    @DiscoverService(value = "image-catalog-services", environment = "dev", version = "1.0.0")
    private Optional<String> baseUrl;

    @Inject
    @DiscoverService(value = "image-filter-service", environment = "dev", version = "1.0.0")
    private Optional<String> imageFilterBaseUrl;

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    @Inject
    private MongoClient mongoClient;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public AnalysisEntity analyze(Integer imageId) {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");


        if(getAnalysis(imageId) == null) {
            AnalysisEntity analysisEntity = new AnalysisEntity();

            byte[] imageBytes = getImageBytes(imageId);

            analysisEntity.setImageId(imageId);
            analysisEntity.setCelebrities(getCelebrities(imageBytes));
            analysisEntity.setNumberOfFaces(getNumberOfFaces(imageBytes));
            analysisEntity.setScenes(getScenes(imageBytes));

            Document scenes = new Document();
            for (Label scene : analysisEntity.getScenes()) {
                scenes.append(scene.getName(), scene.getConfidence());
            }
            Document analysis = new Document("imageId", imageId)
                    .append("numberOfFaces", analysisEntity.getNumberOfFaces())
                    .append("numberOfCelebrities", analysisEntity.getCelebrities().size())
                    .append("celebrities", analysisEntity.getCelebrities())
                    .append("scenes", scenes);

            collection.insertOne(analysis);

            return analysisEntity;
        }
        return getAnalysis(imageId);
    }

    public AnalysisEntity getAnalysis(int imageId) {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");
        FindIterable<Document> iterDoc = collection.find();
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            Document doc = (Document) it.next();
            if (doc.getInteger("imageId") == imageId) {
                return getAnalysisEntity(doc);
            }
        }
        return null;
    }

    public boolean removeDocument(int imageId) {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");
        FindIterable<Document> iterDoc = collection.find();
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            Document doc = (Document) it.next();
            if (doc.getInteger("imageId") == imageId) {
                collection.deleteOne(doc);
                return true;
            }
        }
        return false;
    }

    public List<AnalysisEntity> getAll() {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");
        FindIterable<Document> iterDoc = collection.find();
        List<AnalysisEntity> analysis = new ArrayList<>();

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            Document doc = (Document) it.next();
            AnalysisEntity analysisEntity = getAnalysisEntity(doc);
            analysis.add(analysisEntity);
        }

        return analysis;
    }

    private Integer getNumberOfFaces(byte[] bytes) {
        return amazonRekognitionClient.countFaces(bytes);
    }

    private List<String> getCelebrities(byte[] bytes) {
        return amazonRekognitionClient.checkForCelebrities(bytes);
    }

    private List<Label> getScenes(byte[] bytes) {
        return amazonRekognitionClient.sceneDetection(bytes);
    }

    private byte[] getImageBytes(Integer imageId) {
        return Base64.decodeBase64(getImageString(imageId));
    }

    private String getImageString(Integer imageId) {
        if (imageFilterBaseUrl.isPresent()) {
            log.info("Calling imageUpload service: get image document. " + baseUrl);
            try {
                return httpClient
                        .target(imageFilterBaseUrl.get() + "/api/images/getImage/" + getMongoId(imageId))
                        .request().get(new GenericType<String>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public String getMongoId(Integer imageId) {
        if (baseUrl.isPresent()) {
            log.info("IMAGE SERVICE. " + baseUrl.get() + "/api/images/mongoId/" + imageId);
            try {
                return httpClient
                        .target(baseUrl.get() + "/api/images/url/" + imageId)
                        .request().get(new GenericType<String>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }
    private byte[] getBytesFromURL(String imagePath) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            URL url = new URL(imagePath);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Firefox");
            InputStream inputStream = conn.getInputStream();
            int n = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] img = output.toByteArray();

        return img;
    }

    private AnalysisEntity getAnalysisEntity(Document doc) {
        AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setImageId(doc.getInteger("imageId"));
        analysisEntity.setNumberOfFaces(doc.getInteger("numberOfFaces"));
        analysisEntity.setCelebrities((List<String>) doc.get("celebrities"));
        Document scenes = (Document) doc.get("scenes");
        List<Label> labels = new ArrayList<>();
        for (Map.Entry<String, Object> entry : scenes.entrySet()) {
            Label label = new Label();
            label.setName(entry.getKey());
            Double doubleConf = (Double) entry.getValue();
            label.setConfidence(doubleConf.floatValue());
            labels.add(label);
        }
        analysisEntity.setScenes(labels);

        return analysisEntity;
    }


}
