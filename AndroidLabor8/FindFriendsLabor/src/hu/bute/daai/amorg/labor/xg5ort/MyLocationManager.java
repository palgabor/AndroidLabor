package hu.bute.daai.amorg.labor.xg5ort;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class MyLocationManager implements LocationListener {

	public static final String BR_LOCATION_INFO = "BR_LOCATION_INFO";
	public static final String KEY_LOCATION = "KEY_LOCATION";
	private Context context;
	private LocationManager locMan;

	public MyLocationManager(Context aContext) {
		context = aContext;
		locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	
	public void startLocationMonitoring() {
		locMan.requestLocationUpdates(
        		LocationManager.GPS_PROVIDER,
        		0, 0, this);
		locMan.requestLocationUpdates(
        		LocationManager.NETWORK_PROVIDER,
        		0, 0, this);
	}
	
	public void stopLocationMonitoring() {
		if (locMan != null) {
			locMan.removeUpdates(this);
    	}
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		Intent intent = new Intent(BR_LOCATION_INFO);
	    intent.putExtra(KEY_LOCATION, location);
	    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}
