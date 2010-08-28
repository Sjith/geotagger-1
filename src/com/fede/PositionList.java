package com.fede;

import com.fede.R;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class PositionList extends ListActivity {

	protected GeoDbAdapter mDbHelper;
	protected Long mPositionId;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_list);
        mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
    
	@Override
	protected void onPause() {		
		super.onPause();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();		
	}
    
    	
    private void fillData(){
    	Cursor positionCursor = mDbHelper.getAllPositions();
    	startManagingCursor(positionCursor);
    	// Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{GeoDbAdapter.POSITION_NAME_KEY, 
        							 GeoDbAdapter.POSITION_LATITUDE_KEY,
        							 GeoDbAdapter.POSITION_LONGITUDE_KEY};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.position_elem_name, 
        					 R.id.position_elem_latitude,
        					 R.id.position_elem_longitude};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter positions = 
        	    new SimpleCursorAdapter(this, R.layout.position_list_elem, positionCursor, from, to);
        setListAdapter(positions);
    }
}