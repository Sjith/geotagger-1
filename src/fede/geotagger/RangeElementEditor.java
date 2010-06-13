package fede.geotagger;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RangeElementEditor extends Activity {
	static final private int MENU_VIEW_POSITIONS = Menu.FIRST;
	static final private int MENU_VIEW_RANGES = Menu.FIRST + 1;
	static final private int MENU_EXPORT_TO_XML = Menu.FIRST + 2;
	static final private int MENU_EXPORT_TO_GPX = Menu.FIRST + 3;	
	static final private int MENU_OPTIONS = Menu.FIRST + 4;
	static final private int MENU_CLEAN_ALL = Menu.FIRST + 5;
	
	static final private int CHOOSE_POSITION_ACTION = 1;
	static final private int SHOW_PREFERENCES = 2;
	
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private Long mRangeRowId;
	private PositionListElem mChoosenPosition;

	
	private GpsReadyIndicator mGpsReady;
	private PositionProvider mPositionProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.range_edit);
		
		mChoosenPosition = (PositionListElem) findViewById(R.id.ChoosenPositionElem);
		mFromRange = (EditText) findViewById(R.id.FromEditText);
		mToRange = (EditText) findViewById(R.id.ToEditText);
		mGpsReady = (GpsReadyIndicator) findViewById(R.id.GpsReadyElemEditor);
		mDbHelper = new GeoDbAdapter(this);
        
		
        Intent i = getIntent();
  		Bundle extras = i.getExtras();            
		mRangeRowId = extras != null ? extras.getLong(GeoDbAdapter.ROW_ID) 
		        : null;
		
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
		// BUTTONS
		Button choosePosButton = (Button) findViewById(R.id.GetPositionButton);
		choosePosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				Intent i = new Intent(RangeElementEditor.this, PositionList.class);
		        startActivityForResult(i, CHOOSE_POSITION_ACTION);
			}});
		
		Button createNewPosButton = (Button) findViewById(R.id.AddNewPositionButton);
		createNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if(autoAddNewPosition()){
					populatePosition();
				}
			}});
		
		Button autoSetNewPosButton = (Button) findViewById(R.id.FastAddNewPositionButton);
		autoSetNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				if(autoAddNewPosition()){
					populatePosition();
					finalizeRange();
				};
			}});
		
		Button okButton = (Button) findViewById(R.id.RangeElemOkButton);
		okButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				finalizeRange();
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
		
		int groupId = 0;
		int menuItemId = MENU_VIEW_POSITIONS;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.positions_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.posmenu);
		
		groupId = 0;
		menuItemId = MENU_VIEW_RANGES;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.ranges_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.rangesmenu);
		
		
		SubMenu sub = menu.addSubMenu(0, 0, Menu.NONE, R.string.export_name);  
		sub.setIcon(R.drawable.exportxml);

		menuItemId = MENU_EXPORT_TO_XML;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.export_to_xml_name;
		sub.add(groupId, menuItemId, menuItemOrder, menuItemText);//.setIcon(R.drawable.exportxml);

		menuItemId = MENU_EXPORT_TO_GPX;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.export_to_gpx_name;
		sub.add(groupId, menuItemId, menuItemOrder, menuItemText);//.setIcon(R.drawable.exportxml);
		
		
		menuItemId = MENU_CLEAN_ALL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.clean_all_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.trash);

		menuItemId = MENU_OPTIONS;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.options_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case MENU_VIEW_POSITIONS:{
				Intent i = new Intent(this, PositionListModifier.class);
		        startActivity(i);
			break;
			}
			case MENU_VIEW_RANGES:{
				Intent i = new Intent(this, RangeList.class);
		        startActivity(i);
		    break;
			}
			case MENU_EXPORT_TO_XML:{
				if(mDbHelper.storeToXml(GeoDbAdapter.buildOutputFileName("xml"))){
					GeotaggerUtils.showErrorDialog(getString(R.string.xml_exported_name), getString(R.string.export_to_xml_name), this);
				}else{
					GeotaggerUtils.showErrorDialog(getString(R.string.xml_failed_name), getString(R.string.export_to_xml_name), this);
				}
		    break;
			}
			case MENU_EXPORT_TO_GPX:{
				if(mDbHelper.storeToGpx(GeoDbAdapter.buildOutputFileName("gpx"))){
					GeotaggerUtils.showErrorDialog(getString(R.string.xml_exported_name), getString(R.string.export_to_xml_name), this);
				}else{
					GeotaggerUtils.showErrorDialog(getString(R.string.xml_failed_name), getString(R.string.export_to_xml_name), this);
				}
		    break;
			}
			case MENU_CLEAN_ALL:{
				cleanAll();
		    break;
			}
			case MENU_OPTIONS:{
				Intent i = new Intent(this, GeoPreferences.class); 
				startActivityForResult(i, SHOW_PREFERENCES);
			}
		}
	
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
		return true;
	}
	
	private void populateFields()
	{
		if(mRangeRowId == null){	// its a new record. Try to make the user's life easier
			Long newMaxRange = mDbHelper.getMaxEndRange() + 1;
			mFromRange.setText(newMaxRange.toString());
			mToRange.setText(newMaxRange.toString());
			return;
		}
		
		mFromRange.setEnabled(false); // not editable
		mToRange.setEnabled(false);
		
		Cursor myRange = mDbHelper.getRange(mRangeRowId);
		if(myRange == null){
			return;
		}
        startManagingCursor(myRange);
        mFromRange.setText(myRange.getString(GeoDbAdapter.START_RANGE_COLUMN));
        mToRange.setText(myRange.getString(GeoDbAdapter.END_RANGE_COLUMN));
        mPositionId = myRange.getLong(GeoDbAdapter.POSITION_ID_COLUMN);
        populatePosition();        
	}
	
	private void populatePosition(){
		if(mPositionId == null){
			return;
		}
		Position pos = mDbHelper.getPositionObj(mPositionId.longValue());
        mChoosenPosition.setValues(pos.getName(), pos.getLatitude(), pos.getLongitude());
	}
	
	
	
	private boolean checkRanges(Long fromRange, Long toRange)
	{	
		if(mRangeRowId != null){	// cant edit ranges. There is no point in checking them
			return true;
		}
		
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
				
		if(mRangeRowId == null){
			mDbHelper.addRange(fromRange.intValue(), toRange.intValue(), mPositionId.intValue());
		}else{
			mDbHelper.updateRange(mRangeRowId, fromRange.intValue(), toRange.intValue(), mPositionId.intValue());
		}
		return true;		
	}

	private void finalizeRange(){
		if(checkAndAddRange()){
			if(mRangeRowId == null){	// Coming here from the main screen
				mPositionId = null;
				mChoosenPosition.reset();
				populateFields();				
			}else{						// Coming here from range list
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_CANCELED){
        	return;
        }
        mDbHelper.open();
        Bundle extras = intent.getExtras();
        switch(requestCode) {
	        case CHOOSE_POSITION_ACTION:
	            mPositionId = extras.getLong(GeoDbAdapter.POSITION_ID_KEY);
	            populatePosition();
	            break;
        }
    }
    
    private void cleanAll()
    {
    	String button1String = getString(R.string.ok_name); 
    	String button2String = getString(R.string.cancel_name);
    	AlertDialog.Builder ad = new AlertDialog.Builder(this); 
    	ad.setTitle(R.string.clean_all_name); 
    	ad.setMessage(R.string.are_u_sure_to_clean_name); 
    	ad.setPositiveButton(button1String,
    						 new OnClickListener() { 
	    						public void onClick(DialogInterface dialog, int arg1) {
	    							mDbHelper.removeAll();
	    							populateFields();
	    						} });
    	
    	ad.setNegativeButton(button2String, 
    			             new OnClickListener(){
    								public void onClick(DialogInterface dialog, int arg1) { // do nothing
    						 } });
    	
    	ad.show();    	
    }


}
