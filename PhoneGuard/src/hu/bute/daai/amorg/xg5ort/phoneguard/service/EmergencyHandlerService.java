package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.data.SharedPreferencesConstants;
import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
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
		if(action.equals(SmsParser.ACTION_STOP_EMERGENCY_SMS))
		{
			Log.d("PhoneGuardTag","Stop emergency sms arrived.");
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesConstants.IS_EMERGENCY_STATE, false);
			editor.commit();
			
			stopSelf();
		}
		else if(action.equals(SmsParser.ACTION_EMERGENCY_SMS))
		{
			startEmergencyState(intent);
			fetchDeviceData();
			fetchLocationData();
			sendSms();
			//TODO do these periodically according to the time variable
			//TODO do these periodically according to the settings (can be changed during emergency situation)
		}
		return START_REDELIVER_INTENT;
	}
	
	private void startEmergencyState(Intent intent)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		int time = intent.getIntExtra("timeValue", 0);
		if(time == 0)
		{
			String timeStr = preferences.getString(SharedPreferencesConstants.EMERGENCY_FREQUENCY, "");
			if(timeStr.equals("") == false)
			{
				time = Integer.parseInt(timeStr);
			}
			//TODO be careful, time variable can be empty here (0 as a default) 
		}
		else
		{
			Editor editor = preferences.edit();
			editor.putString(SharedPreferencesConstants.EMERGENCY_FREQUENCY, String.valueOf(time));
			editor.commit();
		}
		//TODO refresh emergency requests according to this value
		
		Editor editor = preferences.edit();
		editor.putBoolean(SharedPreferencesConstants.IS_EMERGENCY_STATE, true);
		editor.commit();
	}
	
	private void fetchDeviceData()
	{
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);	
		final DeviceData deviceData = DeviceData.getInstance();
		
		if(deviceData.getImei().equals(telephonyManager.getDeviceId()) == false || 
		   deviceData.getImsi().equals(telephonyManager.getSubscriberId()) == false ||
		   deviceData.getOperatorName().equals(telephonyManager.getNetworkOperatorName()) == false
		  )
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = preferences.edit();
			editor.putString(SharedPreferencesConstants.IMEI, telephonyManager.getDeviceId());
			editor.putString(SharedPreferencesConstants.IMSI, telephonyManager.getSubscriberId());
			editor.putString(SharedPreferencesConstants.OPERATOR_NAME, telephonyManager.getNetworkOperatorName());
			editor.commit();
			
			deviceData.setImei(telephonyManager.getDeviceId());
			deviceData.setImsi(telephonyManager.getSubscriberId());
			deviceData.setOperatorName(telephonyManager.getNetworkOperatorName());
			
			storeDeviceDataToDB();
		}
	}
	
	private void fetchLocationData()
	{
		Intent intent = new Intent();
		intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.LocationUpdateService");
		startService(intent);
		storeLocationDataToDB();
	}

	private void storeDeviceDataToDB()
	{
		Intent intent = new Intent();
		intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
		intent.setAction(DatabaseService.ACTION_DEVICE_DATA_CHANGED);
		startService(intent);
	}
	
	private void storeLocationDataToDB()
	{
		Intent intent = new Intent();
		intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
		intent.setAction(DatabaseService.ACTION_LOCATION_CHANGED);
		startService(intent);
	}
	
	private void sendSms()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!(preferences.getString(SharedPreferencesConstants.PREFERRED_COMMUNICATION, "N.A.").equals("1")))
		{ 
			Log.d("MyTag","Result sms sent");
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.SmsSenderService");
			startService(intent);
		}
	}
}

