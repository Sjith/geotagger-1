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

public class PositionListModifier extends ListActivity{
	static final private int MENU_ADD = Menu.FIRST;
	static final private int MENU_EDIT = Menu.FIRST + 1;
	static final private int MENU_DEL = Menu.FIRST + 2;
	static final private int MENU_NONE = Menu.FIRST + 3;
	
    private static final int POSITION_CREATE =0;
    private static final int POSITION_EDIT = 1;
    
    private SimpleCursorAdapter mPositionsAdapter;
    protected GeoDbAdapter mDbHelper;
	
	
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
	protected void onPause() {		
		super.onPause();
		mDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mDbHelper.open();		
	}
    

    
	// 			MENUS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		int menuItemId = MENU_ADD;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.add_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText);		
		return true;
	}
	
	@Override public void onCreateContextMenu(ContextMenu menu,
												View v,
												ContextMenu.
												ContextMenuInfo menuInfo) 
	{ 
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getString(R.string.selected_position_name)); 
		menu.add(0, MENU_EDIT, Menu.NONE, R.string.edit_name);
		menu.add(0, MENU_DEL, Menu.NONE, R.string.cancel_name);
		menu.add(0, MENU_NONE, Menu.NONE, R.string.back_name);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
        case MENU_ADD:
        	addPosition();
            return true;
	    }		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		
		AdapterView.AdapterContextMenuInfo menuInfo; 
		menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); 
		Long elemIndex = new Long(menuInfo.id);
		
		switch (item.getItemId()) { 
			case (MENU_DEL): {
				deletePosition(elemIndex); 
				// TODO show dialog couldnt delete position
				fillData();
				return true;
			}
			case (MENU_EDIT): {
				editPosition(elemIndex); 
				return true;
			}
			case (MENU_NONE): {
				// nothing
			}
		}
	return false;
	}
	
	private void editPosition(Long positionId){
		Intent i = new Intent(this, PositionEditor.class);
		i.putExtra(GeoDbAdapter.POSITION_ROW_ID, positionId);
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
	
	private void deletePosition(Long positionId){
		if(isSureToDelete()){
			if(mDbHelper.removePosition(positionId) == false){ // position still in use, cant be deleted
				// TODO dialog position cant be deleted
			}
		}
	}
	

	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
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
        mPositionsAdapter = 
        	    new SimpleCursorAdapter(this, R.layout.position_list_elem, positionCursor, from, to);
        setListAdapter(mPositionsAdapter);
    }
}