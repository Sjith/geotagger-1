package fede.geotagger;

import java.util.Date;

import android.location.Location;

public class Position {
	public static String buildPositionName()
	{
		Date now = new Date();
		return "Position-" + now.toString().substring(0, 16);		
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAltitude() {
		return altitude;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date d) {
		date = d;
	}
	
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public Position(String name, String latitude, String longitude,
			String altitude, Date d) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.date = d;
	}
	
	
	
	public Position() {
		this.name = "";
		this.latitude = "";
		this.longitude = "";
		this.altitude = "";
		this.date = new Date();
	}
	
		
	public Position(Location l, Date d)
	{
		Double lat = l.getLatitude();
		Double lng = l.getLongitude();
		Double alt = l.getAltitude();
		this.name = buildPositionName();
		this.latitude = lat.toString();
		this.longitude = lng.toString();
		this.altitude = alt.toString();;
		this.date = d;
	}
	
	private String name;
	private String latitude;
	private String longitude;
	private String altitude;
	private Date date;
	
}
