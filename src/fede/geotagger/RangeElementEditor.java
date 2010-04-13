package fede.geotagger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class RangeElementEditor extends Activity {
	static final private int MENU_VIEW_POSITIONS = Menu.FIRST;
	static final private int MENU_VIEW_RANGES = Menu.FIRST + 1;
	
	static final private int NEW_POSITION_ACTION = 1;
	static final private int CHOOSE_POSITION_ACTION = 2;
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private Long mRangeRowId;
	private PositionListElem mChoosenPosition;
	private boolean mFromMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.range_edit);
		
		mChoosenPosition = (PositionListElem) findViewById(R.id.ChoosenPositionElem);
		mFromRange = (EditText) findViewById(R.id.FromEditText);
		mToRange = (EditText) findViewById(R.id.ToEditText);
		
		mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
		
        Intent i = getIntent();
        mFromMain = (i.getAction() == "android.intent.action.MAIN");
        
		Bundle extras = i.getExtras();            
		mRangeRowId = extras != null ? extras.getLong(GeoDbAdapter.ROW_ID) 
		        : null;
		
		populateFields();
	
		
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
		// TODO get latitude longitude
		mPositionId = mDbHelper.addPosition("Name", "Latitude", "Longitude", "Altitude");
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
		
		return true;
	}

	private void populateFields(){
		if(mRangeRowId == null){	// its a new record. Try to make the user's life easier
			Long newMaxRange = mDbHelper.getMaxEndRange() + 1;
			mFromRange.setText(newMaxRange.toString());
			return;
		}
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
	
	private void showErrorDialog(){
		Dialog d = new Dialog(RangeElementEditor.this);
		Window window = d.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		d.setTitle(R.string.error_name);
		d.setContentView(R.layout.text_dialog);
		d.show();
	}
	
	
	private boolean checkAndAddRange(){
		if(mPositionId == null || mPositionId == 0){
			showErrorDialog();	
			return false;
		}
		
		Long fromRange = Long.parseLong(mFromRange.getText().toString());
		Long toRange = Long.parseLong(mToRange.getText().toString());
		// TODO Check Ranges
		// TODO Check Action
		
		if(mRangeRowId == null){
			mDbHelper.addRange(fromRange.intValue(), toRange.intValue(), mPositionId.intValue());
		}else{
			mDbHelper.updateRange(mRangeRowId, fromRange.intValue(), toRange.intValue(), mPositionId.intValue());
		}
		return true;		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
        Bundle extras = intent.getExtras();
        switch(requestCode) {
	        case NEW_POSITION_ACTION:
	        case CHOOSE_POSITION_ACTION:
	            mPositionId = extras.getLong(GeoDbAdapter.POSITION_ID_KEY);
	            populatePosition();
	            break;
        }
    }

}
