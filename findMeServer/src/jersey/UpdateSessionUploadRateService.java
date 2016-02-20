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
@Path(Constants.UPDATE_UPLOAD_RATE_SESSION_PATH)
public class UpdateSessionUploadRateService {
	
	private final String logTag = "Jersey.UpdateUploadRate : ";

	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test UpdateUploadRate Service" + "</title>"
	        + "<body><h1>" + "UpdateUploadRate Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  //Application integration Client post a name to update an existing guidance    
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public UpdateResponse updateSession(
		      @FormParam(Constants.POST_PARAM_LABEL_PRIVATE_ID) String privateID,
		      @FormParam(Constants.POST_PARAM_LABEL_UPLOAD_RATE) Integer rate,
		      @Context HttpServletResponse servletResponse) throws IOException {
		  String header = Constants.formatHeader(logTag+"New Service for Update Session Upload rate");
		  System.out.println(header);
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  UpdateResponse response = manager.updateSessionUploadRate(privateID, rate);
		  //returning obtained result
		  System.out.println(logTag+"Update Session Upload Rate Result Formatted");
		  return response;
	  }

}


	

