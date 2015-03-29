package breadcrumb.cue.myapplication;

import java.io.Serializable;
import com.gimbal.android.Place;
public class IndoorLocationCoord implements Serializable{

	/**
	 * Store lat - long for the place.
	 */
	private static final long serialVersionUID = 1L;
	private Place place;
	public IndoorLocationCoord(Place  p){
		this.place = p;
	}
	public String getLat(){
		if(place.getAttributes().getAllKeys().size() != 3)
			return "";
		return place.getAttributes().getValue("lat").toString();
	}
	public String getLong(){
		return place.getAttributes().getValue("long").toString();
	}
	public String getCat(){
		return place.getAttributes().getValue("cat").toString();
	}
	


}
