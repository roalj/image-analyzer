package resources;

import entities.AnalysisEntity;
import services.AnalyzerBean;
import services.ImageAnalyzingRunnable;
import streaming.EventProducer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
@Path("/analyze")
public class AnalyzerResource {
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    @Inject
    private AnalyzerBean analyzerBean;

    @GET
    public Response getAll() {
        return Response.status(Response.Status.CREATED).entity(analyzerBean.getAll()).build();
    }

    @POST
    @Path("/{imageId}")
    public Response analyze(@PathParam("imageId") Integer imageId) {
        return Response.status(Response.Status.CREATED).entity(analyzerBean.analyze(imageId)).build();
    }

    @POST
    public void analyzeImage(Integer imageId, @Suspended AsyncResponse ar) {
        ImageAnalyzingRunnable imageAnalyzingRunnable = new ImageAnalyzingRunnable(imageId);

        executor.execute(() -> {
            imageAnalyzingRunnable.analyzeImage();

            ar.resume("OK");
        });
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
