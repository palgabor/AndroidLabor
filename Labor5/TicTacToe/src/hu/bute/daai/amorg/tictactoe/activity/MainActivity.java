package hu.bute.daai.amorg.tictactoe.activity;

import hu.bute.daai.amorg.tictactoe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private boolean isSingleGame = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final TextView tvFirstName = (TextView)findViewById(R.id.add_first_name);
		tvFirstName.setVisibility(View.INVISIBLE);
		final TextView tvSecondName = (TextView)findViewById(R.id.add_second_name);
		tvSecondName.setVisibility(View.INVISIBLE);
		final TextView tvWhoStarts = (TextView)findViewById(R.id.who_starts);
		tvWhoStarts.setVisibility(View.INVISIBLE);
		
		final EditText etFirstName = (EditText)findViewById(R.id.edit_text_first_name);
		etFirstName.setVisibility(View.INVISIBLE);
		final EditText etSecondName = (EditText)findViewById(R.id.edit_text_second_name);
		etSecondName.setVisibility(View.INVISIBLE);
		
		final RadioButton radioFirst = (RadioButton)findViewById(R.id.radio_first);
		radioFirst.setVisibility(View.INVISIBLE);
		final RadioButton radioSecond = (RadioButton)findViewById(R.id.radio_second);
		radioSecond.setVisibility(View.INVISIBLE);
		
		final Button startButton = (Button)findViewById(R.id.start);
		startButton.setVisibility(View.INVISIBLE);
		startButton.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				if(isSingleGame){
					if(etFirstName.getText().toString().matches("")){
						etFirstName.setError(getString(R.string.empty));
						return;
					}
					else{
						etFirstName.setError(null);
					}
				}
				else{
					if(etFirstName.getText().toString().matches("")){
						etFirstName.setError(getString(R.string.empty));
						return;
					}
					else{
						etFirstName.setError(null);
					}
					if(etSecondName.getText().toString().matches("")){
						etSecondName.setError(getString(R.string.empty));
						return;
					}
					else{
						etSecondName.setError(null);
					}
				}
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, GameActivity.class);
				intent.putExtra("firstName",etFirstName.getText().toString());
				if(isSingleGame){
					intent.putExtra("secondName",getString(R.string.computer));
				}
				else{
					intent.putExtra("secondName",etSecondName.getText().toString());
				}
				intent.putExtra("isSingleGame",isSingleGame);
				if(radioFirst.isChecked()){
					intent.putExtra("isFirstPlayerStarts",true);
				}
				else{
					intent.putExtra("isFirstPlayerStarts",false);
				}
				startActivity(intent);
			}
		});
		
		Button quitButton = (Button)findViewById(R.id.quit);
		quitButton.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				finish();
			}
		});
		
		Button singlePlayer = (Button)findViewById(R.id.single);
		singlePlayer.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				tvFirstName.setVisibility(View.VISIBLE);
				etFirstName.setVisibility(View.VISIBLE);
				tvSecondName.setVisibility(View.INVISIBLE);
				etSecondName.setVisibility(View.INVISIBLE);
				tvWhoStarts.setVisibility(View.VISIBLE);
				radioFirst.setText(getString(R.string.player));
				radioFirst.setVisibility(View.VISIBLE);
				radioSecond.setText(getString(R.string.computer));
				radioSecond.setVisibility(View.VISIBLE);
				startButton.setVisibility(View.VISIBLE);
				isSingleGame = true;
			}
		});
		
		Button multiPlayer = (Button)findViewById(R.id.multi);
		multiPlayer.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				tvFirstName.setVisibility(View.VISIBLE);
				etFirstName.setVisibility(View.VISIBLE);
				tvSecondName.setVisibility(View.VISIBLE);
				etSecondName.setVisibility(View.VISIBLE);
				tvWhoStarts.setVisibility(View.VISIBLE);
				radioFirst.setText(getString(R.string.first_player_starts));
				radioFirst.setVisibility(View.VISIBLE);
				radioSecond.setText(getString(R.string.second_player_starts));
				radioSecond.setVisibility(View.VISIBLE);
				startButton.setVisibility(View.VISIBLE);
				isSingleGame = false;
			}
		});
	}
}
