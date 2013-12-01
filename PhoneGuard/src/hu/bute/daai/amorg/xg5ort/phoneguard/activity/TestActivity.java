package hu.bute.daai.amorg.xg5ort.phoneguard.activity;

import hu.bute.daai.amorg.xg5ort.data.DeviceData;
import hu.bute.daai.amorg.xg5ort.data.LocationData;
import hu.bute.daai.amorg.xg5ort.data.SharedPreferencesConstants;
import hu.bute.daai.amorg.xg5ort.phoneguard.R;
import hu.bute.daai.amorg.xg5ort.phoneguard.parser.SmsParser;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
		
		Button startSettingsActivity = (Button)findViewById(R.id.start_settings_activity);
		Button startEmergency = (Button)findViewById(R.id.start_emergency_state);
		Button startEmergency10sec = (Button)findViewById(R.id.sec10);
		Button startEmergency30sec = (Button)findViewById(R.id.sec30);
		Button startEmergency60sec = (Button)findViewById(R.id.sec60);
		Button stopEmergency = (Button)findViewById(R.id.stop_emergency_state);
		Button displaySharedPreferences = (Button)findViewById(R.id.display_shared_preferences);
		Button displayDeviceData = (Button)findViewById(R.id.display_device_data);
		Button displayLocationData = (Button)findViewById(R.id.display_location_data);
		final TextView displayData = (TextView)findViewById(R.id.displayData);
		
		startSettingsActivity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(), "hu.bute.daai.amorg.xg5ort.phoneguard.activity.SettingsActivity");
				startActivity(intent);
			}
		});
		
		startEmergency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(),
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
				intent.putExtra("timeValue", 0);
				startService(intent);
			}
		});
		
		startEmergency10sec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(),
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
				intent.putExtra("timeValue", 10);
				startService(intent);
			}
		});
		
		startEmergency30sec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(),
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
				intent.putExtra("timeValue", 30);
				startService(intent);
			}
		});
		
		startEmergency60sec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(),
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_EMERGENCY_SMS);
				intent.putExtra("timeValue", 60);
				startService(intent);
			}
		});
		
		stopEmergency.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getApplicationContext(),
						"hu.bute.daai.amorg.xg5ort.phoneguard.service.EmergencyHandlerService");
				intent.setAction(SmsParser.ACTION_STOP_EMERGENCY_SMS);
				startService(intent);
			}
		});
		
		displaySharedPreferences.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				displayData.setText("");
				displayData.setText("Shared preferences:\n");
				displayData.append("Preferred communication: " + preferences.getString(SharedPreferencesConstants.PREFERRED_COMMUNICATION, "N.A.") + "\n");
				displayData.append("Phone number: " + preferences.getString(SharedPreferencesConstants.PHONE_NUMBER, "N.A.") + "\n");
				displayData.append("Password: " + preferences.getString(SharedPreferencesConstants.PASSWORD, "N.A.") + "\n");
				displayData.append("Emergency frequency: " + preferences.getString(SharedPreferencesConstants.EMERGENCY_FREQUENCY, "N.A.") + "\n");
				displayData.append("Is emergency state: " + String.valueOf(preferences.getBoolean(SharedPreferencesConstants.IS_EMERGENCY_STATE, false)) + "\n");
				displayData.append("IMEI: " + preferences.getString(SharedPreferencesConstants.IMEI, "N.A.") + "\n");
				displayData.append("IMSI: " + preferences.getString(SharedPreferencesConstants.IMSI, "N.A.") + "\n");
				displayData.append("Operator: " + preferences.getString(SharedPreferencesConstants.OPERATOR_NAME, "N.A.") + "\n");
			}
		});
		
		displayDeviceData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeviceData deviceData = DeviceData.getInstance();
				displayData.setText("");
				displayData.append("Display device data:\n");
				displayData.append("IMEI: " + deviceData.getImei() + "\n");
				displayData.append("IMSI: " + deviceData.getImsi() + "\n");
				displayData.append("Operator: " + deviceData.getOperatorName() + "\n");
			}
		});
		
		displayLocationData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationData locationData = LocationData.getInstance();
				displayData.setText("");
				displayData.append("Location data:\n");
				displayData.append("Lat: " + locationData.getLatitude() + "\n");
				displayData.append("Long: " + locationData.getLongitude() + "\n");
				displayData.append("Speed: " + locationData.getSpeed() + "\n");
				displayData.append("Accuracy: " + locationData.getAccuracy() + "\n");
				displayData.append("CellId: " + locationData.getCellId() + "\n");
				displayData.append("Lac: " + locationData.getLac() + "\n");
				displayData.append("Address: " + locationData.getAddress() + "\n");
			}
		});
	}
}
