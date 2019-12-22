package resources;

import clients.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Label;
import services.AnalyzerBean;

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
    private AnalyzerBean analyzerBean;

    @POST
    @Path("/{imageId}")
    public Response analyze(@PathParam("imageId") Integer imageId) {
       return Response.status(Response.Status.CREATED).entity(analyzerBean.analyze(imageId)).build();
    }
}
