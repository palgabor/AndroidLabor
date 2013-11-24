package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver
{
	SmsMessage msg = null;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		SmsParser parser = SmsParser.getInstance(context);
		String sms = readSms(intent);
		int result = parse(parser,sms);
		switchAction(context, result);
	}
	
	public String readSms(Intent intent)
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
	
	public int parse(SmsParser parser, String sms)
	{
		int result =  parser.parse(sms);
		if(result == SmsParser.PHONE_GUARD_SMS)
		{
			abortBroadcast();
			result = parser.findAction(sms);
		}
		return result;
	}
	
	public void switchAction(Context context, int result)
	{
		Intent intent;
		switch(result)
		{
			case SmsParser.NON_PHONE_GUARD_SMS: 
				break;
			
			case SmsParser.ACTION_UNKNOWN:
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
				intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
				intent.putExtra("sender", msg.getOriginatingAddress());
				intent.putExtra("time", msg.getTimestampMillis());
				intent.putExtra("body", msg.getDisplayMessageBody());
				intent.putExtra("action",SmsParser.ACTION_UNKNOWN_STR);		
				context.startService(intent);
				break;
				
			case SmsParser.BAD_PASSWORD: 
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
				intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
				intent.putExtra("sender", msg.getOriginatingAddress());
				intent.putExtra("time", msg.getTimestampMillis());
				intent.putExtra("body", msg.getDisplayMessageBody());
				intent.putExtra("action",SmsParser.BAD_PASSWORD_STR);
				context.startService(intent);
				break;
				
			case SmsParser.ACTION_EMERGENCY_WITHOUT_TIME: 
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
				intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
				intent.putExtra("sender", msg.getOriginatingAddress());
				intent.putExtra("time", msg.getTimestampMillis());
				intent.putExtra("body", msg.getDisplayMessageBody());
				intent.putExtra("action",SmsParser.ACTION_EMERGENCY_WITHOUT_TIME_STR);
				context.startService(intent);
				
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
				intent.putExtra("timeValue", 0);
				context.startService(intent);
				break;
			
			case SmsParser.ACTION_STOP_EMERGENCY:
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
				intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
				intent.putExtra("sender", msg.getOriginatingAddress());
				intent.putExtra("time", msg.getTimestampMillis());
				intent.putExtra("body", msg.getDisplayMessageBody());
				intent.putExtra("action",SmsParser.ACTION_STOP_EMERGENCY_STR);
				context.startService(intent);
				
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_STOP_EMERGENCY_SMS);
				context.startService(intent);
				break;
			
			case SmsParser.ACTION_SETTINGS:
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
				intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
				intent.putExtra("sender", msg.getOriginatingAddress());
				intent.putExtra("time", msg.getTimestampMillis());
				intent.putExtra("body", msg.getDisplayMessageBody());
				intent.putExtra("action",SmsParser.ACTION_SETTINGS_STR);
				context.startService(intent);
				
				intent = new Intent();
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.activity.SettingsActivity");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			
			default:
				if(result >= SmsParser.ACTION_EMERGENCY_TIME_BASE)
				{
					intent = new Intent();
					intent.setClassName(context,
							"hu.bute.daai.amorg.xg5ort.phoneguard.service.DatabaseService");
					intent.setAction(DatabaseService.ACTION_SMS_ARRIVED);
					intent.putExtra("sender", msg.getOriginatingAddress());
					intent.putExtra("time", msg.getTimestampMillis());
					intent.putExtra("body", msg.getDisplayMessageBody());
					intent.putExtra("action",SmsParser.ACTION_EMERGENCY_TIME_BASE_STR);
					context.startService(intent);
					
					intent = new Intent();
					intent.setClassName(context,
							"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
					intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
					intent.putExtra("timeValue", result%SmsParser.ACTION_EMERGENCY_TIME_BASE);
					context.startService(intent);
				}
		}
	}
}
