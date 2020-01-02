package resources;

import clients.ImageAnalysingApi;
import entities.AnalysisEntity;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import services.AnalyzerBean;
import streaming.EventProducer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Path("/analyze")
public class AnalyzerResource {

    @Inject
    private AnalyzerBean analyzerBean;

    @Inject
    private EventProducer eventProducer;

    @Inject
    @RestClient
    private ImageAnalysingApi imageAnalysingApi;

    @GET
    public Response getAll() {
        return Response.status(Response.Status.CREATED).entity(analyzerBean.getAll()).build();
    }

    @POST
    @Path("/{imageId}")
    public Response analyze(@PathParam("imageId") Integer imageId) {
        AnalysisEntity analysisEntity = analyzerBean.analyze(imageId);

        CompletionStage<Void> stringCompletionStage =
                imageAnalysingApi.processImageAsynch(imageId);

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println(s));

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
