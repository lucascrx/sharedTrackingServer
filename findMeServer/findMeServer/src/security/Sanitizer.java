package security;

import java.sql.Timestamp;

import response.DateAdapter;

/**Provide a way to check if client inputs have a valid format*/
public class Sanitizer {

	private final static int MAX_SAMPLE_RATE = 1800000;
	
	private final static DateAdapter dateAdapter = new DateAdapter();
	
	/**Sanitizes user provided names as session and device name 
	 * Condition : .{4,50}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsName(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{4,50}" )){
			throw new SanitizationException(input+" doesn't have a valid Name format");
		}
		
	}
	
	/**Sanitizes user provided tokens as public and private IDs
	 *  Condition : .{25,50} 
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsToken(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{25,50}" )){
			throw new SanitizationException(input+" doesn't have a valid Token format");
		}
	}
	

	/**Sanitizes user provided device ID
	 *  Condition : [0-9A-Za-z]{5}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsDeviceID(String input) throws SanitizationException{
		if(input==null || !input.matches( "[0-9A-Za-z]{5}" )){
			throw new SanitizationException(input+" doesn't have a valid Device ID format");
		}
	}
	
	/**Sanitizes user provided device Name
	 *  Condition : .{0,20}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsDeviceName(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{0,20}" )){
			throw new SanitizationException(input+" doesn't have a valid Device Name format");
		}
	}


	/**Sanitizes user provided password
	 *Condition : .{10,50}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsPassword(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{10,50}" )){
			throw new SanitizationException(input+" doesn't have a valid Password format");
		}
	}
	

	/**Sanitizes user provided GPS values as latitude and longitude values
	 *Condition : not null
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeGPSvalue(Double input) throws SanitizationException{
		if(input == null){
			throw new SanitizationException("GPS value is null");
		}
	}

	/**Sanitizes user provided Upload Rate
	 *Condition : not null greater than 0
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeUploadRate(Integer input) throws SanitizationException{
		if(input == null || input<=0 ){
			throw new SanitizationException("provided rate null");
		}
	}

	/**Sanitizes user provided row index
	 * Condition : greater than 1
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeSampleIndex(Integer input) throws SanitizationException{
		if(input==null || input<1){
			throw new SanitizationException("index value is too low : "+input);
		}
	}
	
	/**Sanitizes user provided starting time in a stand alone way and returns the associated timestamp
	 * Condition : after current time
	 * @param input
	 * @throws SanitizationException
	 */
	public static Timestamp sanitizeStartTimeAlone(String input) throws SanitizationException{
		Timestamp inputTime = null;
		try{
			inputTime = new Timestamp(dateAdapter.unmarshal(input).getTime());
		}catch(Exception e){
			throw new SanitizationException("Starting time not readable");
		}
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(inputTime==null || now.after(inputTime)){
			throw new SanitizationException("Starting time has already occured");
		}
		return inputTime;
	}
	
	/**Sanitizes user provided starting time using end Time and returns the associated timestamp
	 * Condition : after current time and before end Time
	 * @param input
	 * @throws SanitizationException
	 */
	public static Timestamp sanitizeStartTime(String input, Timestamp endTime) throws SanitizationException{
		Timestamp inputTime = null;
		try{
			inputTime = new Timestamp(dateAdapter.unmarshal(input).getTime());
		}catch(Exception e){
			throw new SanitizationException("Starting time not readable");
		}
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(inputTime) || inputTime.after(endTime)){
			throw new SanitizationException("Starting time has already occured, or is after end Time");
		}
		return inputTime;
	}
	
	/**Sanitizes user provided ending time using start Time and returns the associated timestamp
	 * Condition : after current time and  after start Time
	 * @param input
	 * @throws SanitizationException
	 */
	public static Timestamp sanitizeEndTime(String input, Timestamp startTime) throws SanitizationException{
		Timestamp inputTime = null;
		try{
			inputTime = new Timestamp(dateAdapter.unmarshal(input).getTime());
		}catch(Exception e){
			throw new SanitizationException("Starting time not readable");
		}
		
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(inputTime) || inputTime.before(startTime)){
			throw new SanitizationException("End time has already occured, or is before start Time");
		}
		return inputTime;
	}
	
}
