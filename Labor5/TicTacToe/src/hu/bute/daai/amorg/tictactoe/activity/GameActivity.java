package hu.bute.daai.amorg.tictactoe.activity;

import hu.bute.daai.amorg.tictactoe.R;
import hu.bute.daai.amorg.tictactoe.draw.GameView;
import hu.bute.daai.amorg.tictactoe.draw.GameView.ITouchListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends Activity implements ITouchListener {
	
	private GameView gv;
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		Button quitButton = (Button)findViewById(R.id.quit);
		quitButton.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				finish();
			}
		});
		
		gv = (GameView) findViewById(R.id.game_view);
		gv.setTouchListener(this);
		gv.setFocusable(true);
		gv.setFocusableInTouchMode(true);
		
		Bundle extras = getIntent().getExtras();
		String firstName = "";
		String secondName = "";
		boolean isSingleGame = true;
		boolean isFirstPlayerStarts = true;
		if (extras != null) {
			firstName = extras.getString("firstName");
			secondName =  extras.getString("secondName");
			isSingleGame = extras.getBoolean("isSingleGame");
			isFirstPlayerStarts = extras.getBoolean("isFirstPlayerStarts");
		}
		
		TextView nextPlayer = (TextView)findViewById(R.id.next_player);
		if(isSingleGame){
			nextPlayer.append(firstName);
		}
		else{
			nextPlayer.append(isFirstPlayerStarts ? firstName : secondName);
		}
		
		gv.startGame(firstName, secondName, isSingleGame, isFirstPlayerStarts);
	}

	@Override
	public void onTouchSelected() {
		gv = (GameView) findViewById(R.id.game_view);
		
		if(gv.getGame().isSingleGame()){
			return;
		}
		if(!gv.isCellOccupied()){
			TextView nextPlayer = (TextView)findViewById(R.id.next_player);
			nextPlayer.setText(getString(R.string.next_player));
			
			//Dirty hack :-)
			if(gv.getGame().getNextPlayer() == 2){
				nextPlayer.append(gv.getGame().getFirstPlayerName());
			}
			else{
				nextPlayer.append(gv.getGame().getSecondPlayerName());
			}
		}
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putBoolean("MyBoolean", true);
	  
	  GameView gv = (GameView)findViewById(R.id.game_view);
	  int[][] cells = gv.getGame().getCells();
	  int numberOfColumns = gv.getGame().getNumberOfColumns();
	  for(int i=0; i<numberOfColumns; i++){
		  for(int j=0; j<numberOfColumns; j++){
			  savedInstanceState.putInt(i + "," + j,cells[i][j]);
		  }
	  }
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  
	  GameView gv = (GameView)findViewById(R.id.game_view);
	  int numberOfColumns = gv.getGame().getNumberOfColumns();
	  for(int i=0; i<numberOfColumns; i++){
		  for(int j=0; j<numberOfColumns; j++){
			 int cell = savedInstanceState.getInt(i + "," + j);
			 gv.getGame().setClickedCell(i,j,cell);
		  }
	  }
	  gv.invalidate();
	}
}