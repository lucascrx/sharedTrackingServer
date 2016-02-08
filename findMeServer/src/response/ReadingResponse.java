package response;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import manager.Sample;
import manager.SampleList;

@XmlRootElement
public class ReadingResponse extends Response implements Serializable{
	
	
	/**Flag indication if server operation went well or not*/
	private boolean operationStatus;
	/**Sample List retrieved from the database*/
	private SampleList samples;

	public ReadingResponse(boolean flag, SampleList sampleList) {
		super();
		this.operationStatus = flag;
		this.samples = sampleList;
	}
	
	public ReadingResponse() {
		super();
		this.operationStatus = false;
		this.samples = null;
	}
	
	public void setOperationStatus(boolean s){
		this.operationStatus=s;
	}
	
	public void setSamples(SampleList l){
		this.samples = l;
	}
	
	@XmlElement
	public boolean getOperationStatus() {
		return operationStatus;
	}

	@XmlElement
	public SampleList getSamples() {
		return samples;
	}
	
}
