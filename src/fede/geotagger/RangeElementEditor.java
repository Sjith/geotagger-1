package fede.geotagger;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class RangeElementEditor extends Activity {
	static final private int MENU_OK = Menu.FIRST;
	static final private int MENU_CANCEL = Menu.FIRST + 1;
	
	private EditText mFromRange;
	private EditText mToRange;
	private GeoDbAdapter mDbHelper;
	private int mPositionId;
	private int mRangeRowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.range_edit);
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

	private boolean checkAndAddRange(){
		if(mPositionId == 0)
			return false;
		
		Long fromRange = Long.parseLong(mFromRange.getText().toString());
		Long toRange = Long.parseLong(mToRange.getText().toString());
		// TODO Check Ranges
		// TODO Check Action		
		mDbHelper.addRange(fromRange.intValue(), toRange.intValue(), mPositionId);
		return true;		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case MENU_OK:
				checkAndAddRange();
			return true;			
			case MENU_CANCEL:
			// TODO HANDLE CANCEL CASE	
			return true;
		}
	
		return true;
	}
	
	

}
