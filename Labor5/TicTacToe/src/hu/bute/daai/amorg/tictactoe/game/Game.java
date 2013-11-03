package hu.bute.daai.amorg.tictactoe.game;

import java.util.Random;




public class Game {
	
	private final String firstName;
	private final String secondName;
	private final boolean isSingleGame;
	private final boolean isFirstPlayerStarts;
	private final int columns;
	private final int winningCombinations;
	private final int[][] cells;

	private int nextPlayer;
	private int lastClickedX;
	private int lastClickedY;
	private int numberOfFreeCells;
	
	public Game(String firstName, String secondName, boolean isSingleGame,
				boolean isFirstPlayerStarts, int columns, int winningCombinations){
		this.firstName = firstName;
		this.secondName = secondName;
		this.isSingleGame = isSingleGame;
		this.isFirstPlayerStarts = isFirstPlayerStarts;
		this.columns = columns;
		this.winningCombinations = winningCombinations;
		
		cells = new int[columns][columns];
		lastClickedX = 0;
		lastClickedY = 0;
		numberOfFreeCells = columns*columns;
		
		if(isFirstPlayerStarts == true){
			nextPlayer = 1;
		}
		else{
			nextPlayer = 2;
		}
	}
	
	public String getFirstPlayerName(){
		return firstName;
	}
	
	public String getSecondPlayerName(){
		return secondName;
	}
	
	public int getNextPlayer(){
		return nextPlayer; 
	}
	
	public boolean isSingleGame(){
		return isSingleGame;
	}
	
	public int[][] getCells(){
		return cells;
	}
	
	public int getNumberOfColumns(){
		return columns;
	}
	
	public boolean isCellOccupied(int x, int y){
		if(cells[x][y] == 0){
			return false;
		}
		else{
			return true;
		}
	}
	
	public void setClickedCell(int x, int y){
		if(cells[x][y] == 0){
			cells[x][y] = nextPlayer;
			if(nextPlayer == 1){
				nextPlayer = 2;
			}
			else{
				nextPlayer = 1;
			}
			lastClickedX = x;
			lastClickedY = y;
			numberOfFreeCells--;
		}
	}
	
	public void setClickedCell(int x, int y, int player){
		if(cells[x][y] == 0){
			cells[x][y] = player;
			if(player == 1){
				nextPlayer = 2;
			}
			else{
				nextPlayer = 1;
			}
			lastClickedX = x;
			lastClickedY = y;
			numberOfFreeCells--;
		}
	}
	
	/**
	 * Computer selects random empty cells
	 */
	public void computerTurn(){
		if(isSingleGame && nextPlayer == 2){
			//Dirty hack:-)
			if(numberOfFreeCells < 2){
				for(int i=0;i<columns;i++){
					for(int j=0;j<columns;j++){
						if(cells[i][j] == 0){
							setClickedCell(i,j);
						}
					}
				}
			}
			else{
				Random random = new Random();
				int selectedX = 0;
				int selectedY = 0;
				do{
					selectedX = random.nextInt(columns);
					selectedY = random.nextInt(columns);
				}while(cells[selectedX][selectedY] != 0);
				
				setClickedCell(selectedX,selectedY);
			}
		}
	}
	
	/** return values:
	 * -1: game is ongoing
	 * 0: games is draw
	 * 1: first player wins
	 * 2: second player wins
	 */
	public int isGameFinished(){
		if(numberOfFreeCells == 0){
			return 0;
		}
		
		int occupiedCells;
		int previousPlayer;
		if(nextPlayer == 1){
			previousPlayer = 2;
		}
		else{
			previousPlayer = 1;
		}
		
		//check columns
		occupiedCells = 0;
		for(int i=0; i<columns; i++){
			if(cells[lastClickedX][i] == previousPlayer){
				occupiedCells++;
			}
		}
		if(occupiedCells == winningCombinations){
			return previousPlayer;
		}
		
		//check rows
		occupiedCells = 0;
		for(int i=0; i<columns; i++){
			if(cells[i][lastClickedY] == previousPlayer){
				occupiedCells++;
			}
		}
		if(occupiedCells == winningCombinations){
			return previousPlayer;
		}
		
		//check diagonal
		//TODO do this for higher tables than 3*3
		occupiedCells = 0;
		for(int i=0; i<columns; i++){
			if(cells[i][i] == previousPlayer){
				occupiedCells++;
			}
		}
		if(occupiedCells == winningCombinations){
			return previousPlayer;
		}
		
		//check anti diagonal
		//TODO do this for higher tables than 3*3
		occupiedCells = 0;
		for(int i=0; i<columns; i++){
			if(cells[i][(columns-1)-i] == previousPlayer){
				occupiedCells++;
			}
		}
		if(occupiedCells == winningCombinations){
			return previousPlayer;
		}
		
		return -1;
	}
}
