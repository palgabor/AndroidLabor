package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class TimeTickReceiver extends BroadcastReceiver
{
	SharedPreferences preferences = null;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		if(isEmergencyState())
		{
			setTimeTickValue(getTimeTickValue() + 1);
			if(!getEmergencyFrequency().equals(""))
			{
				if(getTimeTickValue() >= Integer.parseInt(getEmergencyFrequency()))
				{
					startEmergencyState(context);
					setTimeTickValue(0);
				}
			}
		}
	}
	
	private boolean isEmergencyState()
	{
		return preferences.getBoolean(Constants.SP_IS_EMERGENCY_STATE, false);
	}
	
	private String getEmergencyFrequency()
	{
		return preferences.getString(Constants.SP_EMERGENCY_FREQUENCY, "");
	}
	
	private void startEmergencyState(Context context)
	{
		Intent intent = new Intent();
		intent.setClassName(context,
				"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
		intent.setAction(Constants.ACTION_TIME_TICK);
		context.startService(intent);
	}
	
	private int getTimeTickValue()
	{
		return preferences.getInt(Constants.SP_TIME_TICKS, 0);
		
	}
	
	private void setTimeTickValue(int value)
	{
		Editor editor = preferences.edit();
		editor.putInt(Constants.SP_TIME_TICKS, value);
		editor.commit();
	}
}
