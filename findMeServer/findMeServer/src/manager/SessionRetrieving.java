package manager;

import java.sql.Timestamp;

/**object retrieved when a device ask for contributing to an existing session
 * session private ID and upload rate*/
public class SessionRetrieving {
	
	/**name of the retrieved session**/
	private String name;
	/**private ID of the retrieved session*/
	private String privateID;
	/**upload rate of the retrieved session*/
	private int rate;
	/**starting time of the retrieved session*/
	private Timestamp startTime;
	/**end time of the retrieved session*/
	private Timestamp endTime;
	/**last modification time*/
	private Timestamp lastModificationTime;
	
	public SessionRetrieving(String name, String privateID, int rate, Timestamp start, Timestamp end,Timestamp lastModifTime ) {
		this.name=name;
		this.privateID = privateID;
		this.rate = rate;
		this.startTime=start;
		this.endTime=end;
		this.lastModificationTime=lastModifTime;
	}

	public String getName() {
		return this.name;
	}
	
	public String getPrivateID() {
		return this.privateID;
	}
	
	public int getRate() {
		return this.rate;
	}
	
	public Timestamp getStartTime(){
		return this.startTime;
	}
	
	public Timestamp getEndTime(){
		return this.endTime;
	}

	public Timestamp getLastModificationTime() {
		return lastModificationTime;
	}
}
