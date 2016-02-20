package jersey;

import java.io.IOException;

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
@Path(Constants.UPDATE_SESSION_NAME_PATH)
public class UpdateSessionNameService {
	
	private final String logTag = "Jersey.UpdateName : ";

	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test UpdateName Service" + "</title>"
	        + "<body><h1>" + "UpdateName Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  //Application integration Client post a name to update an existing guidance    
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public UpdateResponse updateSession(
		      @FormParam(Constants.POST_PARAM_LABEL_PRIVATE_ID) String privateID,
		      @FormParam(Constants.POST_PARAM_LABEL_SESSION_NAME) String sessionName,
		      @Context HttpServletResponse servletResponse) throws IOException {

		  String header = Constants.formatHeader(logTag+"New Service for Update Session Name");
		  System.out.println(header);
		  
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  UpdateResponse response = manager.updateSessionName(privateID, sessionName);
		  //returning obtained result
		  System.out.println(logTag+"Update Session Name Result Formatted");
		  return response;
	  }

}
