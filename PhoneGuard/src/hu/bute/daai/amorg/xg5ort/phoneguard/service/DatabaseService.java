package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.data.LocationData;

import java.util.Calendar;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

public class DatabaseService extends Service
{	
	public static final String ACTION_SMS_ARRIVED = "SMS ARRIVED";
	public static final String ACTION_LOCATION_CHANGED = "LOCATION CHANGED";
	public static final String ACTION_DEVICE_DATA_CHANGED = "DEVICE DATA CHANGED";
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent.getAction().equals(ACTION_SMS_ARRIVED))
		{
			Bundle extras = intent.getExtras();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(extras.getLong("time"));
			String time = Calendar.getInstance().get(Calendar.YEAR) + "." + Calendar.getInstance().get(Calendar.MONTH) + "." + 
						  Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ". " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
						  Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND);
			
			Parse.initialize(this, "0baLdmQJInwexbzZaqZDf2IVFVbvtI8XY2er4TZk", "rVeek9zXKKpKrU6DjztnbH3Xg43xKDssx6tdQwrH");
			ParseObject receivedSmsTable = new ParseObject("ReceivedSms");
			
			receivedSmsTable.put("Sender", extras.getString("sender"));
			receivedSmsTable.put("Time", time);
			receivedSmsTable.put("SmsMessage", extras.getString("body"));
			receivedSmsTable.put("Action", extras.getString("action"));
			
			receivedSmsTable.saveInBackground();
			Log.d("PhoneGuardTag","Arrived SMS saved to DB.");
		}
		
		if(intent.getAction().equals(ACTION_LOCATION_CHANGED))
		{
			LocationData locationData = LocationData.getInstance();
			
			Parse.initialize(this, "0baLdmQJInwexbzZaqZDf2IVFVbvtI8XY2er4TZk", "rVeek9zXKKpKrU6DjztnbH3Xg43xKDssx6tdQwrH");
			ParseObject locationTable = new ParseObject("Location");
			
			locationTable.put("Time", String.valueOf(locationData.getTime()));
			locationTable.put("Address", locationData.getAddress());
			locationTable.put("Latitude", String.valueOf(locationData.getLatitude()));
			locationTable.put("Longitude", String.valueOf(locationData.getLongitude()));
			locationTable.put("Accuracy", String.valueOf(locationData.getAccuracy()));
			locationTable.put("Speed", String.valueOf(locationData.getSpeed()));
			locationTable.put("CellID", String.valueOf(locationData.getCellId()));
			locationTable.put("LocationAreaID", String.valueOf(locationData.getLac()));
			
			
			int num = 0;
			List<Integer> neighboringCellIds = locationData.getNeighboringCellIds();
			for(int i: neighboringCellIds)
			{
				locationTable.put("NeighboringCell" + num, String.valueOf(i));
				num++;
			}			
			
			locationTable.saveInBackground();
			Log.d("PhoneGuardTag","Location change saved to DB.");
		}
		
		if(intent.getAction().equals(ACTION_DEVICE_DATA_CHANGED))
		{
			DeviceData deviceData = DeviceData.getInstance();
			
			Parse.initialize(this, "0baLdmQJInwexbzZaqZDf2IVFVbvtI8XY2er4TZk", "rVeek9zXKKpKrU6DjztnbH3Xg43xKDssx6tdQwrH");
			ParseObject deviceDataTable = new ParseObject("DeviceData");
			
			deviceDataTable.put("PhoneNumber", deviceData.getMsisdn());
			deviceDataTable.put("IMEInumber", deviceData.getImei());
			deviceDataTable.put("IMSInumber", deviceData.getImsi());
			deviceDataTable.put("OperatorName", deviceData.getOperatorName());
			
			deviceDataTable.saveInBackground();
			Log.d("PhoneGuardTag","Device data saved to DB.");
		}
		
		stopSelf();
		return START_REDELIVER_INTENT;
	}

}
