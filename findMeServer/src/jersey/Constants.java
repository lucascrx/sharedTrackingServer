package jersey;

public class Constants {
	
	public final static String CREATE_IMMEDIATE_SESSION_PATH = "/immediate_creation";
	public final static String CREATE_PREPARED_SESSION_PATH="/prepared_creation";
	public final static String READ_SESSION_PATH="/reading";
	public final static String UPDATE_SESSION_STARTING_TIME_PATH = "/update_starting_time";
	public final static String UPDATE_SESSION_ENDING_TIME_PATH = "/update_ending_time";
	public final static String UPDATE_SESSION_NAME_PATH = "/update_name";
	public final static String UPDATE_UPLOAD_RATE_SESSION_PATH = "/update_upload_rate";
	public final static String UPDATE_POSITION_SESSION_PATH = "/upload_position";
	public final static String CONTRIBUTE_SESSION_PATH = "/contribution";
	public final static String SYNCHRONIZATION_SESSION_PATH = "/synchronization";
	
	/**label for posted parameters*/
	public final static String POST_PARAM_LABEL_SESSION_NAME = "session_name";
	public final static String POST_PARAM_LABEL_UPLOAD_RATE = "rate";
	public final static String POST_PARAM_LABEL_PUBLIC_ID = "public_token";
	public final static String POST_PARAM_LABEL_PASSWORD = "password";
	public final static String POST_PARAM_LABEL_PRIVATE_ID = "private_token";
	public final static String POST_PARAM_LABEL_LATITUDE = "latitude";
	public final static String POST_PARAM_LABEL_LONGITUDE = "longitude";
	public final static String POST_PARAM_LABEL_DEVICE_NAME = "device_name";
	public final static String POST_PARAM_LABEL_DEVICE_ID = "device_token";
	public final static String POST_PARAM_LABEL_LOOKUP_INDEX = "expected_index";
	public final static String POST_PARAM_LABEL_STARTING_TIME = "starting_time";
	public final static String POST_PARAM_LABEL_ENDING_TIME = "ending_time";

}
