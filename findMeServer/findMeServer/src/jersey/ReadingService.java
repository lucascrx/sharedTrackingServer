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
import response.ReadingResponse;

//Sets the path to base URL 
@Path(Constants.READ_SESSION_PATH)
public class ReadingService {
	

	private final String logTag = "Jersey.Reading";
	
	 //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test Reading Service" + "</title>"
	        + "<body><h1>" + "Reading Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  //Application integration Client post a name to update an existing guidance    
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public ReadingResponse pollGuidance(
		      @FormParam(Constants.POST_PARAM_LABEL_PUBLIC_ID) String publicID,
		      @FormParam(Constants.POST_PARAM_LABEL_LOOKUP_INDEX) Integer index,
		      @Context HttpServletResponse servletResponse) throws IOException {

		  String header = Constants.formatHeader(logTag+" new Service for Session Reading with index : "+index);
		  System.out.println(header);
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  ReadingResponse response = manager.readSession(publicID, index);
		  System.out.println(logTag+"Session Reading Result Formatted");

		  //TODO remove at the end
		  System.out.println(logTag+"last modification date : "+response.getSamples().getLastModificationTime());
		  
		  //returning obtained result
		  return response;  
	  }
	

}
