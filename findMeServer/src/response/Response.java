package response;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**Defines the object to be returned to the client by the server,
 * composed by an operation status and optionally with a extra content*/

@XmlRootElement
@XmlSeeAlso({ReadingResponse.class, CreationResponse.class, UpdateResponse.class })
public abstract class Response {

	public Response() {
	}


}
