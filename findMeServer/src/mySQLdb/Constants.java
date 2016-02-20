package mySQLdb;

/**Class defining constant variables for mySQL database access and management
 * 
 * A Main table stores a row for every guidance session, each row points
 * to a specific session table where are stored location information
 * */

public class Constants {
	
	/**Constant Variables*/
	public static final int TRACKING_STATUS_INITIALIZED = 1;
	public static final int TRACKING_STATUS_RECORDING = 2;
	public static final int TRACKING_STATUS_CLOSED = 3;
	
	/**Database Access Setting*/
	public static final String dbManagerUsername = "******";
	//public static final String dbManagerPassword = "pwd_tomcat";
	public static final String dbManagerPassword = "******";
	//public static final String dbHost = "localhost";
	public static final String dbHost = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	//public static final String dbPort = "3307";
	public static final String dbPort = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	//public static final String dbURL = "jdbc:mysql://"+dbHost+":"+dbPort+"/guidance";
	public static final String dbURL = "******";
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	
	/**Main Table information*/
	public static final String mainTableName = "mainTable";
	public static final String[] mainTableFieldNames = 
		{"id","guidance_public_ID","guidance_private_ID","guide_name",
				"upload_start_time","hashed_password","update_rate","upload_end_time","last_modification_time"};
	public static final String[] mainTableFieldTypes = 
		{"INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY","VARCHAR(50) NOT NULL UNIQUE","VARCHAR(50) NOT NULL UNIQUE","VARCHAR(50) NOT NULL","TIMESTAMP NOT NULL DEFAULT 0","CHAR(64)","INT NOT NULL","TIMESTAMP NULL DEFAULT NULL","TIMESTAMP NOT NULL DEFAULT 0"};
	
	/**Guidance Table information*/
	public static final String sessionTableNamePrefix = "trackingSessionTable_";
	public static final String[] sessionTableFieldNames = 
		{"id","latitude","longitude","timestamp","device_name","device_id"};
	public static final String[] sessionTableFieldTypes =
		{"INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY","DOUBLE NOT NULL","DOUBLE NOT NULL","TIMESTAMP NOT NULL","VARCHAR(20) NOT NULL","VARCHAR(5) NOT NULL"};
	
	public static String prepareTableCreationQuery(String tableName, String[] fieldNames, String[] fieldTypes){
		String s1 = "CREATE TABLE IF NOT EXISTS "+tableName+" (";
		String s2 = "";
		int i = 0;
		int max = fieldNames.length;
		while (i<max){
			if (i!=0){
				s2 = s2 + ", ";
			}
			s2 = s2 + fieldNames[i]+" "+fieldTypes[i];
			i++;
		}
		s2=s2+" );";
		String s3=s1+s2;
		return s3;
	}
	
	public static String prepareMainTableInsertQuery(){
		int fieldNumber = Constants.mainTableFieldNames.length-1;
		String emptyFields = "(";
		for(int i=0;i<fieldNumber;i++){
			if (i!=0){
				emptyFields = emptyFields + ", ";
			}
			//password are hashed
			if (i==4){
				emptyFields = emptyFields + "SHA2(?,256)";
			}else{
				emptyFields = emptyFields + "?";
			}
		}
		emptyFields = emptyFields + ")";
		String query = "INSERT INTO "+Constants.mainTableName+" ("+Constants.printArrayItems(1,Constants.mainTableFieldNames)+")"+" values "+emptyFields;
		return query;
	}
	
	public static String prepareSessionTableInsertQuery(int sessionID){
		int fieldNumber = Constants.sessionTableFieldNames.length-1;
		String emptyFields = "(";
		for(int i=0;i<fieldNumber;i++){
			if (i!=0){
				emptyFields = emptyFields + ", ";
			}
			emptyFields = emptyFields + "?";
		}
		emptyFields = emptyFields + ")";
		String query = "INSERT INTO "+Constants.sessionTableNamePrefix+Integer.toString(sessionID)+" ("+Constants.printArrayItems(1,Constants.sessionTableFieldNames)+")"+" values "+emptyFields;
		return query;
	}
	
	public static String printArrayItems(int startIndex, String[] array){
		String str = "";
		int i = startIndex;
		int max = array.length;
		while (i<max){
			if (i!=startIndex){
				str = str + ", ";
			}
			str = str + array[i];
			i++;
		}
		return str;
	}
	
}
