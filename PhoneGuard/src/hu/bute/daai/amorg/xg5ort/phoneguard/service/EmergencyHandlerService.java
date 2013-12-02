package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.DeviceData;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class EmergencyHandlerService extends Service
{	
	SharedPreferences preferences;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String action = intent.getAction();
		if(action.equals(Constants.ACTION_STOP_EMERGENCY_SMS))
		{
			Editor editor = preferences.edit();
			editor.putBoolean(Constants.SP_IS_EMERGENCY_STATE, false);
			editor.putInt(Constants.SP_TIME_TICKS, 0);
			editor.commit();
			
			stopSelf();
		}
		else if(action.equals(Constants.ACTION_START_EMERGENCY_SMS))
		{
			refreshTime(intent);
			startEmergencyState(intent);
			fetchDeviceData();
			fetchLocationData();
			sendSms();
		}
		else if(action.equals(Constants.ACTION_TIME_TICK))
		{
			fetchDeviceData();
			fetchLocationData();
			sendSms();
		}
		
		stopSelf();
		return START_REDELIVER_INTENT;
	}
	
	private void refreshTime(Intent intent)
	{
		int time = intent.getIntExtra("timeValue", 0);
		if(time == 0)
		{
			String timeStr = preferences.getString(Constants.SP_EMERGENCY_FREQUENCY, "");
			if(timeStr.equals(""))
			{
				Editor editor = preferences.edit();
				editor.putString(Constants.SP_EMERGENCY_FREQUENCY, String.valueOf(1));
				editor.commit();
			}
		}
		else
		{
			Editor editor = preferences.edit();
			editor.putString(Constants.SP_EMERGENCY_FREQUENCY, String.valueOf(time));
			editor.commit();
		}
	}
	
	private void startEmergencyState(Intent intent)
	{
		Editor editor = preferences.edit();
		editor.putBoolean(Constants.SP_IS_EMERGENCY_STATE, true);
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
			Editor editor = preferences.edit();
			editor.putString(Constants.SP_IMEI, telephonyManager.getDeviceId());
			editor.putString(Constants.SP_IMSI, telephonyManager.getSubscriberId());
			editor.putString(Constants.SP_OPERATOR_NAME, telephonyManager.getNetworkOperatorName());
			editor.commit();
			
			deviceData.setImei(telephonyManager.getDeviceId());
			deviceData.setImsi(telephonyManager.getSubscriberId());
			deviceData.setOperatorName(telephonyManager.getNetworkOperatorName());
			
			storeDeviceDataToDB();
		}
	}
	
	private void fetchLocationData()
	{
		storeLocationDataToDB();
	}

	private void storeDeviceDataToDB()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String preferredCommunication = preferences.getString(Constants.SP_PREFERRED_COMMUNICATION, "N.A.");
		if(preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET) ||
		   preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET_AND_SMS))
		{
			Intent intent = new Intent(getApplicationContext(),DatabaseService.class);
			intent.setAction(Constants.ACTION_DEVICE_DATA_CHANGED);
			startService(intent);
		}
	}
	
	private void storeLocationDataToDB()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String preferredCommunication = preferences.getString(Constants.SP_PREFERRED_COMMUNICATION, "N.A.");
		if(preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET) ||
		   preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET_AND_SMS))
		{
			Intent intent = new Intent(getApplicationContext(),DatabaseService.class);
			intent.setAction(Constants.ACTION_LOCATION_CHANGED);
			startService(intent);
		}
	}
	
	private void sendSms()
	{
		if(!(preferences.getString(Constants.SP_PREFERRED_COMMUNICATION, "N.A.").equals(Constants.COMMUNICATION_MODE_INTERNET)))
		{ 
			Intent intent = new Intent(getApplicationContext(),SmsSenderService.class);
			startService(intent);
		}
	}
}

