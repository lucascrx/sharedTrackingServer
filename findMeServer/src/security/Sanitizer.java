package security;

import java.sql.Timestamp;

/**Provide a way to check if client inputs have a valid format*/
public class Sanitizer {

	private final static int MAX_SAMPLE_RATE = 1800000;
	
	/**Sanitizes user provided names as session and device name 
	 * Condition : .{4,255}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsName(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{4,255}" )){
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
	 *  Condition : [0-9]{15}:[0-9]{15} 
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsDeviceID(String input) throws SanitizationException{
		if(input==null || !input.matches( "[0-9]{15}:[0-9]{15}" )){
			throw new SanitizationException(input+" doesn't have a valid Device ID format");
		}
	}


	/**Sanitizes user provided password
	 *Condition : .{10,255}
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStringAsPassword(String input) throws SanitizationException{
		if(input==null || !input.matches( ".{10,255}" )){
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
	
	/**Sanitizes user provided starting time in a stand alone way
	 * Condition : after current time
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStartTimeAlone(Timestamp input) throws SanitizationException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input)){
			throw new SanitizationException("Starting time has already occured");
		}
	}
	
	/**Sanitizes user provided starting time using end Time
	 * Condition : after current time and before end Time
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeStartTime(Timestamp input, Timestamp endTime) throws SanitizationException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input) || input.after(endTime)){
			throw new SanitizationException("Starting time has already occured, or is after end Time");
		}
	}
	
	/**Sanitizes user provided ending time using start Time
	 * Condition : after current time and  after start Time
	 * @param input
	 * @throws SanitizationException
	 */
	public static void sanitizeEndTime(Timestamp input, Timestamp startTime) throws SanitizationException{
		java.util.Date date= new java.util.Date();
		Timestamp now = new Timestamp(date.getTime());
		if(input==null || now.after(input) || input.before(startTime)){
			throw new SanitizationException("End time has already occured, or is before start Time");
		}
	}
	
}
