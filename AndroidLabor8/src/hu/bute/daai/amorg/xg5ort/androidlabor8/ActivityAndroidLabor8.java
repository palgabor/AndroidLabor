package hu.bute.daai.amorg.xg5ort.androidlabor8;

import hu.bute.daai.amorg.xg5ort.androidlabor8.HttpManager.HttpManagerListener;
import hu.bute.daai.amorg.xg5ort.labor8.R;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAndroidLabor8 extends Activity implements HttpManagerListener
{
	private LinearLayout bgLayout;
	private EditText etUserName;
	private Button btnLeft;
	private Button btnRight;
	private Button btnUp;
	private Button btnDown;
	private EditText etMessage;
	private Button btnSendMessage;
	
	private Timer timer = new Timer();
	
	private Date startTime;
	private Date stopTime;
	private int sumOfResponses = 0;
	private int numberOfMessages = 1;
	
	private HttpManager httpManager = new HttpManager(this);
	private final String BASE_URL =
	    "http://avalon.aut.bme.hu/~tyrael/labyrinthwar/";
	
	private class TimerTaskResetBackground extends TimerTask
	{
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bgLayout.setBackgroundColor(Color.BLACK);
				}
			});
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bgLayout = (LinearLayout)findViewById(R.id.bgLayout);
		etUserName = (EditText)findViewById(R.id.editTextUserName);
		btnLeft = (Button)findViewById(R.id.buttonLeft);
		btnRight = (Button)findViewById(R.id.buttonRight);
		btnUp = (Button)findViewById(R.id.buttonUp);
		btnDown = (Button)findViewById(R.id.buttonDown);
		etMessage = (EditText)findViewById(R.id.editTextMessage);
		btnSendMessage = (Button)findViewById(R.id.buttonSendMessage);

		btnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etUserName.isEnabled())
						etUserName.setEnabled(false);
				handleMoveMessage(HttpManager.EMESSAGE_LEFT);
				Animation showAnim = AnimationUtils.loadAnimation(
						ActivityAndroidLabor8.this,
						R.anim.pushanim);
				btnLeft.startAnimation(showAnim);
			}
		});
		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etUserName.isEnabled())
					etUserName.setEnabled(false);
				handleMoveMessage(HttpManager.EMESSAGE_RIGHT);
				Animation showAnim = AnimationUtils.loadAnimation(
						ActivityAndroidLabor8.this,
						R.anim.pushanim);
				btnRight.startAnimation(showAnim);
			}
		});
		btnUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etUserName.isEnabled())
					etUserName.setEnabled(false);
				handleMoveMessage(HttpManager.EMESSAGE_UP);
				Animation showAnim = AnimationUtils.loadAnimation(
						ActivityAndroidLabor8.this,
						R.anim.pushanim);
				btnUp.startAnimation(showAnim);
			}
		});
		btnDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etUserName.isEnabled())
					etUserName.setEnabled(false);
				handleMoveMessage(HttpManager.EMESSAGE_DOWN);
				Animation showAnim = AnimationUtils.loadAnimation(
						ActivityAndroidLabor8.this,
						R.anim.pushanim);
				btnDown.startAnimation(showAnim);
			}
		});
		
		btnSendMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleWriteCommentMessage();
				Animation showAnim = AnimationUtils.loadAnimation(
						ActivityAndroidLabor8.this,
						R.anim.fadeanim);
					etMessage.startAnimation(showAnim);
			}
		});
	}
	
	public void responseArrived(final String aMessage)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (aMessage.startsWith("ERROR"))
		    	{
		    		((LinearLayout)findViewById(R.id.bgLayout)).
	                      setBackgroundColor(Color.RED);
		    		TimerTaskResetBackground ttbg = 
	                      new TimerTaskResetBackground();
		    		timer.schedule(ttbg, 300);
		    	}
		    	
		    	showMessage(aMessage);
			}
		});
		
		stopTime = Calendar.getInstance().getTime();
		updateAvarageResponseTime();
		updateNetworkState();
	}

	public void errorOccured(final String aError)
	{
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	showMessage(aError);
			}
		});
	}

	public void showMessage(String aMessage)
	{
		Toast.makeText(this, aMessage, Toast.LENGTH_SHORT).show();	
	}
	
	public void handleMoveMessage(int aStepCode)
	{
		if (etUserName.getText().toString().equals(""))
		{
			showMessage(getString(R.string.empty_user));
			return;
		}
		
		final StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append("moveuser.php?username=");
		sb.append(URLEncoder.encode(etUserName.getText().toString()));
		sb.append("&step=");
		sb.append(aStepCode);
		
		new Thread() {
			public void run()
			{
				httpManager.execute(sb.toString());
			}
		}.start();
		startTime = Calendar.getInstance().getTime();
	}
	
	public void handleWriteCommentMessage()
	{
		if (etUserName.getText().toString().equals("") || etMessage.getText().equals(""))
		{
			showMessage(getString(R.string.empty_user_or_message));
			return;
		}
		
		final StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append("writemessage.php?username=");
		sb.append(URLEncoder.encode(etUserName.getText().toString()));
		sb.append("&message=");
		sb.append(URLEncoder.encode(etMessage.getText().toString()));
		
		new Thread() {
			public void run()
			{
				httpManager.execute(sb.toString());
			}
		}.start();
		startTime = Calendar.getInstance().getTime();
	}
	
	private void updateAvarageResponseTime()
	{
		sumOfResponses += startTime.compareTo(stopTime);
		TextView averageResponseTime = (TextView)findViewById(R.id.response_time_value);
		averageResponseTime.setText(String.valueOf(sumOfResponses/numberOfMessages));
		numberOfMessages++;
	}
	
	private void updateNetworkState()
	{
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		TextView networkName = (TextView)findViewById(R.id.network_name_value);
		TextView wifiState = (TextView)findViewById(R.id.wifi_state_value);
		networkName.setText(wifiInfo.getSSID());
		wifiState.setText(wifiInfo.toString());
	}
}
