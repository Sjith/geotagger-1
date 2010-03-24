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
import android.widget.TextView;

public class RangeElementEditor extends Activity {
	static final private int MENU_OK = Menu.FIRST;
	static final private int MENU_CANCEL = Menu.FIRST + 1;
	
	static final private int NEW_POSITION_ACTION = 1;
	static final private int CHOOSE_POSITION_ACTION = 2;
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private Long mPositionId;
	private Long mRangeRowId;
	private TextView mChoosenPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.range_edit);
		
		mChoosenPosition = (TextView) findViewById(R.id.ChoosenPosition);
		mFromRange = (EditText) findViewById(R.id.FromEditText);
		mToRange = (EditText) findViewById(R.id.ToEditText);
		
		mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
		
		Bundle extras = getIntent().getExtras();            
		mRangeRowId = extras != null ? extras.getLong(GeoDbAdapter.ROW_ID) 
		        : null;
		
		populateFields();
	
		Button choosePosButton = (Button) findViewById(R.id.GetPositionButton);
		choosePosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				// TODO CHOOSE POSITION FROM POSITION LIST VIEW
			}});
		
		Button createNewPosButton = (Button) findViewById(R.id.AddNewPositionButton);
		createNewPosButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				Intent i = new Intent(RangeElementEditor.this, PositionEditor.class);
		        startActivityForResult(i, NEW_POSITION_ACTION);
			}});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_OK;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.ok_name;

		MenuItem okItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_CANCEL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.cancel_name;
		
		MenuItem cancelItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		return true;
	}

	private void populateFields(){
		if(mRangeRowId == null)
			return;
		Cursor myRange = mDbHelper.getRange(mRangeRowId);
        startManagingCursor(myRange);
        mFromRange.setText(myRange.getString(GeoDbAdapter.START_RANGE_COLUMN));
        mToRange.setText(myRange.getString(GeoDbAdapter.END_RANGE_COLUMN));
        mPositionId = myRange.getLong(GeoDbAdapter.POSITION_ID_COLUMN);
        populatePositionName();        
	}
	
	private void populatePositionName(){
		if(mPositionId == null){
			return;
		}
		Cursor choosenPosition = mDbHelper.getPosition(mPositionId.longValue());
		startManagingCursor(choosenPosition);
        mChoosenPosition.setText(choosenPosition.getString(GeoDbAdapter.POSITION_NAME_COLUMN));
	}
	
	
	
	private boolean checkAndAddRange(){
		if(mPositionId == 0){
			Dialog d = new Dialog(RangeElementEditor.this);
			Window window = d.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			d.setTitle(R.string.error_name);
			d.setContentView(R.layout.text_dialog);
			d.show();
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
			case MENU_OK:
				if(checkAndAddRange()){
	                setResult(RESULT_OK);
	                finish();
				}else{
					setResult(Activity.RESULT_CANCELED);
					finish();
				}
			return true;			
			case MENU_CANCEL:
				setResult(Activity.RESULT_CANCELED);
				finish();	
			return true;
		}
	
		return true;
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch(requestCode) {
	        case NEW_POSITION_ACTION:
	        case CHOOSE_POSITION_ACTION:
	            mPositionId = extras.getLong(GeoDbAdapter.POSITION_ID_KEY);
	            populatePositionName();
	            break;
        }
    }

}
