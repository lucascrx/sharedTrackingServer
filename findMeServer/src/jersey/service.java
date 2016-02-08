package jersey;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/service")
public class service {
	
	  @Context
	  UriInfo uriInfo;
	  @Context
	  Request request;

	  //Application integration     
	  @GET
	  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	  public String getTodo() {
	    return "application get";
	  }
	  
	  // for the browser
	  @GET
	  @Consumes(MediaType.TEXT_XML)
	  public String getTodoHTML() {
	    return "browser get";
	  }

	  // This method is called if HTML is request
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
	        + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	  }

} 