package amorg.labo11.service.xg5ort;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

// Beallitasok kepernyo
public class Preferences extends PreferenceActivity {

	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Bundle data = new Bundle();
		data.putString("results", "");
		Intent intent = new Intent();
		intent.putExtras(data);
		setResult(RESULT_OK, intent);
		finish();
	}
}