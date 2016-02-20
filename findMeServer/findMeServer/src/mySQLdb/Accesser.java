package mySQLdb;

import java.util.ArrayList;

import manager.Sample;
import manager.SampleList;
import manager.SessionMetaData;
import manager.SessionRetrieving;
import security.SanitizationException;
import security.Sanitizer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**class accessing the database
 * providing entry points for the manager*/
public class Accesser {
	
    public Accesser() {
    	deployMainTable();
    }
    
	private final String logTag = "mySQLdb.Accesser";
	private Connection dbConnection;
	
	
	/**Create new guidance in the database : 
	 * add a new row in the main table with INITIALIZED status and create a new session table
	 * @throws SQLException */
	public void createNewGuidance(String publicToken, String privateToken, String guideName, String password, 
			int updateRate, Timestamp startTime, Timestamp endTime,Timestamp currentTime) 
			throws SQLException{
		openConnectionWithDb();	
		try{
			//MAIN TABLE ROW INSERTION
			String query = Constants.prepareMainTableInsertQuery();
			System.out.println(this.logTag+" new row in main table creation query ready : "+query);
			PreparedStatement preparedStmt = this.dbConnection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString (1, publicToken);
			preparedStmt.setString (2, privateToken);
			preparedStmt.setString (3, guideName);
			preparedStmt.setTimestamp(4,startTime);
			if(password==null){
				preparedStmt.setNull(5, java.sql.Types.CHAR);
			}else{
				preparedStmt.setString (5, password);
			}
			preparedStmt.setInt(6, updateRate);
			if(endTime==null){
				preparedStmt.setNull(7, java.sql.Types.TIMESTAMP);
			}else{
				preparedStmt.setTimestamp(7,endTime);
				
			}
			preparedStmt.setTimestamp(8,currentTime);
			System.out.println(this.logTag+" Main Table insert statement : "+preparedStmt);
	        int affectedRows = preparedStmt.executeUpdate();
	        //Retrieving affected ID of the new row;
	        int newRowID;
	        if (affectedRows == 0) {
	            throw new SQLException("New session row insertion inside main table failed");
	        }
	        ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	        	System.out.println("retrieved inserted row" + generatedKeys.getInt(1));
	        	//Retrieving session ID field
	        	newRowID = generatedKeys.getInt(1);
	        }
	        else {
	        	throw new SQLException("Session row retreiving with session ID within main Table failed");
	        }
			
	        //CREATE NEW SESSION TABLE
	        String newGuidanceTableName = Constants.sessionTableNamePrefix+Integer.toString(newRowID);
			String str = Constants.prepareTableCreationQuery(newGuidanceTableName,Constants.sessionTableFieldNames,Constants.sessionTableFieldTypes);
			System.out.println(this.logTag+" new guidance table creation query ready : "+str);
			//executing statement					
			Statement stmt = this.dbConnection.createStatement();
			stmt.executeUpdate(str);
		}finally{
			closeConnectionWithDb();
		}
	}
	
	public void deleteGuidance(int guidancePrivateID){
		
	}
	
	/**
	 * Retrieve the private ID and the upload Rate of an existing session given a publicID and password (hashed) couple
	 * In order for new devices to be tracked within the same session
	 * @throws SQLException */
	public SessionRetrieving retrieveExistingSession(String publicID, String password) throws SQLException{
		SessionRetrieving retrieving = null;
		openConnectionWithDb();	
		try{		
			//resolve session row in the main table
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[1]+" = ? AND "+Constants.mainTableFieldNames[5]+" = SHA2(?,256)";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,publicID);
			prepStmt.setString(2,password);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" session associated row found in the main table using publicID and password");
				String name = rs.getString(Constants.mainTableFieldNames[3]);
				String token = rs.getString(Constants.mainTableFieldNames[2]);
				int rate = rs.getInt(Constants.mainTableFieldNames[6]);
				Timestamp startTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp endTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				Timestamp lastmodifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				retrieving = new SessionRetrieving(name,token,rate,startTime,endTime,lastmodifTime);
			}else{
				throw new SQLException("Session row retreiving with public ID "+publicID+" and password "+password+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return retrieving;
	}
	
	/**
	 * Update an existing session table with a new sample
	 * @throws DatabaseSynchroException, SQLException
	 * @return the last modification time of session meta data*/
	public Timestamp updateGuidanceWithSample(String guidancePrivateID, Sample submittedSample) throws DatabaseSynchroException, SQLException{
		Timestamp lastModifTime = null;
		openConnectionWithDb();	
		try{
		//resolve guidance table name and if first sample update status
			//resolving guidance table from guidance private ID 
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[2]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePrivateID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" session associated row found in the main table");
				int rowRef = rs.getInt(Constants.mainTableFieldNames[0]);
				
				lastModifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				
				//checking if sample date is between session start and end date
				Timestamp startingTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp endingTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				Timestamp sampleTime = submittedSample.getTime();
				if(sampleTime.before(startingTime)){
					throw new DatabaseSynchroException(lastModifTime);
				}
				if(endingTime!=null&&sampleTime.after(endingTime)){
					throw new DatabaseSynchroException(lastModifTime);
				}
				
				//insert new row in the guidance table
				String query2 = Constants.prepareSessionTableInsertQuery(rowRef);
				System.out.println(this.logTag+" new row in guidance table "+rowRef+" sample insertion query ready : "+query2);
				PreparedStatement preparedStmt2 = this.dbConnection.prepareStatement(query2);
				preparedStmt2.setBigDecimal(1, new BigDecimal(submittedSample.getLatitude()));
				preparedStmt2.setBigDecimal (2, new BigDecimal(submittedSample.getLongitude()));
				preparedStmt2.setTimestamp(3, submittedSample.getTime());
				preparedStmt2.setString(4, submittedSample.getDeviceName());
				preparedStmt2.setString(5, submittedSample.getDeviceID());
				System.out.println(this.logTag+" Session Table insert statement : "+preparedStmt2);
				preparedStmt2.executeUpdate();
				System.out.println(this.logTag+" new sample row inserted in guidance table "+rowRef);
			}else{
				throw new SQLException("Session row retreiving with private ID "+guidancePrivateID+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return lastModifTime;
	}
	
	/**read sample from the specified session starting at the provided index
	 * @return list of samples within the session beyond the index
	 * @throws DatabaseSynchroException, SQLException*/
	public SampleList readGuidance(String guidancePublicID, int expectedSample) throws DatabaseSynchroException, SQLException{
		SampleList result = new SampleList();
		openConnectionWithDb();
		try{
			//Select row reference from the main table using public ID
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[1]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePublicID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" guidance associated row found in the main table");
				
				int rowRef = rs.getInt(Constants.mainTableFieldNames[0]);
			
				Timestamp lastmodifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				result.setLastModificationTime(lastmodifTime);
				
				Timestamp startTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp endTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				java.util.Date date= new java.util.Date();
				Timestamp now = new Timestamp(date.getTime());
				
				//Checking if the session is in recording range
				if(endTime!=null&&now.after(endTime)){
					throw new DatabaseSynchroException(lastmodifTime);
				}
				if(now.before(startTime)){
					throw new DatabaseSynchroException(lastmodifTime);
					
				}
				
				//Select sample with ID greater than expectedSample
				System.out.println(this.logTag+" the read Guidance "+rowRef+" is recording : reading the latest samples");
				String guidanceTableName = Constants.sessionTableNamePrefix+Integer.toString(rowRef);
				String selectString2 = "SELECT * FROM "+guidanceTableName+" WHERE "+Constants.sessionTableFieldNames[0]+" >= ? ";
				PreparedStatement prepStmt2 = this.dbConnection.prepareStatement(selectString2);
				prepStmt2.setInt(1,expectedSample);
				ResultSet rs2 = prepStmt2.executeQuery();
				while(rs2.next()){
					//constructing the returned object with the retrieved samples
					double latitude = rs2.getDouble(Constants.sessionTableFieldNames[1]);
					double longitude = rs2.getDouble(Constants.sessionTableFieldNames[2]);
					Timestamp time = rs2.getTimestamp(Constants.sessionTableFieldNames[3]);
					String devName = rs2.getString(Constants.sessionTableFieldNames[4]);
					String devID = rs2.getString(Constants.sessionTableFieldNames[5]);
					Sample sample = new Sample(time,latitude,longitude,devName,devID);
					result.addSample(sample);
				}	
			}else {
	        	throw new SQLException("Session row retreiving with public ID "+guidancePublicID+" within main Table failed");
	        }
		}finally{
			closeConnectionWithDb();
		}
		//return 0, 1 or multiples rows
		return result;
	}
	
	/**Update the upload rate of the session
	 * @throws SQLException */
	public void updateSessionUploadRate(String privateID, int newRate, Timestamp now) throws SQLException{
		openConnectionWithDb();
		try{
		      String updateQuery = "UPDATE "+ Constants.mainTableName +" set "+ Constants.mainTableFieldNames[6] +" = ? , "+Constants.mainTableFieldNames[8]+" = ? where "+Constants.mainTableFieldNames[2]+" = ?";
		      PreparedStatement preparedStmt = this.dbConnection.prepareStatement(updateQuery);
		      preparedStmt.setInt(1, newRate);
		      preparedStmt.setTimestamp(2,now);
		      preparedStmt.setString(3, privateID);
		      preparedStmt.executeUpdate();
		}finally{
			closeConnectionWithDb();
		}
	}
	
	/**Update the name of the session
	 * @throws SQLException */
	public void updateSessionName(String privateID, String newName, Timestamp now) throws SQLException{
		openConnectionWithDb();
		try{
		      String updateQuery = "UPDATE "+ Constants.mainTableName +" set "+ Constants.mainTableFieldNames[3] +" = ? , "+Constants.mainTableFieldNames[8]+" = ? where "+Constants.mainTableFieldNames[2]+" = ?";
		      PreparedStatement preparedStmt = this.dbConnection.prepareStatement(updateQuery);
		      preparedStmt.setString(1, newName);
		      preparedStmt.setTimestamp(2,now);
		      preparedStmt.setString(3, privateID);
		      preparedStmt.executeUpdate();
		}finally{
			closeConnectionWithDb();
		}
	}
	
	/**Update the starting time of the session
	 * @throws SQLException */
	public void updateSessionStartingTime(String privateID, Timestamp newTime, Timestamp now) throws SQLException{
		openConnectionWithDb();
		try{
		      String updateQuery = "UPDATE "+ Constants.mainTableName +" set "+ Constants.mainTableFieldNames[4] +" = ? , "+Constants.mainTableFieldNames[8]+" = ? where "+Constants.mainTableFieldNames[2]+" = ?";
		      PreparedStatement preparedStmt = this.dbConnection.prepareStatement(updateQuery);
		      preparedStmt.setTimestamp(1, newTime);
		      preparedStmt.setTimestamp(2,now);
		      preparedStmt.setString(3, privateID);
		      preparedStmt.executeUpdate();
		}finally{
			closeConnectionWithDb();
		}
	}
	
	/**Update the ending time of the session
	 * @throws SQLException */
	public void updateSessionEndingTime(String privateID, Timestamp newTime, Timestamp now) throws SQLException{
		openConnectionWithDb();
		try{
		      String updateQuery = "UPDATE "+ Constants.mainTableName +" set "+ Constants.mainTableFieldNames[7] +" = ? , "+Constants.mainTableFieldNames[8]+" = ? where "+Constants.mainTableFieldNames[2]+" = ?";
		      PreparedStatement preparedStmt = this.dbConnection.prepareStatement(updateQuery);
		      preparedStmt.setTimestamp(1, newTime);
		      preparedStmt.setTimestamp(2,now);
		      preparedStmt.setString(3, privateID);
		      System.out.println(this.logTag+" : update ending time statement : "+preparedStmt);
		      preparedStmt.executeUpdate();
		}finally{
			closeConnectionWithDb();
		}
	}
	
	/**Get Session meta data for client Synchronization
	 * @throws SQLException */
	public SessionMetaData getSessionMetaData(String publicID) throws SQLException{
		SessionMetaData metadata = new SessionMetaData();
		openConnectionWithDb();
		try{
			//Select row reference from the main table using public ID
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[1]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,publicID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" guidance associated row found in the main table");
				//retrieving row values
				String name = rs.getString(Constants.mainTableFieldNames[3]);
				int rate = rs.getInt(Constants.mainTableFieldNames[6]);
				Timestamp startTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp endTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				Timestamp lastmodifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				//filling metadata object with the retrieved values
				metadata.setName(name);
				metadata.setRate(rate);
				metadata.setStartTime(startTime);
				metadata.setEndTime(endTime);
				metadata.setLastModificationTime(lastmodifTime);
			}else {
	        	throw new SQLException("Session row retreiving with public ID "+publicID+" within main Table failed");
	        }		      
		}finally{
			closeConnectionWithDb();
		}
		return metadata;
	}
	
	/**
	 * Get starting time of an existing session table with a new sample
	 * @throws SQLException 
	 * @return session starting time*/
	public Timestamp getStartingTime(String guidancePrivateID) throws SQLException{
		Timestamp startingTime = null;
		openConnectionWithDb();	
		try{
		//resolve guidance table name and if first sample update status
			//resolving guidance table from guidance private ID 
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[2]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePrivateID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" session associated row found in the main table");
				startingTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
			}else{
				throw new SQLException("Session row retreiving with private ID "+guidancePrivateID+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return startingTime;
	}
	
	/**
	 * Get ending time of an existing session table with a new sample
	 * @throws SQLException 
	 * @return session ending time*/
	public Timestamp getEndingTime(String guidancePrivateID) throws SQLException{
		Timestamp endingTime = null;
		openConnectionWithDb();	
		try{
		//resolve guidance table name and if first sample update status
			//resolving guidance table from guidance private ID 
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[2]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePrivateID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+" session associated row found in the main table");
				endingTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
			}else{
				throw new SQLException("Session row retreiving with private ID "+guidancePrivateID+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return endingTime;
	}
	
	/**
	 * when the starting time of an existing session table is updated,
	 * its synchronization must be verified with this function
	 * 
	 * start time can only be modified before the current starting time occurs and can only be postponed
	 * + before endTime if set
	 * 
	 * @throws SQLException, DatabaseSynchroException
	 * @return session starting time*/
	public Timestamp verifyStartingTimeSynchro(String guidancePrivateID, String newStart) throws SQLException, DatabaseSynchroException{
		Timestamp newStartTime = null;
		openConnectionWithDb();	
		try{
		//resolve guidance table name and if first sample update status
			//resolving guidance table from guidance private ID 
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[2]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePrivateID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+"Starting time synchro verification session associated row found in the main table");
				Timestamp currentStartingTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp currentEndingTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				Timestamp lastModifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				//check synchro
				java.util.Date date= new java.util.Date();
				Timestamp now = new Timestamp(date.getTime());
				//check synchronization with end time
				try{
					if(currentEndingTime==null){
						newStartTime = Sanitizer.sanitizeStartTimeAlone(newStart);
					}else{
						newStartTime = Sanitizer.sanitizeStartTime(newStart, currentEndingTime);
					}
				} catch (SanitizationException e) {
					throw new DatabaseSynchroException(lastModifTime);
				}
				//check synchronization with current start time
				//start time can only be modified before starting time and can only be postponed
				if(!now.before(currentStartingTime)||!currentStartingTime.before(newStartTime)){
					//send Exception
					throw new DatabaseSynchroException(lastModifTime);
				}
			}else{
				throw new SQLException("Session row retreiving with private ID "+guidancePrivateID+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return newStartTime;
	}
	
	
	/**
	 * when the ending time of an existing session table is updated,
	 * its synchronization must be verified with this function
	 * 
	 * end time can only be modified before the current ending time occurs and the new ending time mustn't
	 * have occurred yet
	 * + after startTime
	 * 
	 * @throws SQLException, DatabaseSynchroException
	 * @return session starting time*/
	public Timestamp verifyEndingTimeSynchro(String guidancePrivateID, String newEnd) throws SQLException, DatabaseSynchroException{
		Timestamp newEndTime = null;
		openConnectionWithDb();	
		try{
		//resolve guidance table name and if first sample update status
			//resolving guidance table from guidance private ID 
			String selectString = "SELECT * FROM "+Constants.mainTableName+" WHERE "+Constants.mainTableFieldNames[2]+" = ? ";
			PreparedStatement prepStmt = this.dbConnection.prepareStatement(selectString);
			prepStmt.setString(1,guidancePrivateID);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()){
				System.out.println(this.logTag+"Ending time synchro verification session associated row found in the main table");
				Timestamp currentStartingTime = rs.getTimestamp(Constants.mainTableFieldNames[4]);
				Timestamp currentEndingTime = rs.getTimestamp(Constants.mainTableFieldNames[7]);
				Timestamp lastModifTime = rs.getTimestamp(Constants.mainTableFieldNames[8]);
				//check synchro
				java.util.Date date= new java.util.Date();
				Timestamp now = new Timestamp(date.getTime());
				//check synchronization with start time
				try{
					newEndTime = Sanitizer.sanitizeEndTime(newEnd, currentStartingTime);	
				} catch (SanitizationException e) {
					throw new DatabaseSynchroException(lastModifTime);
				}
				//check synchronization with current start time
				//end time can only be modified before the current ending time occurs
				//the new ending time must also be in the future
				if(currentEndingTime!=null&&!now.before(currentEndingTime)){
					throw new DatabaseSynchroException(lastModifTime);
				}
				if(!now.before(newEndTime)){
					throw new DatabaseSynchroException(lastModifTime);		
				}
			}else{
				throw new SQLException("Session row retreiving with private ID "+guidancePrivateID+" within main Table failed");
			}
		}finally{
			closeConnectionWithDb();
		}
		return newEndTime;
	}
	
	
	
	
	
	/**Internal Methods*/

	public void openConnectionWithDb(){
		try {
			Class.forName(Constants.JDBC_DRIVER);
			this.dbConnection = DriverManager.getConnection(Constants.dbURL,Constants.dbManagerUsername,Constants.dbManagerPassword);			
			System.out.println(this.logTag+" Connection to database open");
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnectionWithDb(){
		try {
			if (this.dbConnection != null){
				this.dbConnection.close(); 
			}
			System.out.println(this.logTag+" Connection to database closed");
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deployMainTable(){
		openConnectionWithDb();		
		//preparing creation statement
		String str = Constants.prepareTableCreationQuery(Constants.mainTableName, Constants.mainTableFieldNames,Constants.mainTableFieldTypes);
		System.out.println(this.logTag+" Creation query ready : "+str);
		//executing statement
		try{
			Statement stmt = this.dbConnection.createStatement();
			stmt.executeUpdate(str);
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConnectionWithDb();
		}
	}
	
}
