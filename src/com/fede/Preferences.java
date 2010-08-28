package com.fede;

public class Preferences {
	private boolean gpsEnabled;
	private boolean cellEnabled;
	public boolean isGpsEnabled() {
		return gpsEnabled;
	}
	public void setGpsEnabled(boolean gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
	}
	public boolean isCellEnabled() {
		return cellEnabled;
	}
	public void setCellEnabled(boolean cellEnabled) {
		this.cellEnabled = cellEnabled;
	}
	public Preferences(boolean gpsEnabled, boolean cellEnabled) {
		super();
		this.gpsEnabled = gpsEnabled;
		this.cellEnabled = cellEnabled;
	}
	
	
}
