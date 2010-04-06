package fede.geotagger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

public class PositionListElem extends TableLayout {
	private TextView mName;
	private TextView mLongitude;
	private TextView mLatitude;
	
	
	public PositionListElem(Context context, AttributeSet  attrs){
		super(context, attrs);
		String infService = Context.LAYOUT_INFLATER_SERVICE; 
		LayoutInflater li; 
		li = (LayoutInflater)getContext().getSystemService(infService); 
		li.inflate(R.layout.position_list_elem, this, true);
		mName = (TextView) findViewById(R.id.position_elem_name);
		mLatitude = (TextView) findViewById(R.id.position_elem_latitude);
		mLongitude = (TextView) findViewById(R.id.position_elem_longitude);
	}
	
	public void setValues(String name, String latitude, String longitude){
		mName.setText(name);
		mLatitude.setText(latitude);
		mLongitude.setText(longitude);
	}
}
