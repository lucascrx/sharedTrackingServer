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
import javax.xml.bind.annotation.XmlElement;

import manager.SampleManager;
import response.CreationResponse;

//Plain old Java Object it does not extend as class or implements 
//an interface

//The class registers its methods for the HTTP GET request using the @GET annotation. 
//Using the @Produces annotation, it defines that it can deliver several MIME types,
//text, XML and HTML. 

//The browser requests per default the HTML MIME type.

//Sets the path to base URL 
@Path(Constants.CREATE_PREPARED_SESSION_PATH)
public class PreparedCreationService {
	
	private final String logTag = "Jersey.PreparedCreation : ";

	  //browser debugging
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Test Prepared Creation Service" + "</title>"
	        + "<body><h1>" + "Prepared Creation Service" + "</body></h1>" + "</html> ";
	  }
	  
	  
	  /**Immediate Session Creation Service to start a new tracking session quickly
	   * providing a guidance name and a sampling rate*/   
	  @POST
	  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	  @Produces(MediaType.APPLICATION_JSON)
	  public CreationResponse createSession(
		      @FormParam(Constants.POST_PARAM_LABEL_SESSION_NAME) String name,
		      @FormParam(Constants.POST_PARAM_LABEL_PUBLIC_ID) String publicID,
		      @FormParam(Constants.POST_PARAM_LABEL_PASSWORD) String password,
		      @FormParam(Constants.POST_PARAM_LABEL_UPLOAD_RATE) Integer rate,
		      @FormParam(Constants.POST_PARAM_LABEL_STARTING_TIME) String start,
		      @FormParam(Constants.POST_PARAM_LABEL_ENDING_TIME) String end,
		      @Context HttpServletResponse servletResponse) throws IOException {
		  
		  String header = Constants.formatHeader(logTag+"New Service for Creating Prepared Session ");
		  System.out.println(header);
		  //asking Manager for processing
		  SampleManager manager = SampleManager.INSTANCE;
		  CreationResponse response = manager.createNewSession(name, publicID, password,rate,start,end);
		  System.out.println(logTag+"Prepared Session Creation Result Formatted");
		  //returning obtained result
		  return response;
		  
	  }

}
