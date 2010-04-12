package fede.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PositionEditor extends Activity {
	static final private int MENU_OK = Menu.FIRST;
	static final private int MENU_CANCEL = Menu.FIRST + 1;
	
	private EditText mPositionName;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private String mLatitude;
	private String mLongitude;
	private String mAltitude;
	private TextView mAltitudeText;
	private TextView mLatitudeText;
	private TextView mLongitudeText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_position);
		
		mAltitudeText = (TextView) findViewById(R.id.PositionLayoutAltitude);
		mLatitudeText = (TextView) findViewById(R.id.PositionLayoutLatitude);
		mLongitudeText = (TextView) findViewById(R.id.PositionLayoutLongitude);
		mPositionName = (EditText) findViewById(R.id.PositionNameEditText);
		
		mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
		
        Bundle extras = getIntent().getExtras();            
		mPositionId = extras != null ? extras.getLong(GeoDbAdapter.POSITION_ROW_ID) 
		        : null;
		
		populateFields();
		
		Button updatePosButton = (Button) findViewById(R.id.UpdatePositionButton);
		updatePosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				mLatitude = "latitudeName";
				mLongitude = "longitudeName";
				mAltitude = "altitudeName";
				// TODO Take real location from gps / cell / whatever
		
				mAltitudeText.setText(mAltitude);
				mLatitudeText.setText(mLatitude);
				mLongitudeText.setText(mLongitude);
			}});
		

		Button okButton = (Button) findViewById(R.id.PositionOKButton);
		okButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if(mPositionId == null){
				mPositionId = mDbHelper.addPosition(mPositionName.getText().toString(), 
													mLatitudeText.getText().toString(), 
													mLongitudeText.getText().toString(), 
													mAltitudeText.getText().toString());
				}else{
					mDbHelper.updatePosition(mPositionId, 
											 mPositionName.getText().toString(), 
											 mLatitudeText.getText().toString(), 
											 mLongitudeText.getText().toString(), 
								   			 mAltitudeText.getText().toString());
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

	
	
	private void populateFields(){
		if(mPositionId == null){
			return;
		}
		Position pos = mDbHelper.getPositionObj(mPositionId);
		mPositionName.setText(pos.getName());
		mLatitudeText.setText(pos.getLatitude());
		mLongitudeText.setText(pos.getLongitude());
		mAltitudeText.setText(pos.getAltitude());
	}
}
	


