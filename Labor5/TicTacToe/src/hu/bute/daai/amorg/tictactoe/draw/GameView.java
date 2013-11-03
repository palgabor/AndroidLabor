package hu.bute.daai.amorg.tictactoe.draw;

import hu.bute.daai.amorg.tictactoe.activity.ResultActivity;
import hu.bute.daai.amorg.tictactoe.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class GameView extends View{

	private final int LEFT = 0;
	private final int TOP = 0;
	private final int STROKE_WIDTH = 5;
	private final int CELL_SIZE;
	private final int NUMBER_OF_COLUMNS= 3;
	private final int NUMBER_OF_WINNING_COMBINATIONS = 3;
	
	private int touchX;
	private int touchY;
	private ITouchListener touchListener = null;
	
	private Game game;
	
	@SuppressWarnings("deprecation")
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		requestFocus();
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if(display.getWidth() < display.getHeight()){
			CELL_SIZE = display.getWidth()/NUMBER_OF_COLUMNS - (60/NUMBER_OF_COLUMNS);
		}
		else{
			CELL_SIZE = display.getHeight()/NUMBER_OF_COLUMNS - (60/NUMBER_OF_COLUMNS);
		}
	}
	
	public void startGame(String firstName, String secondName, boolean isSingleGame,
						  boolean isFirstPlayerStarts){
		game = new Game(firstName, secondName, isSingleGame, isFirstPlayerStarts,
				NUMBER_OF_COLUMNS, NUMBER_OF_WINNING_COMBINATIONS);
		
		if(game.isSingleGame()){
			game.computerTurn();
			isGameFinished();
		}
		invalidate();
	}
	
	public Game getGame(){
		return game;
	}
	
	public interface ITouchListener {
		abstract void onTouchSelected();
	}
	
	public void setTouchListener(ITouchListener aTouchListener) {
		touchListener = aTouchListener;
	}

	private void touchSelected() {
		if (touchListener != null)
			touchListener.onTouchSelected();
		
		if(touchX > NUMBER_OF_COLUMNS*CELL_SIZE - STROKE_WIDTH ||
		   touchY > NUMBER_OF_COLUMNS*CELL_SIZE - STROKE_WIDTH){
			return;
		}
		
		int cellX = touchX/CELL_SIZE;
		int cellY = touchY/CELL_SIZE;
		
		game.setClickedCell(cellX,cellY);
		isGameFinished();
		if(game.isSingleGame()){
			game.computerTurn();
			isGameFinished();
		}

		invalidate();
	}

	public int getTouchX() {
		return touchX;
	}

	public int getTouchY() {
		return touchY;
	}
	
	public boolean isCellOccupied(){
		return game.isCellOccupied(touchX/CELL_SIZE, touchY/CELL_SIZE);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Négyzetes felület kikényszerítése
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
		setMeasuredDimension(d, d);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			return true;
		} else if (action == MotionEvent.ACTION_UP) {
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			touchSelected();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        paintTable(canvas);
        drawSignsToCells(canvas, game.getNextPlayer());
	}
	
	public void paintTable(Canvas canvas){
		Paint mLinePaint = new Paint();
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStrokeWidth(STROKE_WIDTH);
		mLinePaint.setStyle(Style.STROKE);
		
		for(int i=0; i<NUMBER_OF_COLUMNS; i++){
			for(int j=0; j<NUMBER_OF_COLUMNS; j++){
				canvas.drawRect(LEFT+CELL_SIZE*j, TOP+CELL_SIZE*i,
								LEFT+CELL_SIZE*(j+1), TOP+CELL_SIZE*(i+1), mLinePaint);
			}
		}
	}
	
	public void drawSignsToCells(Canvas canvas, int nextPlayer)
	{
		Paint mLinePaint = new Paint();
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStrokeWidth(STROKE_WIDTH);
		mLinePaint.setStyle(Style.STROKE);
		
		int cells[][] = game.getCells();
		for(int i=0; i<NUMBER_OF_COLUMNS; i++) {
			for(int j=0; j<NUMBER_OF_COLUMNS; j++)
			{
				if(cells[i][j] != 0){	
					int centerX = CELL_SIZE*i + CELL_SIZE/2;
					int centerY = CELL_SIZE*j + CELL_SIZE/2;
					
					if(cells[i][j] == 1){
						canvas.drawLine(centerX-CELL_SIZE/4, centerY-CELL_SIZE/4,
										centerX+CELL_SIZE/4, centerY+CELL_SIZE/4, mLinePaint);
						canvas.drawLine(centerX-CELL_SIZE/4, centerY+CELL_SIZE/4,
										centerX+CELL_SIZE/4, centerY-CELL_SIZE/4, mLinePaint);
					}
					else{
						canvas.drawCircle(centerX, centerY, CELL_SIZE/4, mLinePaint);
					}
				}
			}
		}
	}
	
	public void isGameFinished(){
		int ret = game.isGameFinished();
		if(ret != -1){
			Intent intent = new Intent();
			intent.setClass(getContext(), ResultActivity.class);
			intent.putExtra("result",ret);
			intent.putExtra("firstPlayerName", game.getFirstPlayerName());
			intent.putExtra("secondPlayerName", game.getSecondPlayerName());
			((Activity)getContext()).startActivity(intent);
		}
	}
}
