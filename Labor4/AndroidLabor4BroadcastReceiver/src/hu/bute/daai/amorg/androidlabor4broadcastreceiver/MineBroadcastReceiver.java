package hu.bute.daai.amorg.androidlabor4broadcastreceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class MineBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")){
			// 'pdus' nevu extraban egy Object tombot kapunk, amibol kinyerheto az sms
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			
			if(pdus == null){
				Log.e("RECEIVER", "pdus are null");
			}
			else{
				Log.v("RECEIVER","received " + pdus.length + " messages");
				SmsMessage msg = null;
				// Object tombot kaptunk, vegigmegyunk rajta
				for (Object pdu : pdus) {
					// a konkret SMS kinyerese
					msg = SmsMessage.createFromPdu((byte[])pdu);
					if(msg != null){
						showToast(context,"Message from "+msg.getOriginatingAddress()+": "+msg.getDisplayMessageBody());
					}
					else{
						Log.e("RECEIVER", "Sms is null");
					}
				}
			}
		}
		
		if(intent.getAction().equalsIgnoreCase("android.intent.action.WALLPAPER_CHANGED"))
		{
			showToast(context,"Wallpaper changed");
		}
		
		if(intent.getAction().equalsIgnoreCase("android.intent.action.AIRPLANE_MODE"))
		{
			showToast(context,"Changed to/from Airplane mode");
		}
	}

	private void showToast(Context context, String string)
	{
		Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
	}
}
