package manager;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import response.DateAdapter;

/**Session Meta data for client synchronization*/
public class SessionMetaData {
	
	/**name of the retrieved session**/
	private String name;
	/**upload rate of the retrieved session*/
	private int rate;
	/**starting time of the retrieved session*/
	private Timestamp startTime;
	/**end time of the retrieved session*/
	private Timestamp endTime;
	/**last modification time*/
	private Timestamp lastModificationTime;
	
	public SessionMetaData(String name, int rate, Timestamp startTime, Timestamp endTime,
			Timestamp lastModificationTime) {
		this.name = name;
		this.rate = rate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.lastModificationTime = lastModificationTime;
	}
	

	public SessionMetaData() {
		this.name = null;
		this.rate = 0;
		this.startTime = null;
		this.endTime = null;
		this.lastModificationTime = null;
	}
	
	@XmlElement
	public String getName() {
		return name;
	}
	
	@XmlElement
	public int getRate() {
		return rate;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getStartTime() {
		return startTime;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getEndTime() {
		return endTime;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getLastModificationTime() {
		return lastModificationTime;
	}


	
	
	public void setName(String name) {
		this.name = name;
	}


	public void setRate(int rate) {
		this.rate = rate;
	}


	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}


	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}


	public void setLastModificationTime(Timestamp lastModificationTime) {
		this.lastModificationTime = lastModificationTime;
	}

	
	
	
}
