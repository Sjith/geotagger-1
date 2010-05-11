package fede.geotagger;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;



public class PositionProvider {
	private LocationUpdater mGpsUpdater;
	private LocationUpdater mCellUpdater;
	
	public PositionProvider(Context c, LocationInterface gpsInterface, LocationInterface cellInterface) 	
	{
		
		mGpsUpdater = new LocationUpdater(c, LocationManager.GPS_PROVIDER, gpsInterface);		   
		mCellUpdater = new LocationUpdater(c, LocationManager.NETWORK_PROVIDER, cellInterface);
										  
	}
	
	public Location getCurrentLocation()
	{
		if(mGpsUpdater.isEnabled()){
			return mGpsUpdater.getLocation();
		}else{
			return mCellUpdater.getLocation();
		}
	}
	
	public void enableProvider(String provider)
	{
		if(provider == LocationManager.GPS_PROVIDER){
			mGpsUpdater.startUpdating();
		}
		if(provider == LocationManager.NETWORK_PROVIDER){
			mCellUpdater.startUpdating();
		}
	}
	
	public void disableProvider(String provider)
	{
		if(provider == LocationManager.GPS_PROVIDER){
			mGpsUpdater.stopUpdating();
		}
		if(provider == LocationManager.NETWORK_PROVIDER){
			mCellUpdater.stopUpdating();
		}
	}
	

}
