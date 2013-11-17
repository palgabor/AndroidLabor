package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import hu.bute.daai.amorg.xg5ort.phoneguard.service.BootHandlerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED"))
		{
			Intent i = new Intent(context,BootHandlerService.class);
			context.startService(i);
		}
	}

}
