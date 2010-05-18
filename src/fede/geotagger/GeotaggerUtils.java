package fede.geotagger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceManager;

public class GeotaggerUtils {	
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
	
	public static Preferences getPreferences(Context c)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		
		return new Preferences (prefs.getBoolean("PREF_ENABLE_GPS", true),	// better being more accurate
								prefs.getBoolean("PREF_ENABLE_CELL", true));	
	}
	
	
}
