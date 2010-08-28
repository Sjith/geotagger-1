package com.fede;

import com.fede.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;

public class GeoTaggerActivityMenu {
	public interface CleanFinished{
		public void onCleanFinished();
	}
	
	static final private int MENU_VIEW_POSITIONS = Menu.FIRST;
	static final private int MENU_VIEW_RANGES = Menu.FIRST + 1;
	static final private int MENU_EXPORT = Menu.FIRST + 2;
		
	static final private int MENU_OPTIONS = Menu.FIRST + 3;
	static final private int MENU_CLEAN_ALL = Menu.FIRST + 4;
	
	static final private int SHOW_PREFERENCES = 2;

	
	private Activity mActivity;
	private GeoDbAdapter mDbHelper;
	private CleanFinished mCleanFinish;
	
	public GeoTaggerActivityMenu(Activity a, GeoDbAdapter adp, CleanFinished cleanFinishInterface){
		mActivity = a;
		mDbHelper = adp;
		mCleanFinish = cleanFinishInterface; 
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {	
		int groupId = 0;
		int menuItemId = MENU_VIEW_POSITIONS;
		int menuItemOrder = Menu.NONE;	 
		int menuItemText = R.string.positions_name;

		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.posmenu);
		
		groupId = 0;
		menuItemId = MENU_VIEW_RANGES;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.ranges_name;
		
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.rangesmenu);
		
		
		menuItemId = MENU_EXPORT;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.export_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.exportxml);
		
		
				
		menuItemId = MENU_CLEAN_ALL;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.clean_all_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(R.drawable.trash);

		menuItemId = MENU_OPTIONS;
		menuItemOrder = Menu.NONE;	 
		menuItemText = R.string.options_name;
		menu.add(groupId, menuItemId, menuItemOrder, menuItemText).setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}
	

	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()){
			case MENU_VIEW_POSITIONS:{
				Intent i = new Intent(mActivity, PositionList.class);
		        mActivity.startActivity(i);
			break;
			}
			case MENU_VIEW_RANGES:{
				Intent i = new Intent(mActivity, RangeList.class);
		        mActivity.startActivity(i);
		    break;
			}
			case MENU_EXPORT:{
				if(GeotaggerUtils.positionMode(mActivity)){
					if(mDbHelper.storeToGpx(GeoDbAdapter.buildOutputFileName("gpx"))){
						GeotaggerUtils.showErrorDialog(mActivity.getString(R.string.gpx_exported_name), 
								mActivity.getString(R.string.export_to_xml_name), mActivity);
					}else{
						GeotaggerUtils.showErrorDialog(mActivity.getString(R.string.xml_failed_name), 
								mActivity.getString(R.string.export_to_xml_name), mActivity);
					}
				}
				
				if(GeotaggerUtils.rangeMode(mActivity)){
					if(mDbHelper.storeToXml(GeoDbAdapter.buildOutputFileName("xml"))){
						GeotaggerUtils.showErrorDialog(mActivity.getString(R.string.xml_exported_name), mActivity.getString(R.string.export_to_xml_name), mActivity);
					}else{
						GeotaggerUtils.showErrorDialog(mActivity.getString(R.string.xml_failed_name), mActivity.getString(R.string.export_to_xml_name), mActivity);
					}
				}
				
		    break;
			}
			case MENU_CLEAN_ALL:{
				cleanAll();
		    break;
			}
			case MENU_OPTIONS:{
				Intent i = new Intent(mActivity, GeoPreferences.class); 
				mActivity.startActivityForResult(i, SHOW_PREFERENCES);
			}
		}
	
		return true;
	}
	
	private void cleanAll()
    {
    	String button1String = mActivity.getString(R.string.ok_name); 
    	String button2String = mActivity.getString(R.string.cancel_name);
    	AlertDialog.Builder ad = new AlertDialog.Builder(mActivity); 
    	ad.setTitle(R.string.clean_all_name); 
    	ad.setMessage(R.string.are_u_sure_to_clean_name); 
    	ad.setPositiveButton(button1String,
    						 new OnClickListener() { 
	    						public void onClick(DialogInterface dialog, int arg1) {
	    							mDbHelper.removeAll();
	    							if(mCleanFinish != null)
	    								mCleanFinish.onCleanFinished();
	    						} });
    	
    	ad.setNegativeButton(button2String, 
    			             new OnClickListener(){
    								public void onClick(DialogInterface dialog, int arg1) { // do nothing
    						 } });
    	
    	ad.show();    	
    }
}
