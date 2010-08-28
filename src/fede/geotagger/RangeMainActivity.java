package fede.geotagger;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import fede.geotagger.GeoTaggerActivityMenu.CleanFinished;

public class RangeMainActivity extends Activity implements CleanFinished{
	
	
	
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private PositionListElem mChoosenPosition;

	
	private GpsReadyIndicator mGpsReady;
	private PositionProvider mPositionProvider;
	private GeoTaggerActivityMenu mMenu; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_view_range);
		
		mChoosenPosition = (PositionListElem) findViewById(R.id.ChoosenPositionElem);
		mFromRange = (EditText) findViewById(R.id.FromEditText);
		mToRange = (EditText) findViewById(R.id.ToEditText);
		mGpsReady = (GpsReadyIndicator) findViewById(R.id.GpsReadyElemEditor);
		mDbHelper = new GeoDbAdapter(this);
		mMenu = new  GeoTaggerActivityMenu(this, mDbHelper, this);
		if(GeotaggerUtils.showChangeLog(this)){
			showDialog(getString(R.string.update_dialog));
		}
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
		if(GeotaggerUtils.positionMode(this)){
			Intent i = new Intent(this, OnlyPosMainActivity.class);
			startActivity(i);
			finish();
		}
		
		mDbHelper.open();
		populateFields();
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
		
		
		Button autoSetNewPosButton = (Button) findViewById(R.id.FastAddNewPositionButton);
		autoSetNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if(autoAddNewPosition()){
					finalizeRange();
				};
			}});
		
		 Button incrementButton = (Button) findViewById(R.id.AddEndRangeButton);
		 incrementButton.setOnClickListener(new View.OnClickListener(){
		 public void onClick(View view){
		 Long increment = Long.parseLong(mToRange.getText().toString()) + 1;
		 mToRange.setText(increment.toString());
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
	
	private void populateFields()
	{
		Long newMaxRange = mDbHelper.getMaxEndRange() + 1;
		mFromRange.setText(newMaxRange.toString());
		mToRange.setText(newMaxRange.toString());
		return;
	}
	

	
	
	
	private boolean checkRanges(Long fromRange, Long toRange)
	{	
		if(fromRange == 0 || toRange == 0){
			GeotaggerUtils.showErrorDialog(getString(R.string.invalid_range_name), 
								   getString(R.string.invalid_range_name),
								   this);
			return false;
		}
		
		if(fromRange > toRange){
			GeotaggerUtils.showErrorDialog(getString(R.string.error_name), 
											 getString(R.string.invalid_range_name),
											 this);
			return false;
		}
		
		
		if(mDbHelper.goodRangeBound(fromRange) == false){
			GeotaggerUtils.showErrorDialog(getString(R.string.error_name), 
					 getString(R.string.invalid_range_name),
					 this);	
			return false;
		}
		
		if(mDbHelper.goodRangeBound(toRange) == false){
			GeotaggerUtils.showErrorDialog(getString(R.string.error_name), 
					 getString(R.string.invalid_range_name),
					 this);
			return false;
		}
		
		return true;
	}
	
	private boolean checkAndAddRange()
	{
		if(mPositionId == null || mPositionId == 0){
			GeotaggerUtils.showErrorDialog(getString(R.string.error_name), 
								   getString(R.string.invalid_range_name),
								   this);
			return false;
		}
		
		Long fromRange = Long.parseLong(mFromRange.getText().toString());
		Long toRange = Long.parseLong(mToRange.getText().toString());
				
		if(checkRanges(fromRange, toRange) == false){
			return false;
		}
				
		mDbHelper.addRange(fromRange.intValue(), toRange.intValue(), mPositionId.intValue());
		return true;		
	}

	private void finalizeRange(){
		if(checkAndAddRange()){
			mPositionId = null;
			populateFields();				
		}
	}


	public void onCleanFinished() {
		populateFields();		
	}
	
	
	private void showDialog(String message){
		String button1String = getString(R.string.ok_name); 
    	AlertDialog.Builder ad = new AlertDialog.Builder(this); 
    	ad.setMessage(message); 
    	ad.setPositiveButton(button1String,
    						 new OnClickListener() { 
	    						public void onClick(DialogInterface dialog, int arg1) {
	    							// do nothing
	    						} });
    	ad.show();
	}



    
    


}
