package response;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class UpdateResponse extends Response implements Serializable{
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**Last modification time of session meta data, in case of parameter update response,
	 * this correspond to the time the triggered update occurred (no synchronization needed in this precise case)*/
	private Timestamp lastModificationTime;

	public UpdateResponse(boolean flag, Timestamp lastModifTime) {
		super();
		this.operationStatus=flag;
		this.lastModificationTime=lastModifTime;
	}
	
	public UpdateResponse() {
		super();
		this.operationStatus=false;
		this.lastModificationTime=null;
	}
	
	public void setStatus(boolean s){
		this.operationStatus = s;
	}
	
	public void setTime(Timestamp t){
		this.lastModificationTime = t;
	}
	
	@XmlElement
	public boolean getOperationStatus() {
		return operationStatus;
	}

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getLastModificationTime() {
		return lastModificationTime;
	}

}
