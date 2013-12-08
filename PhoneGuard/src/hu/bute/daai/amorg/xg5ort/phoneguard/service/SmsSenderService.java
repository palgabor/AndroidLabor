package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.phoneguard.data.LocationData;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

public class SmsSenderService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(preferences.getBoolean(Constants.SP_IS_EMERGENCY_STATE, false))
		{
			String message = collectInformation();
			sendSMS(message);
		}
		
		stopSelf();
		return START_REDELIVER_INTENT;
	}
	
	private String collectInformation()
	{
		LocationData locationData = LocationData.getInstance();
		
		String message = "";
		message += "Address:" + locationData.getAddress();
		message += "Lat:" + locationData.getLatitude();
		message += ",Long:" + locationData.getLongitude();
		message += ",Acc:" + locationData.getAccuracy();
		message += ",Speed:" + locationData.getSpeed();
		message += ",CellId:" + locationData.getCellId();
		message += ",LAC:" + locationData.getLac();
		return message;
	}
	
	private void sendSMS(String message)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String phoneNumber = preferences.getString(Constants.SP_PHONE_NUMBER, "");
		SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	}
}
