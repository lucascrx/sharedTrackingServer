package manager;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import response.DateAdapter;

@XmlRootElement
public class Sample implements Serializable{	

	private Timestamp time;
	private double latitude;
	private double longitude;
	private String deviceName;
	private String deviceID;

	public Sample(Timestamp time, double latitude,double longitude,String devName, String devID) {
		this.time = time;
		this.latitude = latitude;
		this.longitude = longitude;
		this.deviceName = devName;
		this.deviceID = devID;
	}

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getTime() {
		return time;
	}


	@XmlElement
	public double getLongitude() {
		return longitude;
	}
	

	@XmlElement
	public double getLatitude() {
		return latitude;
	}


	@XmlElement
	public String getDeviceName() {
		return deviceName;
	}


	@XmlElement
	public String getDeviceID() {
		return deviceID;
	}

	

}
