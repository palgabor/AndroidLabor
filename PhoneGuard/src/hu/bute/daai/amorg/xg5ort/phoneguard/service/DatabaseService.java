package hu.bute.daai.amorg.xg5ort.phoneguard.service;

import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.LocationData;

import java.util.Calendar;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.parse.Parse;
import com.parse.ParseObject;

public class DatabaseService extends Service
{	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if(intent.getAction().equals(Constants.ACTION_SMS_ARRIVED))
		{
			Bundle extras = intent.getExtras();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(extras.getLong("time"));
			String time = Calendar.getInstance().get(Calendar.YEAR) + "." + Calendar.getInstance().get(Calendar.MONTH) + "." + 
						  Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ". " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
						  Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND);
			
			Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_ID);
			ParseObject receivedSmsTable = new ParseObject("ReceivedSms");
			
			receivedSmsTable.put("Sender", extras.getString("sender"));
			receivedSmsTable.put("Time", time);
			receivedSmsTable.put("SmsMessage", extras.getString("body"));
			receivedSmsTable.put("Action", extras.getString("action"));
			
			receivedSmsTable.saveInBackground();
		}
		
		if(intent.getAction().equals(Constants.ACTION_LOCATION_CHANGED))
		{
			LocationData locationData = LocationData.getInstance();
			
			Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_ID);
			ParseObject locationTable = new ParseObject("Location");
			
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
		}
		
		if(intent.getAction().equals(Constants.ACTION_DEVICE_DATA_CHANGED))
		{
			DeviceData deviceData = DeviceData.getInstance();
			
			Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_ID);
			ParseObject deviceDataTable = new ParseObject("DeviceData");
			
			deviceDataTable.put("IMEInumber", deviceData.getImei());
			deviceDataTable.put("IMSInumber", deviceData.getImsi());
			deviceDataTable.put("OperatorName", deviceData.getOperatorName());
			
			deviceDataTable.saveInBackground();
		}
		
		stopSelf();
		return START_REDELIVER_INTENT;
	}
}
