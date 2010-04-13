package fede.geotagger;

public class Position {
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
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public Position(String name, String latitude, String longitude,
			String altitude) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public Position() {
		this.name = "";
		this.latitude = "";
		this.longitude = "";
		this.altitude = "";
	}
	
	private String name;
	private String latitude;
	private String longitude;
	private String altitude;
	
}
