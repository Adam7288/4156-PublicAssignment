package models;

import com.google.gson.Gson;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  public GameBoard() {
	gameStarted = false;
	turn = 1;
	winner = 0;
	isDraw = false;
  }

public Player getP1() {
	return p1;
}

public void setP1(Player p1) {
	this.p1 = p1;
}

public Player getP2() {
	return p2;
}

public void setP2(Player p2) {
	this.p2 = p2;
}

public boolean isGameStarted() {
	return gameStarted;
}

public void setGameStarted(boolean gameStarted) {
	this.gameStarted = gameStarted;
}

private boolean isGameFinished() { return isDraw || getWinner() > 0; }

public int getTurn() {
	return turn;
}

public void setTurn(int turn) {
	this.turn = turn;
}

public char[][] getBoardState() {
	return boardState;
}

public void setBoardState(char[][] boardState) {
	this.boardState = boardState;
}

public int getWinner() {
	return winner;
}

public void setWinner(int winner) {
	this.winner = winner;
}

public boolean isDraw() {
	return isDraw;
}

public void setDraw(boolean isDraw) {
	this.isDraw = isDraw;
} 

public boolean addMove(Move move) {
	
	if(!validateMove(move)) 
		return false;
	
	setVal(move.getMoveX(), move.getMoveY(), move.getPlayer().getType());
	
	checkForWinner();
	toggleTurn();
	
	return true;
}

public String toJson() { //https://stackoverflow.com/questions/18106778/convert-java-object-to-json-and-vice-versa
	
	Gson gson=new Gson(); 
	String gameBoardJson=gson.toJson(this);
	
	return gameBoardJson;
}

private boolean validateMove(Move move) { 
	
	//not their turn
	if(move.getPlayer().getId() != turn || isGameFinished())
		return false;
	
	//out of bounds
	if(move.getMoveX() > 2 || move.getMoveX() < 0 || move.getMoveY() > 2 || move.getMoveY() < 0)
		return false;
	
	//already a mark in that spot
	if(getVal(move.getMoveX(), move.getMoveY()) != 0)
		return false;
	
	return true;
}

private void toggleTurn() { 
	
	int newTurn = getTurn() == 1 ? 2 : 1; 
	setTurn(newTurn);
}

private char getVal(int x, int y) { return getBoardState()[x][y]; }

private void setVal(int x, int y, char type) { boardState[x][y] = type; }

private int getPlayerNumFromType(char type) {
	
	if(p1.getType() == type)
		return p1.getId();
	
	return p2.getId();
}

private void checkForWinner() {
	
	boolean openSpots = false;
	
	//check horiz
	for(int i=0; i<=2; i++) {
		
		char curVal = 0;
		for(int j=0; j<=2; j++) {
			
			if(j==0) {
				curVal = getVal(i, j);
				continue;
			}
			
			if(curVal != getVal(i, j))
				break;
			
			if(j == 2) {			
				setWinner(getPlayerNumFromType(getVal(i, j)));
				return;
			}
		}
			
	}
	
	//check vert
	for(int j=0; j<=2; j++) {
		
		char curVal = 0;
		for(int i=0; i<=2; i++) {
			
			if(j==0) {
				curVal = getVal(i, j);
				continue;
			}
			
			if(curVal != getVal(i, j))
				break;
			
			if(j == 2) {			
				setWinner(getPlayerNumFromType(getVal(i, j)));
				return;
			}
		}	
	}	
	
	//check diag
	if((getVal(0,0) == getVal(1,1) && getVal(1,1) == getVal(2,2)) || (getVal(2,0) == getVal(1,1) && getVal(1,1) == getVal(0,2))) {
		setWinner(getPlayerNumFromType(getVal(1, 1))); //set winner as middle marker either way
		return;
	}
	
	if(!openSpots)
		isDraw = true;
}
