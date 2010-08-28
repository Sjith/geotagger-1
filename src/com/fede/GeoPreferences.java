package com.fede;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.fede.R;

public class GeoPreferences extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.userprefs);
	}
}
