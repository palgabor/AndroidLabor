package hu.bute.daai.amorg.tictactoe.activity;

import hu.bute.daai.amorg.tictactoe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		Bundle extras = getIntent().getExtras();
		int result = 0;
		String firstPlayerName = "";
		String secondPlayerName = "";
		if(extras != null){
			result = extras.getInt("result");
			firstPlayerName = extras.getString("firstPlayerName");
			secondPlayerName = extras.getString("secondPlayerName");
		}
		
		TextView tv = (TextView)findViewById(R.id.result);
		if(result == 0){
			tv.setText(getString(R.string.result_draw));
		}
		else if(result == 1){
			tv.setText(firstPlayerName + " ");
			tv.append(getString(R.string.result_win));
		}
		else if(result == 2){
			tv.setText(secondPlayerName + " ");
			tv.append(getString(R.string.result_win));
		}
		
		Button okButton = (Button)findViewById(R.id.ok);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent intent = new Intent(ResultActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
