package jersey;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import manager.SampleManager;
import response.UpdateResponse;


//Sets the path to base URL 
@Path(Constants.UPDATE_SESSION_STARTING_TIME_PATH)
public class UpdateSessionStartingTimeService {
	
	private final String logTag = "Jersey.UpdateStartingTime : ";

	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test UpdateStartingTime Service" + "</title>"
	        + "<body><h1>" + "UpdateStartingTime Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  //Application integration Client post a name to update an existing guidance    
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public UpdateResponse updateSession(
		      @FormParam(Constants.POST_PARAM_LABEL_PRIVATE_ID) String privateID,
		      @FormParam(Constants.POST_PARAM_LABEL_STARTING_TIME) String start,
		      @Context HttpServletResponse servletResponse) throws IOException {
		  
		  String header = Constants.formatHeader(logTag+"New Service for Update Session Starting time");
		  System.out.println(header);
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  UpdateResponse response = manager.updateSessionStartingTime(privateID, start);
		  //returning obtained result
		  System.out.println(logTag+"Update Session Starting time Result Formatted");
		  return response;
	  }

}