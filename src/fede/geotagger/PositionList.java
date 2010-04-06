package fede.geotagger;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
    
    private void selectPosDialog(){
    	if(mPositionId == null){
    		return;
    	}
    	Context context = PositionList.this; 
    	String title = getString(R.string.are_u_sure_name); 
    	Position pos = mDbHelper.getPositionObj(mPositionId);
    	if(pos == null){
    		return;
    	}
    	String message = getString(R.string.choose_position_name) + " " + pos.getName(); 
    	String button1String = getString(R.string.ok_name); 
    	String button2String = getString(R.string.cancel_name);
    	AlertDialog.Builder ad = new AlertDialog.Builder(context); 
    	ad.setTitle(title); 
    	ad.setMessage(message); 
    	ad.setPositiveButton(button1String,
    						 new OnClickListener() { 
	    						public void onClick(DialogInterface dialog, int arg1) {
	    							Intent result = new Intent();
	    							result.putExtra(GeoDbAdapter.POSITION_ID_KEY, mPositionId);
	    					        setResult(RESULT_OK, result);
	    					        finish();
	    						} });
    	
    	ad.setNegativeButton(button2String, 
    			             new OnClickListener(){
    								public void onClick(DialogInterface dialog, int arg1) { // do nothing
    						 } });
    	
    	ad.show();
    	
    	
    	return;
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mPositionId = id;
		selectPosDialog();
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