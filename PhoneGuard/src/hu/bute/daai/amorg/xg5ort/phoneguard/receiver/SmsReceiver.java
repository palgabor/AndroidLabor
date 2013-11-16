package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED"))
		{
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");

			SmsMessage msg = null;
			for (Object pdu : pdus)
			{
				msg = SmsMessage.createFromPdu((byte[])pdu);
				if(msg != null)
				{
					//msg.getOriginatingAddress(), msg.getMessageBody()
					String body = msg.getDisplayMessageBody();
					SmsParser parser = SmsParser.getInstance();
					int result =  parser.parse(body);
					if(result == SmsParser.NON_PHONE_GUARD_SMS || result == SmsParser.BAD_PASSWORD)
					{
						return;
					}
					if(result == SmsParser.PHONE_GUARD_SMS)
					{
						abortBroadcast();
						result = parser.findAction(body);
						Toast.makeText(context, "Result: " + result, Toast.LENGTH_LONG).show();
						
					}
				}
			}
		}
	}

}
