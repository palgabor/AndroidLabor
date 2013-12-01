package hu.bute.daai.amorg.xg5ort.phoneguard.service;
import hu.bute.daai.amorg.xg5ort.data.LocationData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdateService extends Service
{	
	private LocationListener locationListener = null;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		switchOnGps();
		updateGsmLocation();
		updateGpsLocation();
		return START_REDELIVER_INTENT;
	}
	
	private void switchOnGps()
	{
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == true)
        {
        	return;
        }

        final Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("3"));
        sendBroadcast(intent);
        Log.d("MyTag","GPS turned on");
	}
	
	private void updateGsmLocation()
	{
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		final LocationData locationData = LocationData.getInstance();

		GsmCellLocation location = (GsmCellLocation)telephonyManager.getCellLocation();
		if(location != null)
		{
			locationData.setCellId(location.getCid());
			locationData.setLac(location.getLac());
		}
		
		List<NeighboringCellInfo> cellInfo = telephonyManager.getNeighboringCellInfo();
		for(NeighboringCellInfo info: cellInfo)
		{
			locationData.add(info.getCid());
		}
	}
	private void updateGpsLocation()
	{
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationListener = new GpsLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(locationListener != null)
		{
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationListener);
		}
	}
	
	private class GpsLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{	
			LocationData locationData = LocationData.getInstance();
			locationData.setLatitude(location.getLatitude());
			locationData.setLongitude(location.getLongitude());
			locationData.setSpeed(location.getSpeed());
			locationData.setAccuracy(location.getAccuracy());
			locationData.setTime(location.getTime());
			
			Toast.makeText(getApplicationContext(), "Location data changed to: " + location.getLatitude() + 
					";" + location.getLongitude(), Toast.LENGTH_LONG).show();
			
			Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
			try
			{
				Log.d("PhoneGuardTag","Address list.");
				List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if(addresses.size() > 0)
				{
					locationData.setAddress(addresses.get(0).getLocality());
					
				}
				Toast.makeText(getApplicationContext(), "Locality: " + locationData.getAddress(), Toast.LENGTH_LONG).show();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
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
	}
}

