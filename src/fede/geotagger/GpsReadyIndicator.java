package fede.geotagger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GpsReadyIndicator extends LinearLayout{
	private TextView mGpsStatus;
	private boolean mStatus;
	
	
	public GpsReadyIndicator(Context context, AttributeSet  attrs){
		super(context, attrs);
		String infService = Context.LAYOUT_INFLATER_SERVICE; 
		LayoutInflater li; 
		li = (LayoutInflater)getContext().getSystemService(infService); 
		li.inflate(R.layout.gps_ready_token, this, true);
		mGpsStatus = (TextView) findViewById(R.id.GpsReadyText);
		statusKo();
	}
	
	public void statusOk(){
		mGpsStatus.setText("Ready");
		mStatus = true;
	}
	public void statusKo(){
		mGpsStatus.setText("Not ready");
		mStatus = false;
	}
	public boolean isOk()
	{
		return mStatus;
	}
}
