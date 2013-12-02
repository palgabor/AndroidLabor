package hu.bute.daai.amorg.xg5ort.phoneguard.activity;

import hu.bute.daai.amorg.xg5ort.phoneguard.R;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!preferences.getBoolean(Constants.SP_IS_LOCATION_UPDATE_SERVICE_RUNNING, false))
		{
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.LocationUpdateService");
			startService(intent);
		}
		//TODO don't let to exit before fill phone number and password filled
	}
}
