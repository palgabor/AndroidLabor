package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.data.SharedPreferencesConstants;
import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class BootHandlerService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		copySharedPrefsToDeviceData();
		checkDeviceData();
		stopSelf();
		return START_REDELIVER_INTENT;
	}

	private void copySharedPrefsToDeviceData()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		DeviceData deviceData = DeviceData.getInstance();
		
		deviceData.setMsisdn(preferences.getString(SharedPreferencesConstants.PHONE_NUMBER, ""));
		deviceData.setImei(preferences.getString(SharedPreferencesConstants.IMEI, ""));
		deviceData.setImsi(preferences.getString(SharedPreferencesConstants.IMSI, ""));
		deviceData.setOperatorName(preferences.getString(SharedPreferencesConstants.OPERATOR_NAME, ""));
	}
	
	private void checkDeviceData()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(preferences.getBoolean(SharedPreferencesConstants.IS_EMERGENCY_STATE, true))
		{
			Context context = getApplicationContext();
			Intent intent = new Intent();
			intent.setClassName(context,
					"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
			intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
			intent.putExtra("timeValue", 0);
			context.startService(intent);
			return;
		}
		
		DeviceData deviceData = DeviceData.getInstance();
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		if(deviceData.getMsisdn().equals(telephonyManager.getLine1Number()) &&
		   deviceData.getImei().equals(telephonyManager.getDeviceId()) &&
		   deviceData.getImsi().equals(telephonyManager.getSubscriberId()) &&
		   deviceData.getOperatorName().equals(telephonyManager.getNetworkOperatorName())
		  )
		{
			return;
		}
		else
		{
			Context context = getApplicationContext();
			Intent intent = new Intent();
			intent.setClassName(context,
					"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
			intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
			intent.putExtra("timeValue", 0);
			context.startService(intent);
		}
		
	}
}
