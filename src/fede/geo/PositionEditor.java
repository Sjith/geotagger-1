package fede.geo;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PositionEditor extends Activity {
	@Override
	protected void onPause() {
		super.onPause();
		mDbHelper.close();
		
		Preferences pref = GeotaggerUtils.getPreferences(this);
		if(pref.isGpsEnabled()){
			mPositionProvider.disableProvider(LocationManager.GPS_PROVIDER);
		}
		if(pref.isCellEnabled()){
			mPositionProvider.disableProvider(LocationManager.NETWORK_PROVIDER);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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

	
	private EditText mPositionName;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private TextView mAltitudeText;
	private TextView mLatitudeText;
	private TextView mLongitudeText;
	private GpsReadyIndicator mGpsReady;
	private PositionProvider mPositionProvider;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_position);
		
		mAltitudeText = (TextView) findViewById(R.id.PositionLayoutAltitude);
		mLatitudeText = (TextView) findViewById(R.id.PositionLayoutLatitude);
		mLongitudeText = (TextView) findViewById(R.id.PositionLayoutLongitude);
		mPositionName = (EditText) findViewById(R.id.PositionNameEditText);
		mGpsReady = (GpsReadyIndicator) findViewById(R.id.GpsReadyElemPosition);
		mDbHelper = new GeoDbAdapter(this);
        
		
        Bundle extras = getIntent().getExtras();            
		mPositionId = extras != null ? extras.getLong(GeoDbAdapter.POSITION_ROW_ID) 
		        : null;
		
		setupLocationListener();
		
		
		Button updatePosButton = (Button) findViewById(R.id.UpdatePositionButton);
		updatePosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				
				if(mGpsReady.isValidPosition() == false){
					GeotaggerUtils.showErrorDialog(getString(R.string.error_name), 
												   getString(R.string.gps_not_ready_name),
												   view.getContext());
					return;
				}
				Position pos = new Position(mPositionProvider.getCurrentLocation(), new Date());
				mAltitudeText.setText(pos.getAltitude());
				mLatitudeText.setText(pos.getLatitude());
				mLongitudeText.setText(pos.getLongitude());
			}});
		

		Button okButton = (Button) findViewById(R.id.PositionOKButton);
		okButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if(mPositionId == null){
				mPositionId = mDbHelper.addPosition(mPositionName.getText().toString(), 
													mLatitudeText.getText().toString(), 
													mLongitudeText.getText().toString(), 
													mAltitudeText.getText().toString(),
													new Date());
				}else{
					mDbHelper.updatePosition(mPositionId, 
											 mPositionName.getText().toString(), 
											 mLatitudeText.getText().toString(), 
											 mLongitudeText.getText().toString(), 
								   			 mAltitudeText.getText().toString(),
								   			 new Date());
				}
				Intent result = new Intent();
				result.putExtra(GeoDbAdapter.POSITION_ID_KEY, mPositionId);
				setResult(RESULT_OK, result);
				finish();
				
			}});
		
		Button cancelButton = (Button) findViewById(R.id.PositionCancelButton); 
		cancelButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){				
				setResult(RESULT_CANCELED);
				finish();	
				
			}});
		
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
	
	private void populateFields()
	{
		if(mPositionId == null){
			mPositionName.setText(Position.buildPositionName());	// generate a new name
			return;
		}
		Position pos = mDbHelper.getPositionObj(mPositionId);
		mPositionName.setText(pos.getName());
		mLatitudeText.setText(pos.getLatitude());
		mLongitudeText.setText(pos.getLongitude());
		mAltitudeText.setText(pos.getAltitude());
	}
}
	


