package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class EmergencyHandlerService extends Service
{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		if(action.equals(SmsParser.ACTION_STOP_EMERGENCY_STR))
		{
			stopSelf();
		}
		else if(action.equals(SmsParser.ACTION_EMERGENCY_STR))
		{
			fetchValues(intent);
		}
		return START_REDELIVER_INTENT;
	}
	
	public void fetchValues(Intent intent)
	{
		int time = intent.getIntExtra("timeValue", 0);
		if(time == 0)
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			time = Integer.parseInt(preferences.getString(DatabaseService.EMERGENCY_FREQUENCY, ""));
		}
		
		Log.d("Service: ", "time: " + time);
		
		//TODO fetch location data (GPS, cell, network?)
		//TODO fetch telephone data (IMEI, IMSI)
		//TODO start database service with the fetched data
		//TODO do these periodically according to the time variable
		//TODO AsyncTask
	}
}
