package fede.geotagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
				mPositionId = mDbHelper.addPosition(mPositionName.getText().toString(), 
													mLatitudeText.getText().toString(), 
													mLongitudeText.getText().toString(), 
													mAltitudeText.getText().toString());
				
			}});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_OK;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.ok_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_CANCEL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.cancel_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case MENU_OK:
				Intent result = new Intent();
				result.putExtra(GeoDbAdapter.POSITION_ID_KEY, mPositionId);
				setResult(RESULT_OK, result);
				finish();	
			return true;			
			case MENU_CANCEL:
				setResult(RESULT_CANCELED);
				finish();	
			return true;
		}
	
		return true;
	}
	
	

}
