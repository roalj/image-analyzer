package resources;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/analyze")
public class AnalyzerResource {
    @GET
    public Integer getCommentsList() {
       return 42;
    }
}
