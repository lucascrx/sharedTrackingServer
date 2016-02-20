package mySQLdb;

import java.sql.Timestamp;

/**Exception sent when the sql statement is not synchronized (timestamp basedith the session*/
public class DatabaseSynchroException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Timestamp of session last modification for reconfiguration purposes*/
	private Timestamp lastModifTimestamp;
	
	public DatabaseSynchroException(Timestamp lastModif){
		super();
		this.lastModifTimestamp=lastModif;
	}
	
	public Timestamp getLastModif(){
		return this.lastModifTimestamp;
	}
	
	
}
