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
// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL 
@Path(Constants.CREATE_IMMEDIATE_SESSION_PATH)
public class ImmediateCreationService {
	
	private final String logTag = "Jersey.ImmediateCreation : ";

	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test Immediate Creation Service" + "</title>"
	        + "<body><h1>" + "Immediate Creation Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  /**Immediate Session Creation Service to start a new tracking session quickly
	   * providing a guidance name and a sampling rate*/   
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public CreationResponse createSession(
		      @FormParam(Constants.POST_PARAM_LABEL_SESSION_NAME) String name,
		      @FormParam(Constants.POST_PARAM_LABEL_UPLOAD_RATE) Integer rate,
		      @Context HttpServletResponse servletResponse) throws IOException {
		  
		  System.out.println(logTag+"New Service for Creating Immediate Session ");
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  CreationResponse response = manager.createNewSession(name,rate);
		  System.out.println(logTag+"Immediate Session Creation Result Formatted");
		  //returning obtained result
		  return response;
		  
	  }
	
}
