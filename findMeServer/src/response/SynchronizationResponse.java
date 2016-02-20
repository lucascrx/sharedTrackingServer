package response;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import manager.SessionMetaData;

/**Response message for synchronization query*/
@XmlRootElement
public class SynchronizationResponse extends Response{
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**session meta data for client synchronization*/
	private SessionMetaData metadata;
	
	public SynchronizationResponse(boolean operationStatus, SessionMetaData metadata) {
		this.operationStatus = operationStatus;
		this.metadata = metadata;
	}
	
	public SynchronizationResponse() {
		this.operationStatus = false;
		this.metadata = null;
	}
	
	@XmlElement
	public boolean getOperationStatus() {
		return operationStatus;
	}
	
	@XmlElement
	public SessionMetaData getMetadata() {
		return metadata;
	}
	
	
	

}
