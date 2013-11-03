package hu.bute.daai.amorg.multiactivityapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends Activity
{
	public static final String MYTAG = "DEMOTAG";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(MYTAG,"Call SecondActivity.onCreate()");
		
		setContentView(R.layout.activity_second);
		
		TextView tvShow = (TextView)findViewById(R.id.show1from2);
		tvShow.setOnClickListener(new OnClickListener() 
		{ 
			public void onClick(View v)
			{
				finish();
			}
		});
		
		TextView tvShow3 = (TextView)findViewById(R.id.show3from2);
		tvShow3.setOnClickListener(new OnClickListener() 
		{ 
			public void onClick(View v)
			{
				EditText et = (EditText)findViewById(R.id.editText);
				if(et.getText().toString().matches(""))
				{
					String errorText = getString(R.string.err_personal_id_not_empty);
					et.setError(errorText);
				}
				else
				{
					et.setError(null);
					Intent myIntent = new Intent();
					myIntent.setClass(SecondActivity.this, ThirdActivity.class);
					startActivity(myIntent);
				}
			}
		});
	
	}
	
	public void onStart()
	{
		super.onStart();
		Log.d(MYTAG,"Call SecondActivity.onStart()");
	}
	
	public void onResume()
	{
		super.onResume();
		Log.d(MYTAG,"Call SecondActivity.onResume()");
	}
	
	public void onPause()
	{
		super.onPause();
		Log.d(MYTAG,"Call SecondActivity.onPause()");
	}
	
	public void onStop()
	{
		super.onStop();
		Log.d(MYTAG,"Call SecondActivity.onStop()");
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(MYTAG,"Call SecondActivity.onDestroy()");
	}
}
