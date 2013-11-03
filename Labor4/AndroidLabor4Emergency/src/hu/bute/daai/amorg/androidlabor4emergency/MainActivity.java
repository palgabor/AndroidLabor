package hu.bute.daai.amorg.androidlabor4emergency;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button112 = (Button)findViewById(R.id.call112Btn);
		Button buttonTHome = (Button)findViewById(R.id.thomeBtn);
		Button buttonInvitel = (Button)findViewById(R.id.invitelBtn);
		Button buttonDigi = (Button)findViewById(R.id.digiBtn);
		
		button112.setOnClickListener(callEmergency);
		buttonTHome.setOnClickListener(callEmergency);
		buttonInvitel.setOnClickListener(callEmergency);
		buttonDigi.setOnClickListener(callEmergency);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	OnClickListener callEmergency = new OnClickListener() {
		public void onClick(View v) {
			String phoneNumber = "tel:";
			
			/* *
			* T-Home hibabejelento: 1412
			* Invitel hibabejelento: 1445
			* Digi hibabejelento: 1272
			
			* */
			switch (v.getId()) {
				case R.id.call112Btn:
					phoneNumber += "112";
					break;
				case R.id.thomeBtn:
					phoneNumber += "1412";
					break;
				case R.id.invitelBtn:
					phoneNumber += "1445";
					break;
				case R.id.digiBtn:
					phoneNumber += "1272";
					break;
				default:
					break;
			}
			
			// szam felhivasa
			Intent i = new Intent(Intent.ACTION_CALL,Uri.parse(phoneNumber));
			startActivity(i);
		}
	};

}
