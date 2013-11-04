package amorg.labo11.service.xg5ort;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EarthquakeAlarmReceiver extends BroadcastReceiver {

	public static final String ACTION_REFRESH_EARTHQUAKE_ALARM = "amorg.labo11.earthquake.ACTION_REFRESH_EARTHQUAKE_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		// implementacio
		if(intent.getAction() == ACTION_REFRESH_EARTHQUAKE_ALARM)
		{
			Intent earthQuakeService = new Intent(context, EarthquakeService.class);
			context.startService(earthQuakeService);
		}
	}
}