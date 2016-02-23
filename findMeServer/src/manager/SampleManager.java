package manager;

import java.sql.SQLException;
import java.sql.Timestamp;

import mySQLdb.Accesser;
import mySQLdb.DatabaseSynchroException;
import response.CreationResponse;
import response.DateAdapter;
import response.ReadingResponse;
import response.SynchronizationResponse;
import response.UpdateResponse;
import security.TokenGenerator;
import security.SanitizationException;
import security.Sanitizer;

/**Central class between the web services and the database accesser
 * A unique instance of this class is created : it is a singleton*/
public enum SampleManager {
	

	INSTANCE;
    private SampleManager() {
		System.out.println("Manager Initialization...");
    	this.generator = new TokenGenerator();
    	this.dbAccesser = new Accesser();
		System.out.println("Manager Initialized");
    }
	
    /**Log Tag for debugging purposes*/
    private static String logTag = "Sample Manager : ";
    
    /**Random generator for tokens generation*/
    private TokenGenerator generator;
    /**MySQL accesser for database management*/
    private Accesser dbAccesser;
    
	/**create a new tracking session in immediate mode, the public ID is generated and no 
	 * password is set. A new session is created with initialized status.
	 * @return the public and the private ID of the created session, and null if the creation has failed*/
	public CreationResponse createNewSession(String sessionName,Integer rate){
		CreationResponse response = new CreationResponse();
		try {
			System.out.println(logTag+" Creating new Session");
			
			//input sanitization
			Sanitizer.sanitizeStringAsName(sessionName);
			Sanitizer.sanitizeUploadRate(rate);
			
			//generate guidance private and public ID : random token
			String publicToken = this.generator.randomString();
			String privateToken = this.generator.randomString();
			//current time is starting time and last modification time (no end time set) 
			java.util.Date date= new java.util.Date();
			System.out.println(logTag+"Current Time, Absolute Time : "+date.getTime()+" | Local Time : "+date.toString());
			Timestamp now = new Timestamp(date.getTime());
			this.dbAccesser.createNewGuidance(publicToken, privateToken, sessionName, null, (int) rate,now,null,now);
			System.out.println(logTag+"New session creation Sucess : new row inserted + new session table created");
			response = new CreationResponse(true,sessionName,rate,publicToken,privateToken,now,null,now);
		} catch (SQLException e) {
			System.out.println(logTag+"New session creation database manip error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"New session creation not valid input : "+e.getMessage());
		}
		return response;
	}

	/**create a new tracking session in prepared mode, the public ID and the password
	 * are provided. A new session is created with initialized status.
	 * @return the privateID of the created session, and null if the creation has failed*/
	public CreationResponse createNewSession(String sessionName, String password, Integer rate,
			String start, String end ){
		CreationResponse response = new CreationResponse();
		try {

			System.out.println(logTag+"Creating new Session");
			
			//input sanitization
			Sanitizer.sanitizeStringAsName(sessionName);
			Sanitizer.sanitizeStringAsPassword(password);
			Sanitizer.sanitizeUploadRate(rate);
			Timestamp startTime = Sanitizer.sanitizeStartTimeAlone(start);
			Timestamp endTime = null;
			if(end!=null){
				endTime = Sanitizer.sanitizeEndTime(end, startTime);
			}
						
			//generate guidance private ID : random token
			String publicToken = this.generator.randomString();
			String privateToken = this.generator.randomString();
			//generating current time for lastmodiftime
			java.util.Date date= new java.util.Date();
			Timestamp now = new Timestamp(date.getTime());
			//creating new session in the database
			this.dbAccesser.createNewGuidance(publicToken, privateToken, sessionName, password,(int) rate, startTime,endTime,now);
			//generating response for client
			response = new CreationResponse(true,sessionName,rate,publicToken,privateToken,startTime,endTime,now);
			System.out.println(logTag+"New session creation Success : new row inserted + new session table created");
		} catch (SQLException e) {
			System.out.println(logTag+"New session creation database manipulation error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"New session creation not valid input : "+e.getMessage());
		}
		return response;
	}
	
	/**For prepared tracking session another device can ask to be tracked in presenting the password set
	 */
	public CreationResponse contributeToSession(String publicID, String password){
		CreationResponse response = new CreationResponse();
		try {
			System.out.println(logTag+"Contributing to existing Session");
			
			//input sanitization
			Sanitizer.sanitizeStringAsToken(publicID);
			Sanitizer.sanitizeStringAsPassword(password);
			
			//see if public ID and password couple exists in a row of the main table 
			//if yes return private ID
			SessionRetrieving result = this.dbAccesser.retrieveExistingSession(publicID, password);
			System.out.println(logTag+"existing Session retrieved, private ID transmitted");
			response = new CreationResponse(true,result.getName(),result.getRate(),publicID,result.getPrivateID(),
					result.getStartTime(),result.getEndTime(),result.getLastModificationTime());
		} catch (SQLException e) {
			System.out.println(logTag+"Contributing to Existing Session database manipulation error : : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"Contributing to Existing Session not valid input : "+e.getMessage());
		}
		return response;
	}
	
	/**update an existing tracking session, identified with privateID with a new sample
	 * @return the status of the update operation and the last modification time*/
		public UpdateResponse updateSessionWithSample(String privateID, Double latitude, Double longitude, String deviceName, String deviceID){
			UpdateResponse result = new UpdateResponse();
			try{
				System.out.println(logTag+"Updating Session : new Position");
				//input sanitization
				Sanitizer.sanitizeStringAsToken(privateID);
			Sanitizer.sanitizeGPSvalue(latitude);
			Sanitizer.sanitizeGPSvalue(longitude);
			Sanitizer.sanitizeStringAsDeviceName(deviceName);
			Sanitizer.sanitizeStringAsDeviceID(deviceID);
			
			//create sample object
			java.util.Date date= new java.util.Date();
			Timestamp t = new Timestamp(date.getTime());
			Sample sample = new Sample(t, (double) latitude, (double) longitude, deviceName, deviceID);
			
			//insert sample into the database
			Timestamp lastModifTime = this.dbAccesser.updateGuidanceWithSample(privateID, sample);
			result.setStatus(true);
			result.setTime(lastModifTime);
			System.out.println(logTag+"Session sample uplaod Sucess : session table populated with a new row");
		} catch (DatabaseSynchroException e){
			System.out.println(logTag+"Session sample uplaod, Synchro error, formating response for synchro : "+e.getMessage());
			Timestamp synchroTime = e.getLastModif();
			result.setTime(synchroTime);
		} catch (SQLException e) {
			System.out.println(logTag+"Session sample uplaod, database manipulation error : "+e.getMessage());
		}catch (SanitizationException e) {
		
			System.out.println(logTag+"Session update not valid input : "+e.getMessage());
		}
		return result;
	}
	
	/**fetch samples from the tracking session identified by publicID with identifiers upper or equal to
	 * the provided index. Sample list is formated under a String format
	 * @return the sample list formated under a String*/
	public ReadingResponse readSession(String publicID, Integer sampleIndex){
		ReadingResponse result = new ReadingResponse();
		try{
			System.out.println(logTag+"Reading Session");

			//input sanitization
			Sanitizer.sanitizeStringAsToken(publicID);
			Sanitizer.sanitizeSampleIndex(sampleIndex);
			
			//fetching sample list from the database
			SampleList list = this.dbAccesser.readGuidance(publicID,(int) sampleIndex);
			
			System.out.println(logTag+"Session read successfully, returning sample list, length : "+list.getSamples().size());
			result.setOperationStatus(true);
			result.setSamples(list);
		} catch (DatabaseSynchroException e){
			System.out.println(logTag+"Session sample reading, Synchro error, formating response for synchro : "+e.getMessage());
			Timestamp synchroTime = e.getLastModif();
			result.setSamples(new SampleList(synchroTime));
		} catch (SQLException e) {
			System.out.println(logTag+"Session reading database manipulation error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"Session reading not valid input : "+e.getMessage());
		}
		return result;
	}
	
	/**update name parameter of an existing tracking session, identified with privateID 
	 * @return the status of the update operation and the update time*/
	public UpdateResponse updateSessionName(String privateID, String newName){
		UpdateResponse result = new UpdateResponse();
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		try{
			System.out.println(logTag+"Updating Session Metadata : Name");
			//sanitization
			Sanitizer.sanitizeStringAsToken(privateID);
			Sanitizer.sanitizeStringAsName(newName);
			//update
			this.dbAccesser.updateSessionName(privateID, newName, now);
			result = new UpdateResponse(true,now);
			System.out.println(logTag+"Session name update Sucess : session name parameter updated");
		} catch (SQLException e) {
			System.out.println(logTag+"Session name update database manipulation error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"Session name update not valid input : "+e.getMessage());
		}
		return result;
	}
	
	/**update upload rate parameter of an existing tracking session, identified with privateID 
	 * @return the status of the update operation and the update time*/
	public UpdateResponse updateSessionUploadRate(String privateID, Integer rate){
		UpdateResponse result = new UpdateResponse();
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		try{
			System.out.println(logTag+"Updating Session Metadata : Rate");
			//sanitization
			Sanitizer.sanitizeStringAsToken(privateID);
			Sanitizer.sanitizeUploadRate(rate);
			//update
			this.dbAccesser.updateSessionUploadRate(privateID, (int) rate, now);
			result = new UpdateResponse(true,now);
			System.out.println(logTag+"Session rate update Sucess : upload rate parameter updated");
		} catch (SQLException e) {
			System.out.println(logTag+"Session rate update database manipulation error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"Session rate update not valid input : "+e.getMessage());
		}
		return result;
	}

	/**update starting time parameter of an existing tracking session, identified with privateID 
	 * @return the status of the update operation and the update time*/
	public UpdateResponse updateSessionStartingTime(String privateID, String start){
		UpdateResponse result = new UpdateResponse();
		Timestamp startTime = null;
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		try{
			System.out.println(logTag+"Updating Session Metadata : Starting Time");
			//sanitization
			startTime = this.dbAccesser.verifyStartingTimeSynchro(privateID,start);
			//update
			this.dbAccesser.updateSessionStartingTime(privateID, startTime, now);
			result = new UpdateResponse(true,now);
			System.out.println(logTag+"Session starting time update Sucess : starting time parameter updated");	
		} catch (DatabaseSynchroException e) {
			System.out.println(logTag+"Session starting time update : synchronization error");
			Timestamp lastModif = e.getLastModif();
			result.setTime(lastModif);
		} catch (SQLException e) {
			System.out.println(logTag+"Session starting time update database manipulation error : "+e.getMessage());
		}
		return result;
	}
	
	/**update ending time parameter of an existing tracking session, identified with privateID 
	 * @return the status of the update operation and the update time*/
	public UpdateResponse updateSessionEndingTime(String privateID, String end){
		UpdateResponse result = new UpdateResponse();
		Timestamp endTime = null;
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		try{
			System.out.println(logTag+"Updating Session Metadata : ending Time");
			//sanitization
			endTime = this.dbAccesser.verifyEndingTimeSynchro(privateID,end);
			//update
			this.dbAccesser.updateSessionEndingTime(privateID, endTime, now);
			result = new UpdateResponse(true,now);
			System.out.println(logTag+"Session ending time update Sucess : ending time parameter updated");
		} catch (DatabaseSynchroException e) {
			System.out.println(logTag+"Session ending time update : synchronization error");
			Timestamp lastModif = e.getLastModif();
			result.setTime(lastModif);
		}catch (SQLException e) {
			System.out.println(logTag+"Session ending time update database manipulation error : "+e.getMessage());
		} 
		return result;
	}
	
	/**Fetch meta-data of a tracking session identified with its publicID
	 * for client synchronization
	 * @return the meta-data of the targeted session*/
	public SynchronizationResponse getSessionMetaData(String publicID){
		SynchronizationResponse synchroResponse = new SynchronizationResponse();
		try{
			System.out.println(logTag+"Fetching Session Metadata for synchronization");
			//sanitization		
			Sanitizer.sanitizeStringAsToken(publicID);
			//fetching
			SessionMetaData metadata = this.dbAccesser.getSessionMetaData(publicID);
			synchroResponse = new SynchronizationResponse(true,metadata);
			System.out.println(logTag+"Session metadata fetched");
		} catch (SQLException e) {
			System.out.println(logTag+"Fetching Session Metadata database manipulation error : "+e.getMessage());
		} catch (SanitizationException e) {
			System.out.println(logTag+"Fetching Session Metadata not valid input : "+e.getMessage());
		}
		return synchroResponse;
	}
	
}
