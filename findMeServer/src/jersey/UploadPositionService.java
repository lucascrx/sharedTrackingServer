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
@Path(Constants.UPDATE_POSITION_SESSION_PATH)
public class UploadPositionService {
	

	private final String logTag = "Jersey.UploadPosition : ";

	  
	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test UploadPosition Service" + "</title>"
	        + "<body><h1>" + "UploadPosition Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  //Application integration Client post a name to update an existing guidance    
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public UpdateResponse updateSession(
		      @FormParam(Constants.POST_PARAM_LABEL_PRIVATE_ID) String privateID,
		      @FormParam(Constants.POST_PARAM_LABEL_LATITUDE) Double latitude,
		      @FormParam(Constants.POST_PARAM_LABEL_LONGITUDE) Double longitude,
		      @FormParam(Constants.POST_PARAM_LABEL_DEVICE_NAME) String deviceName,
		      @FormParam(Constants.POST_PARAM_LABEL_DEVICE_ID) String deviceID,
		      @Context HttpServletResponse servletResponse) throws IOException {
		  
		  String header = Constants.formatHeader(logTag+"New Service for Upload Position");
		  System.out.println(header);
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  UpdateResponse response = manager.updateSessionWithSample(privateID, latitude, longitude, deviceName, deviceID);
		  //returning obtained result
		  System.out.println(logTag+"Upload Position Result Formatted");
		  return response;
	  }
	

}
