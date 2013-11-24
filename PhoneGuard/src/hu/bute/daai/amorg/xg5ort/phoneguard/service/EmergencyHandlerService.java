package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.data.LocationData;
import hu.bute.daai.amorg.xg5ort.data.SharedPreferencesConstants;
import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class EmergencyHandlerService extends Service
{
	private LocationListener listener = null;
	
	
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
			Log.d("PhoneGuardTag","Start emergency sms arrived.");
			startEmergencyState(intent);
			fetchDeviceData();
			Log.d("PhoneGuardTag","Device data fetched.");
			fetchLocationData();
			Log.d("PhoneGuardTag","Location data fetched.");
			if(!isInternetAvailable())
			{
				sendSms();
				Log.d("PhoneGuardTag","Result SMS sent.");
			}
			//TODO GPS-t be kell kapcsolni?
			
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
		   deviceData.getMsisdn().equals(telephonyManager.getLine1Number()) == false ||
		   deviceData.getOperatorName().equals(telephonyManager.getNetworkOperatorName()) == false
		  )
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = preferences.edit();
			editor.putString(SharedPreferencesConstants.SENDER_PHONE_NUMBER, telephonyManager.getLine1Number());
			editor.putString(SharedPreferencesConstants.IMEI, telephonyManager.getDeviceId());
			editor.putString(SharedPreferencesConstants.IMSI, telephonyManager.getSubscriberId());
			editor.putString(SharedPreferencesConstants.OPERATOR_NAME, telephonyManager.getNetworkOperatorName());
			editor.commit();
			
			deviceData.setMsisdn(telephonyManager.getLine1Number());
			deviceData.setImei(telephonyManager.getDeviceId());
			deviceData.setImsi(telephonyManager.getSubscriberId());
			deviceData.setOperatorName(telephonyManager.getNetworkOperatorName());
			
			if(isInternetAvailable())
			{
				storeDeviceDataToDB();
			}
		}
	}
	
	private void fetchLocationData()
	{		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		final LocationData locationData = LocationData.getInstance();

		Log.d("PhoneGuardTag","CellId and Lac.");
		GsmCellLocation location = (GsmCellLocation)telephonyManager.getCellLocation();
		if(location != null)
		{
			locationData.setCellId(location.getCid());
			locationData.setLac(location.getLac());
		}
		
		Log.d("PhoneGuardTag","Neighboring cells.");
		List<NeighboringCellInfo> cellInfo = telephonyManager.getNeighboringCellInfo();
		for(NeighboringCellInfo info: cellInfo)
		{
			locationData.add(info.getCid());
		}
		
		listener = new LocationListener()
		{	
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras)
			{
				
			}
			
			@Override
			public void onProviderEnabled(String provider)
			{
						
			}
			
			@Override
			public void onProviderDisabled(String provider)
			{
				
			}
			
			@Override
			public void onLocationChanged(Location location)
			{
				Log.d("PhoneGuardTag","Set GPS values");
				locationData.setLatitude(location.getLatitude());
				locationData.setLongitude(location.getLongitude());
				locationData.setSpeed(location.getSpeed());
				locationData.setAccuracy(location.getAccuracy());
				locationData.setTime(location.getTime());
				
				if(isInternetAvailable())
				{
					Log.d("PhoneGuardTag","Geocoder");
					Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
					try
					{
						Log.d("PhoneGuardTag","Address list.");
						List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						if(addresses.size() > 0)
						{
							locationData.setAddress(addresses.get(0).getLocality());
						}
					}catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
		
		if(isInternetAvailable())
		{
			storeLocationDataToDB();
		}
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
		Intent intent = new Intent();
		intent.setClassName(getApplicationContext(),"hu.bute.daai.amorg.xg5ort.phoneguard.service.SmsSenderService");
		startService(intent);
	}
	
	private boolean isInternetAvailable()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(Integer.parseInt(preferences.getString(SharedPreferencesConstants.PREFERRED_COMMUNICATION, "3")) != 1)
		{
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnectedOrConnecting())
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(listener != null)
		{
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(listener);
		}
	}
}

