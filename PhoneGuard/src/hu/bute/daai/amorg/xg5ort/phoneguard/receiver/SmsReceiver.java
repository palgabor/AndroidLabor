package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED"))
		{
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			
			if(pdus == null)
			{
				Log.e("SmsReceiver", "pdus are null");
			}
			else
			{
				Log.v("SmsReceiver","received " + pdus.length + " messages");
				SmsMessage msg = null;
				for (Object pdu : pdus)
				{
					msg = SmsMessage.createFromPdu((byte[])pdu);
					if(msg != null)
					{
						Toast.makeText(context,"Message from "+msg.getOriginatingAddress()+": "+msg.getDisplayMessageBody(), Toast.LENGTH_LONG).show();
					}
					else
					{
						Log.e("SmsReceiver", "Sms is null");
					}
				}
			}
		}
	}

}
