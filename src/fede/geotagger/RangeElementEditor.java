package fede.geotagger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RangeElementEditor extends Activity {
	static final private int MENU_VIEW_POSITIONS = Menu.FIRST;
	static final private int MENU_VIEW_RANGES = Menu.FIRST + 1;
	static final private int MENU_EXPORT_TO_XML = Menu.FIRST + 2;
	static final private int MENU_CLEAN_ALL = Menu.FIRST + 3;
	
	static final private int NEW_POSITION_ACTION = 1;
	static final private int CHOOSE_POSITION_ACTION = 2;
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private Long mRangeRowId;
	private PositionListElem mChoosenPosition;
	private boolean mFromMain;
	private GpsReadyIndicator mGpsReady;
	private LocationUpdater mLUpdater;
	
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
        mFromMain = (i.getAction() == "android.intent.action.MAIN");
        
		Bundle extras = i.getExtras();            
		mRangeRowId = extras != null ? extras.getLong(GeoDbAdapter.ROW_ID) 
		        : null;
		
		setupLocationListener();
		setupButtons();		
		
	}
	
	private void setupLocationListener()
	{
		mLUpdater = new LocationUpdater(this, new LocationInterface(){
		    public void statusReady (){
		    	mGpsReady.statusOk();
		    }
		    public void statusNotReady(){
		    	mGpsReady.statusKo();
		    }			
		});
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		mLUpdater.stopUpdating();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();
		populateFields();
		mLUpdater.startUpdating();		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
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
				Intent i = new Intent(RangeElementEditor.this, PositionEditor.class);
		        startActivityForResult(i, NEW_POSITION_ACTION);
			}});
		
		Button autoSetNewPosButton = (Button) findViewById(R.id.FastAddNewPositionButton);
		autoSetNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				autoAddNewPosition();
				populatePosition();
				finalizeRange();
			}});
		
		Button okButton = (Button) findViewById(R.id.RangeElemOkButton);
		okButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				finalizeRange();
			}});
	}

	private void autoAddNewPosition(){
		// todo check gps status
		Location l = mLUpdater.getLocation();
		mPositionId = mDbHelper.addPosition(new Position(l));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_VIEW_POSITIONS;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.positions_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_VIEW_RANGES;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.ranges_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		menuItemId = MENU_EXPORT_TO_XML;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.export_to_xml_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		menuItemId = MENU_CLEAN_ALL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.clean_all_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
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
				mDbHelper.storeToXml(getString(R.string.file_name_name));
		    break;
			}
			case MENU_CLEAN_ALL:{
				cleanAll();
		    break;
			}
		}
	
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
	
	private void showErrorDialog(String errorString)
	{
    	Context context = this; 
    	String title = getString(R.string.invalid_range_name); 
    	Position pos = mDbHelper.getPositionObj(mPositionId);
    	if(pos == null){
    		return;
    	} 
    	String button1String = getString(R.string.ok_name); 
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
	
	private boolean checkRanges(Long fromRange, Long toRange)
	{	
		if(mRangeRowId != null){	// cant edit ranges. There is no point in checking them
			return true;
		}
		
		if(fromRange == 0 || toRange == 0){
			showErrorDialog(getString(R.string.invalid_range_name));
			return false;
		}
		
		if(fromRange > toRange){
			showErrorDialog(getString(R.string.invalid_range_name));
			return false;
		}
		
		
		if(mDbHelper.goodRangeBound(fromRange) == false){
			showErrorDialog(getString(R.string.invalid_range_name));	
			return false;
		}
		
		if(mDbHelper.goodRangeBound(toRange) == false){
			showErrorDialog(getString(R.string.invalid_range_name));	
			return false;
		}
		
		return true;
	}
	
	private boolean checkAndAddRange()
	{
		if(mPositionId == null || mPositionId == 0){
			showErrorDialog(getString(R.string.error_name));	
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
				Intent i = new Intent(this, RangeElementEditor.class);
		        startActivity(i);
			}else{						// Coming here from range list
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mDbHelper.open();
        Bundle extras = intent.getExtras();
        switch(requestCode) {
	        case NEW_POSITION_ACTION:
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
	    						} });
    	
    	ad.setNegativeButton(button2String, 
    			             new OnClickListener(){
    								public void onClick(DialogInterface dialog, int arg1) { // do nothing
    						 } });
    	
    	ad.show();    	
    }

}
