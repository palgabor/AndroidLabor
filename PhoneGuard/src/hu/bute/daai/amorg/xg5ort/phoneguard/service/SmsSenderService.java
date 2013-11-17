package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
		//TODO send SMS
		//TODO delete sent SMS
		//TODO AsyncTask
		stopSelf();
		return START_REDELIVER_INTENT;
	}
}
