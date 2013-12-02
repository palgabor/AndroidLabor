package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import hu.bute.daai.amorg.xg5ort.phoneguard.activity.SettingsActivity;
import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;
import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService;
import hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver
{
	SmsMessage msg = null;
	Context context = null;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		this.context = context;
		SmsParser parser = SmsParser.getInstance(context);
		String sms = readSms(intent);
		int result = parse(parser,sms);
		switchAction(result);	
	}
	
	private String readSms(Intent intent)
	{
		String body = "";
		if(intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED"))
		{
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");

			for (Object pdu : pdus)
			{
				msg = SmsMessage.createFromPdu((byte[])pdu);
				if(msg != null)
				{
					body = msg.getDisplayMessageBody();
				}
			}
		}
		return body;
	}
	
	private int parse(SmsParser parser, String sms)
	{
		int result =  parser.parse(sms);
		if(result == Constants.PHONE_GUARD_SMS)
		{
			abortBroadcast();
			result = parser.findAction(sms);
		}
		return result;
	}
	
	private void switchAction(int result)
	{
		switch(result)
		{
			case Constants.NON_PHONE_GUARD_SMS: 
				break;
			
			case Constants.ACTION_UNKNOWN:
				startDatabaseService(Constants.ACTION_UNKNOWN_STR);
				break;
				
			case Constants.BAD_PASSWORD: 
				startDatabaseService(Constants.BAD_PASSWORD_STR);
				break;
				
			case Constants.ACTION_EMERGENCY_WITHOUT_TIME: 
				startDatabaseService(Constants.ACTION_EMERGENCY_WITHOUT_TIME_STR);
				startEmergencyHandlerService(Constants.ACTION_START_EMERGENCY_SMS,0);
				break;
			
			case Constants.ACTION_STOP_EMERGENCY:
				startDatabaseService(Constants.ACTION_STOP_EMERGENCY_SMS);
				startEmergencyHandlerService(Constants.ACTION_STOP_EMERGENCY_SMS,0);
				break;
			
			case Constants.ACTION_SETTINGS:
				startDatabaseService(Constants.ACTION_SETTINGS_STR);
				startSettingsActivity();
				break;
				
			case Constants.ACTION_START_SMS:
				startDatabaseService(Constants.ACTION_START_SMS_STR);
				setPreferredCommunicationMode(Constants.ACTION_START_SMS);
				break;
				
			case Constants.ACTION_STOP_SMS:
				startDatabaseService(Constants.ACTION_STOP_SMS_STR);
				setPreferredCommunicationMode(Constants.ACTION_STOP_SMS);
				break;
				
			case Constants.ACTION_START_INTERNET:
				startDatabaseService(Constants.ACTION_START_INTERNET_STR);
				setPreferredCommunicationMode(Constants.ACTION_START_INTERNET);
				break;
				
			case Constants.ACTION_STOP_INTERNET:
				startDatabaseService(Constants.ACTION_STOP_INTERNET_STR);
				setPreferredCommunicationMode(Constants.ACTION_STOP_INTERNET);
				break;
				
			default:
				if(result >= Constants.ACTION_EMERGENCY_TIME_BASE)
				{
					startDatabaseService(Constants.ACTION_EMERGENCY_TIME_BASE_STR);
					startEmergencyHandlerService(Constants.ACTION_START_EMERGENCY_SMS,result%Constants.ACTION_EMERGENCY_TIME_BASE);
				}
		}
	}
	
	private void startDatabaseService(String action)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String preferredCommunication = preferences.getString(Constants.SP_PREFERRED_COMMUNICATION, "N.A.");
		if(preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET) ||
		   preferredCommunication.equals(Constants.COMMUNICATION_MODE_INTERNET_AND_SMS))
		{
			Intent intent = new Intent(context,DatabaseService.class);
			intent.setAction(Constants.ACTION_SMS_ARRIVED);
			intent.putExtra("sender", msg.getOriginatingAddress());
			intent.putExtra("time", msg.getTimestampMillis());
			intent.putExtra("body", msg.getDisplayMessageBody());
			intent.putExtra("action",action);		
			context.startService(intent);
		}
	}
	
	private void startEmergencyHandlerService(String action, int timeValue)
	{
		Intent intent = new Intent(context,EmergencyHandlerService.class);
		intent.setAction(action);
		intent.putExtra("timeValue", timeValue);
		context.startService(intent);
	}
	
	private void startSettingsActivity()
	{
		Intent intent = new Intent(context,SettingsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	private void setPreferredCommunicationMode(int action)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String actionStr = preferences.getString(Constants.SP_PREFERRED_COMMUNICATION, "N.A.");
		if(!actionStr.equals("N.A."))
		{
			int currentAction = Integer.parseInt(actionStr);
			switch(action)
			{
				case Constants.ACTION_START_SMS:
					if(currentAction == Integer.parseInt(Constants.COMMUNICATION_MODE_INTERNET))
					{
						Editor editor = preferences.edit();
						editor.putString(Constants.SP_PREFERRED_COMMUNICATION, Constants.COMMUNICATION_MODE_INTERNET_AND_SMS);
						editor.commit();
					}
					break;
				case Constants.ACTION_START_INTERNET:
					if(currentAction == Integer.parseInt(Constants.COMMUNICATION_MODE_SMS))
					{
						Editor editor = preferences.edit();
						editor.putString(Constants.SP_PREFERRED_COMMUNICATION, Constants.COMMUNICATION_MODE_INTERNET_AND_SMS);
						editor.commit();
					}
					break;
				case Constants.ACTION_STOP_SMS:
					if(currentAction == Integer.parseInt(Constants.COMMUNICATION_MODE_INTERNET_AND_SMS))
					{
						Editor editor = preferences.edit();
						editor.putString(Constants.SP_PREFERRED_COMMUNICATION, Constants.COMMUNICATION_MODE_INTERNET);
						editor.commit();
					}
					break;
				case Constants.ACTION_STOP_INTERNET:
					if(currentAction == Integer.parseInt(Constants.COMMUNICATION_MODE_INTERNET_AND_SMS))
					{
						Editor editor = preferences.edit();
						editor.putString(Constants.SP_PREFERRED_COMMUNICATION, Constants.COMMUNICATION_MODE_SMS);
						editor.commit();
					}
			}
		}
	}
}
