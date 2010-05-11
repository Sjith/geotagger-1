package fede.geotagger;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GeoPreferences extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.userprefs);
	}
}
