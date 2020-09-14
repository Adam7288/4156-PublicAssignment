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
	
	//check validity = toggle turn if valid. Check for winner;
	
	
	return true;
}

public String toJson() { //https://stackoverflow.com/questions/18106778/convert-java-object-to-json-and-vice-versa
	
	Gson gson=new Gson(); 
	String gameBoardJson=gson.toJson(this);
	
	return gameBoardJson;
}

private boolean validateMove(Move move) { 

  
}

private void checkForWinner() {
	
}
