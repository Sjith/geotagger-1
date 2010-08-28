package com.fede;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

interface LocationInterface {

    void statusReady ();
    void statusNotReady();
}




public class LocationUpdater {
	private LocationManager mLManager;
	private LocationListener mLListener;
	private String mLProvider;
	private LocationInterface mInterface;
	private boolean mStatusOk; 

	public Location getLocation()
	{
		return mLManager.getLastKnownLocation(mLProvider);
	}
	
	public LocationUpdater(Context c, 
			String provider,
			LocationInterface i)
	{
		mInterface = i;
		mLManager = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
		mLProvider = provider;
		mStatusOk = false;
		
	    mLListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
	        	mStatusOk = true;
	        	mInterface.statusReady();
	        }
       
	        public void onProviderDisabled(String provider){
	        	mStatusOk = false;
	        	mInterface.statusNotReady();	        	
	        }

	        public void onProviderEnabled(String provider) {}

	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        	if( status == LocationProvider.OUT_OF_SERVICE){
	        		mStatusOk = false;
	        		mInterface.statusNotReady();
	        	}
	        }
	    };
	}
	
	public boolean isEnabled(){
		return mStatusOk;
	}
	
	public void stopUpdating()
	{
		mLManager.removeUpdates(mLListener);
		mInterface.statusNotReady();	// for obvious reasons
	}
	
	public void startUpdating()
	{
		mInterface.statusNotReady();	// it will get back ready whenever the location is available
		mLManager.requestLocationUpdates(mLProvider, 10000, 0, mLListener);
	}
};
