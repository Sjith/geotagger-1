package fede.geo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GeoPreferences extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.userprefs);
	}
}
