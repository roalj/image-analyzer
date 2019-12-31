package resources;

import clients.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.Label;
import entities.AnalysisEntity;
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

    @GET
    public Response getAll() {
        return Response.status(Response.Status.CREATED).entity(analyzerBean.getAll()).build();
    }

    @POST
    @Path("/{imageId}")
    public Response analyze(@PathParam("imageId") Integer imageId) {
        AnalysisEntity analysisEntity = analyzerBean.analyze(imageId);

        return Response.status(Response.Status.CREATED).entity(analysisEntity).build();
    }

    @GET
    @Path("/{imageId}")
    public Response getAnalysis(@PathParam("imageId") Integer imageId) {
        AnalysisEntity analysisEntity = analyzerBean.getAnalysis(imageId);

        if (analysisEntity == null) {
            return Response.status(Response.Status.CREATED).entity("Analiza ne obstaja").build();
        }
        return Response.status(Response.Status.CREATED).entity(analysisEntity).build();
    }
}
