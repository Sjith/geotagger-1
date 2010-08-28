package com.fede;

import java.util.Date;

import com.fede.R;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class OnlyPosMainActivity extends Activity {
	private GeoTaggerActivityMenu mMenu; 
	private GeoDbAdapter mDbHelper;
	private PositionListElem mChoosenPosition;
	private Long mPositionId;
	
	private GpsReadyIndicator mGpsReady;
	private PositionProvider mPositionProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.main_view_onlypos);
		
		mChoosenPosition = (PositionListElem) findViewById(R.id.OnlyPosChoosenPositionElem);
		
		mGpsReady = (GpsReadyIndicator) findViewById(R.id.GpsReadyElemEditor);
		mDbHelper = new GeoDbAdapter(this);
		mMenu = new GeoTaggerActivityMenu(this, mDbHelper, null);
		setupLocationListener();
		setupButtons();			
	}
	

	@Override
	protected void onPause() {		
		super.onPause();
		Preferences pref = GeotaggerUtils.getPreferences(this);
		if(pref.isGpsEnabled()){
			mPositionProvider.disableProvider(LocationManager.GPS_PROVIDER);
		}
		if(pref.isCellEnabled()){
			mPositionProvider.disableProvider(LocationManager.NETWORK_PROVIDER);
		}
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(GeotaggerUtils.rangeMode(this)){
			Intent i = new Intent(this, RangeMainActivity.class);
			startActivity(i);
			finish();
		}
		
		mDbHelper.open();
		Preferences pref = GeotaggerUtils.getPreferences(this);
		if(pref.isGpsEnabled()){
			mPositionProvider.enableProvider(LocationManager.GPS_PROVIDER);
		}
		if(pref.isCellEnabled()){
			mPositionProvider.enableProvider(LocationManager.NETWORK_PROVIDER);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	
	private void setupLocationListener()
	{
		LocationInterface gpsInterface = new LocationInterface(){
		    public void statusReady (){
		    	mGpsReady.setGpsStatusOk();
		    }
		    public void statusNotReady(){
		    	mGpsReady.setGpsStatusKo();
		    }			
		};
		
		LocationInterface cellInterface = new LocationInterface(){
			public void statusReady (){
				mGpsReady.setCellStatusOk();
			}
			public void statusNotReady(){
				mGpsReady.setCellStatusKo();
			}			
		};
		mPositionProvider = new PositionProvider(this, gpsInterface, cellInterface);
	}
	
	private void setupButtons()
	{

		Button createNewPosButton = (Button) findViewById(R.id.AddNewPositionButton);
		createNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				autoAddNewPosition();
			}});
		
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		mMenu.onCreateOptionsMenu(menu);
				
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);
		mMenu.onOptionsItemSelected(item);
		return true;
	}
	
	private boolean autoAddNewPosition(){
		if(mGpsReady.isValidPosition() == false){
			GeotaggerUtils.showErrorDialog(getString(R.string.gps_not_ready_name), 
								   getString(R.string.error_name), 
								   this);
			return false;
		}
		
		mPositionId = mDbHelper.addPosition(new Position(mPositionProvider.getCurrentLocation(), new Date()));
		Position pos = mDbHelper.getPositionObj(mPositionId.longValue());
        mChoosenPosition.setValues(pos.getName(), pos.getLatitude(), pos.getLongitude());
		return true;
	}
	
		

	

    
    

}
