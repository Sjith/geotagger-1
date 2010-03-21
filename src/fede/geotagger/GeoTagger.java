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

public class GeoTagger extends ListActivity {

	private GeoDbAdapter mDbHelper;
	private long mItemId;
	
	static final private int MENU_ADD = Menu.FIRST;
	static final private int MENU_EDIT = Menu.FIRST + 1;
	static final private int MENU_DEL = Menu.FIRST + 2;
	
    private static final int RANGE_CREATE=0;
    private static final int RANGE_EDIT=1;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_list);
        mDbHelper = new GeoDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mItemId = id;
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_ADD;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.ok_name;

		MenuItem okItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_EDIT;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.edit_name;
		
		MenuItem editItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		groupId = 0;
		menuItemId = MENU_DEL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.cancel_name;
		
		MenuItem cancelItem = menu.add(groupId, menuItemId, menuItemOrder, menuItemText);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
        case MENU_ADD:
            createRange();
            return true;
        }
		
		return super.onOptionsItemSelected(item);
	}
	
	private void createRange(){
		Intent i = new Intent(this, RangeElementEditor.class);
        startActivityForResult(i, RANGE_CREATE);
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
        String[] from = new String[]{GeoDbAdapter.START_RANGE_KEY, GeoDbAdapter.END_RANGE_KEY};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.range_elem_from, R.id.range_elem_to};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
        	    new SimpleCursorAdapter(this, R.layout.range_elem, positionCursor, from, to);
        setListAdapter(notes);
    }
}