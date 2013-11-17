package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DatabaseService extends Service
{
	public static final String USE_INTERNET = "USE_INTERNET";
	public static final String TELEPHONE_NUMBER = "TELEPHONE_NUMBER";
	public static final String PASSWORD = "PASSWORD";
	public static final String EMERGENCY_FREQUENCY = "EMERGENCY_FREQUENCY";
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//TODO handle intent
		//TODO if network available, use BaaS service
		//TODO if network not available, use SmsSenderService (new service)
		//TODO Do we need SQLite?
		//TODO AsyncTask very important!
		stopSelf();
		return START_REDELIVER_INTENT;
	}

}
