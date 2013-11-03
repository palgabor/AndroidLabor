package hu.bute.daai.amorg.multiactivityapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class MainActivity extends Activity
{
	public static final String MYTAG = "DEMOTAG";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(MYTAG,"Call MainActivity.onCreate()");
		
		setContentView(R.layout.activity_main);
		
		TextView tvShow2 = (TextView)findViewById(R.id.tvShowSecond);
		tvShow2.setOnClickListener(new OnClickListener() 
		{ 
			public void onClick(View v)
			{
				Intent myIntent = new Intent();
				myIntent.setClass(MainActivity.this, SecondActivity.class);
				startActivity(myIntent);
			}
		});
	}
	
	public void onStart()
	{
		super.onStart();
		Log.d(MYTAG,"Call MainActivity.onStart()");
	}
	
	public void onResume()
	{
		super.onResume();
		Log.d(MYTAG,"Call MainActivity.onResume()");
	}
	
	public void onPause()
	{
		super.onPause();
		Log.d(MYTAG,"Call MainActivity.onPause()");
	}
	
	public void onStop()
	{
		super.onStop();
		Log.d(MYTAG,"Call MainActivity.onStop()");
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(MYTAG,"Call MainActivity.onDestroy()");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_exit:
				finish();
				break;
		}
		return true;
	}
}
