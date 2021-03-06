package manager;

import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import response.DateAdapter;

/**Define the object returned when sample are read from the db*/

@XmlAccessorType(XmlAccessType.FIELD)
public class SampleList {
	
	/**The list of the requested samples*/
	private ArrayList<Sample> samples;
	/**The current update rate*/
	private Timestamp lastModificationTime;
	
	public SampleList(ArrayList<Sample> samples, Timestamp modifTime) {
		this.samples = samples;
		this.lastModificationTime = modifTime;
	}
	
	public SampleList() {
		this.samples = new ArrayList<Sample>();
		this.lastModificationTime = null;
	}
	
	public SampleList(Timestamp t) {
		this.samples = new ArrayList<Sample>();
		this.lastModificationTime = t;
	}
	
	@XmlElement
	public ArrayList<Sample> getSamples() {
		return samples;
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Timestamp getLastModificationTime() {
		return lastModificationTime;
	}

	public void setSamples(ArrayList<Sample> samples) {
		this.samples = samples;
	}

	public void setLastModificationTime(Timestamp modifTime) {
		this.lastModificationTime = modifTime;
	}
	
	public void addSample(Sample s){
		this.samples.add(s);
	}
}
