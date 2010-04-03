package fede.geotagger;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PositionList extends ListActivity {

	protected GeoDbAdapter mDbHelper;
	protected Long mPositionId;
	
	static final private int MENU_ADD = Menu.FIRST;
	static final private int MENU_EDIT = Menu.FIRST + 1;
	static final private int MENU_DEL = Menu.FIRST + 2;
	
    private static final int POSITION_CREATE =0;
    private static final int POSITION_EDIT = 1;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_list);
        mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mPositionId = id;
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_ADD;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.add_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_EDIT;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.edit_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_DEL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.cancel_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
        case MENU_ADD:
        	addPosition();
            return true;
		case MENU_EDIT:
			editPosition();
	        return true;
		case MENU_DEL:
			deletePosition();
			return true;
	    }
		
		return super.onOptionsItemSelected(item);
	}
	
	private void editPosition(){
		Intent i = new Intent(this, PositionEditor.class);
		i.putExtra(GeoDbAdapter.POSITION_ROW_ID, mPositionId);
        startActivityForResult(i, POSITION_EDIT);
	}
	
	
	private void addPosition(){
		Intent i = new Intent(this, PositionEditor.class);
        startActivityForResult(i, POSITION_CREATE);
	}
	
	private boolean isSureToDelete(){
		// TODO dialogue are you sure to delete?
		return true;
	}
	
	private void deletePosition(){
		if(isSureToDelete() && mPositionId != null){
			mDbHelper.removePosition(mPositionId);
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
	
    private void fillData(){
    	Cursor positionCursor = mDbHelper.getAllRanges();
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