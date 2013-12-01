package hu.bute.daai.amorg.xg5ort.phoneguard.activity;

import hu.bute.daai.amorg.xg5ort.phoneguard.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Intent intent = new Intent();
		intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.LocationUpdateService");
		startService(intent);
		//TODO don't let to exit before fill phone number and password filled
	}
}
