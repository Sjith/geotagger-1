package com.fede;

import com.fede.R;
import android.content.Context;
import android.content.res.TypedArray;
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
		
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PositionListElem);

        int c = a.getColor(R.styleable.PositionListElem_textColor, 0x000);
        		
		mName = (TextView) findViewById(R.id.position_elem_name);
		mName.setTextColor(c);
		mLatitude = (TextView) findViewById(R.id.position_elem_latitude);
		mLatitude.setTextColor(c);
		mLongitude = (TextView) findViewById(R.id.position_elem_longitude);
		mLongitude.setTextColor(c);
	}
	
	public void setValues(String name, String latitude, String longitude){
		mName.setText(name);
		mLatitude.setText(latitude);
		mLongitude.setText(longitude);
	}
	
	public void reset()
	{
		mName.setText("");
		mLatitude.setText("");
		mLongitude.setText("");
	}
}
