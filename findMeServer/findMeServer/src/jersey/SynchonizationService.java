
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
import response.CreationResponse;
import response.Response;
import response.SynchronizationResponse;


//Plain old Java Object it does not extend as class or implements 
//an interface

//The class registers its methods for the HTTP GET request using the @GET annotation. 
//Using the @Produces annotation, it defines that it can deliver several MIME types,
//text, XML and HTML. 

//The browser requests per default the HTML MIME type.

//Sets the path to base URL 
@Path(Constants.SYNCHRONIZATION_SESSION_PATH)
public class SynchonizationService {
		
		private final String logTag = "Jersey.Synchronization : ";

		  //browser debugging
		  @GET
		  @Produces(MediaType.TEXT_HTML)
		  public String sayHtmlHello() {
		    return "<html> " + "<title>" + "Test Synchronization Service" + "</title>"
		        + "<body><h1>" + "Synchronization Service" + "</body></h1>" + "</html> ";
		  }
		  
		  
		  /**Immediate Session Creation Service to start a new tracking session quickly
		   * providing a guidance name and a sampling rate*/   
		  @POST
		  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		  @Produces(MediaType.APPLICATION_JSON)
		  public SynchronizationResponse contribute(
			      @FormParam(Constants.POST_PARAM_LABEL_PUBLIC_ID) String publicID,
			      @Context HttpServletResponse servletResponse) throws IOException {
			  
			  String header = Constants.formatHeader(logTag+"New Service for Synchronization ");
			  System.out.println(header);
			  //asking Manager for processing
			  SampleManager manager = SampleManager.INSTANCE;
			  SynchronizationResponse response = manager.getSessionMetaData(publicID);
			  //returning obtained result
			  System.out.println(logTag+"Contribution result formatted");
			  return response;
			  
		  }

	

}

