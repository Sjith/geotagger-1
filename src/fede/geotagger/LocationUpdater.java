package fede.geotagger;

import android.content.Context;
import android.location.Criteria;
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
	private Context mContext;
	private LocationManager mLManager;
	private LocationListener mLListener;
	private String mLProvider;
	LocationInterface mInterface;

	public Location getLocation()
	{
		return mLManager.getLastKnownLocation(mLProvider);
	}
	
	public LocationUpdater(Context c, LocationInterface i)
	{
		mContext = c;
		mLManager = (LocationManager)c.getSystemService(mContext.LOCATION_SERVICE);

	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(true);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    mLProvider = mLManager.getBestProvider(criteria, true);    
	    mLManager.requestLocationUpdates(mLProvider, 2000, 10, mLListener);    
    
	    mLListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
	        	mInterface.statusReady();
	        }
       
	        public void onProviderDisabled(String provider){}

	        public void onProviderEnabled(String provider) {}

	        public void onStatusChanged(String provider, int status, Bundle extras) {
	        	if( status == LocationProvider.OUT_OF_SERVICE ||
	        		status == LocationProvider.TEMPORARILY_UNAVAILABLE){
	        		mInterface.statusNotReady();
	        	}
	        }
	    };
	}
};
