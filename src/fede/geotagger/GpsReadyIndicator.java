package fede.geotagger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GpsReadyIndicator extends LinearLayout{
	private TextView mGpsStatusText;
	private boolean mGpsStatus;
	private boolean mCellStatus;
	
	
	public GpsReadyIndicator(Context context, AttributeSet attr){
		super(context, attr);
		String infService = Context.LAYOUT_INFLATER_SERVICE; 
		LayoutInflater li; 
		li = (LayoutInflater)getContext().getSystemService(infService); 
		li.inflate(R.layout.gps_ready_token, this, true);
		mGpsStatusText = (TextView) findViewById(R.id.GpsReadyText);
		setGpsStatusKo();
	}
	
	public void setGpsStatusOk(){
		mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_ready_color));
		mGpsStatus = true;
	}
	
	public void setGpsStatusKo(){
		if(mCellStatus == true){
			mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_almost_ready_color));
		}else{
			mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_notready_color));
		}
		mGpsStatus = false;
	}
	
	public void setCellStatusOk()
	{
		mCellStatus = true;
		if(mGpsStatus == true){
			mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_ready_color));
		}else{
			mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_almost_ready_color));
		}
	}
	
	public void setCellStatusKo(){
		if(mGpsStatus == false){
			mGpsStatusText.setBackgroundColor(getResources().getColor(R.color.gps_notready_color));
		}
		mCellStatus = false;
	}
	
	public boolean isValidPosition()
	{
		return mGpsStatus || mCellStatus;
	}
}
