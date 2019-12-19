package resources;

import clients.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Label;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/analyze")
public class AnalyzerResource {

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    @GET
    public Integer getCommentsList() {
       return 42;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(InputStream uploadedInputStream) {

        StringBuilder sb = new StringBuilder();

        byte[] bytes = new byte[0];
        try (uploadedInputStream) {
            bytes = uploadedInputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer numberOfFaces = amazonRekognitionClient.countFaces(bytes);
        sb.append("Number of faces " + numberOfFaces + "\n");

        List<String> detectedCelebrities = amazonRekognitionClient.checkForCelebrities(bytes);

        if (!detectedCelebrities.isEmpty()) {
            sb.append(detectedCelebrities.stream().collect(Collectors.joining(",\n")));
        }

        List<Label> labels = amazonRekognitionClient.sceneDetection(bytes);

        for (Label label : labels) {
            sb.append("\nLabel: " + label.getName() + "---> Confidence: " + label.getConfidence().toString());
        }


        return Response.status(Response.Status.CREATED).entity(sb.toString()).build();
    }
}
