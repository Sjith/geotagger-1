package fede.geo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceManager;

public class GeotaggerUtils {	
	private static final String PREF_NAME = "geoPrefs";
	private static final String PREFERENCE_LAST_VERSION = "lastVersion";
	private static final int ACTUAL_VERSION = 5;
	
	public static void showErrorDialog(String errorString, String title, Context context)
	{
    	String button1String = context.getString(R.string.ok_name); 
    	AlertDialog.Builder ad = new AlertDialog.Builder(context); 
    	ad.setTitle(title); 
    	ad.setMessage(errorString); 
    	ad.setPositiveButton(button1String,
    						 new OnClickListener() { 
	    						public void onClick(DialogInterface dialog, int arg1) {
	    							// do nothing
	    						} });
    	ad.show();
    	return;    
	}
	
	public static boolean showChangeLog(Context c)
	{
		int mode = Context.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(PREF_NAME, mode);		
		Long lastVersion = mySharedPreferences.getLong(PREFERENCE_LAST_VERSION, 0);
		
		if(ACTUAL_VERSION > lastVersion){
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.putLong(PREFERENCE_LAST_VERSION, ACTUAL_VERSION);
			editor.commit();
			return true;
		}
		return false;
	}
	public static Preferences getPreferences(Context c)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		
		return new Preferences (prefs.getBoolean("PREF_ENABLE_GPS", true),	// better being more accurate
								prefs.getBoolean("PREF_ENABLE_CELL", true));	
	}
	
	public static Boolean rangeMode(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String mode = prefs.getString("PREF_GEOTAGGER_TYPE", "RANGE");
		if(mode.equals("RANGE")){
			return true;
		}
		return false;		
	}
	
	public static Boolean positionMode(Context c){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String mode = prefs.getString("PREF_GEOTAGGER_TYPE", "RANGE");
		if(mode.equals("POSITION")){
			return true;
		}
		return false;
		
	}
	
}
