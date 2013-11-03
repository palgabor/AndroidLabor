package hu.bute.daai.amorg.multiactivityapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ThirdActivity extends Activity
{
	public static final String MYTAG = "DEMOTAG";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(MYTAG,"Call ThirdActivity.onCreate()");
		
		setContentView(R.layout.activity_third);
		
		TextView tvShow = (TextView)findViewById(R.id.show2from3);
		tvShow.setOnClickListener(new OnClickListener() 
		{ 
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	public void onStart()
	{
		super.onStart();
		Log.d(MYTAG,"Call ThirdActivity.onStart()");
	}
	
	public void onResume()
	{
		super.onResume();
		Log.d(MYTAG,"Call ThirdActivity.onResume()");
	}
	
	public void onPause()
	{
		super.onPause();
		Log.d(MYTAG,"Call ThirdActivity.onPause()");
	}
	
	public void onStop()
	{
		super.onStop();
		Log.d(MYTAG,"Call ThirdActivity.onStop()");
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(MYTAG,"Call ThirdActivity.onDestroy()");
	}
}
