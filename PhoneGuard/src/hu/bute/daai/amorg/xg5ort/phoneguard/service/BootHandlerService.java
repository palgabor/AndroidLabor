package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.DeviceData;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class BootHandlerService extends Service
{
	SharedPreferences preferences;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		startLocationUpdateService();
		copySharedPrefsToDeviceData();
		checkDeviceData();
		stopSelf();
		return START_REDELIVER_INTENT;
	}

	private void copySharedPrefsToDeviceData()
	{
		DeviceData deviceData = DeviceData.getInstance();
		
		deviceData.setImei(preferences.getString(Constants.SP_IMEI, ""));
		deviceData.setImsi(preferences.getString(Constants.SP_IMSI, ""));
		deviceData.setOperatorName(preferences.getString(Constants.SP_OPERATOR_NAME, ""));
	}
	
	private void checkDeviceData()
	{
		if(preferences.getBoolean(Constants.SP_IS_EMERGENCY_STATE, true))
		{
			startEmergencyHandlerService();
			return;
		}
		
		DeviceData deviceData = DeviceData.getInstance();
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		if(deviceData.getImei().equals(telephonyManager.getDeviceId()) &&
		   deviceData.getImsi().equals(telephonyManager.getSubscriberId()) &&
		   deviceData.getOperatorName().equals(telephonyManager.getNetworkOperatorName())
		  )
		{
			return;
		}
		else
		{
			startEmergencyHandlerService();
		}
	}
	
	private void startLocationUpdateService()
	{
		if(!preferences.getBoolean(Constants.SP_IS_LOCATION_UPDATE_SERVICE_RUNNING, false))
		{
			Intent intent = new Intent();
			intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.LocationUpdateService");
			startService(intent);
		}
	}
	
	private void startEmergencyHandlerService()
	{
		Context context = getApplicationContext();
		Intent intent = new Intent();
		intent.setClassName(context,
				"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
		intent.setAction(Constants.ACTION_START_EMERGENCY_SMS);
		intent.putExtra("timeValue", 0);
		context.startService(intent);
	}
}
