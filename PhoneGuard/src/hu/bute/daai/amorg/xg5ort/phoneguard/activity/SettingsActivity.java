package hu.bute.daai.amorg.xg5ort.phoneguard.activity;

import hu.bute.daai.amorg.xg5ort.phoneguard.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		//TODO don't let to exit before fill phone number and password fielads
	}
}