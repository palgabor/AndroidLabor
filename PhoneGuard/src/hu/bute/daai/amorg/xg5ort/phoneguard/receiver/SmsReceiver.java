package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver
{

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

			SmsMessage msg = null;
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
		Intent intent = new Intent();
		switch(result)
		{
			case SmsParser.ACTION_UNKNOWN: break;
			case SmsParser.NON_PHONE_GUARD_SMS: break;
			case SmsParser.BAD_PASSWORD: break;
			case SmsParser.ACTION_EMERGENCY_WITHOUT_TIME: 
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_STR);
				intent.putExtra("timeValue", 0);
				context.startService(intent);
				break;
			
			case SmsParser.ACTION_STOP_EMERGENCY:
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_STOP_EMERGENCY_STR);
				context.startService(intent);
				break;
			
			case SmsParser.ACTION_SETTINGS:
				intent.setClassName(context,
						"hu.bute.daai.amorg.xg5ort.phoneguard.activity.SettingsActivity");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			
			default:
				if(result >= SmsParser.ACTION_EMERGENCY_TIME_BASE)
				{
					intent.setClassName(context,
							"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
					intent.setAction(SmsParser.ACTION_EMERGENCY_STR);
					intent.putExtra("timeValue", result%SmsParser.ACTION_EMERGENCY_TIME_BASE);
					context.startService(intent);
				}
		}
	}
}
