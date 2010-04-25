package fede.geotagger;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

public class RangeList extends ListActivity {

	private GeoDbAdapter mDbHelper;

	
	static final private int MENU_EDIT = Menu.FIRST;
	static final private int MENU_DEL = Menu.FIRST + 1;
	static final private int MENU_NONE = Menu.FIRST + 2;
	
    private static final int RANGE_EDIT=0;
	
	
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
	protected void onPause() {		
		super.onPause();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();		
	}
    
    @Override public void onCreateContextMenu(ContextMenu menu,
			View v,
			ContextMenu.
			ContextMenuInfo menuInfo) 
    { 
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.setHeaderTitle(getString(R.string.selected_range_name)); 
    	menu.add(0, MENU_EDIT, Menu.NONE, R.string.edit_name);
    	menu.add(0, MENU_DEL, Menu.NONE, R.string.cancel_name);
    	menu.add(0, MENU_NONE, Menu.NONE, R.string.back_name);
    }
    
    @Override public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		AdapterView.AdapterContextMenuInfo menuInfo; 
		menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); 
		Long elemIndex = new Long(menuInfo.id);
		
		switch (item.getItemId()) { 
			case (MENU_DEL): {
				deleteRange(elemIndex); 
				fillData();
				return true;
			}
			case (MENU_EDIT): {
				editRange(elemIndex); 
				return true;
			}
			case (MENU_NONE): {
				// nothing
			}
		}
	return false;
	}
    
	private void editRange(Long itemId){
		Intent i = new Intent(this, RangeElementEditor.class);
		i.putExtra(GeoDbAdapter.ROW_ID, itemId);
        startActivityForResult(i, RANGE_EDIT);
	}
	
	private void deleteRange(Long itemId){
		mDbHelper.removeRange(itemId);
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