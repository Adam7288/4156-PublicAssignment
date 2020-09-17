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

  }

  public void resetGameBoard() {

    char[][] initBoardState = { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} }; 

    setBoardState(initBoardState);

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

  private boolean isGameFinished() { 
    return isDraw || getWinner() > 0; 
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

  private char getVal(int x, int y) { 
    return getBoardState()[x][y]; 
  }

  private void setVal(int x, int y, char type) { 
    boardState[x][y] = type; 
  }

  private boolean hasOpenSlots() {

    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        if (getVal(i, j) == 0) {
          return true;
        }
      }
    }

    return false;
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

  public Message addMove(Move move) {

    Message message = new Message();

    if (!validateMove(move, message)) {
      return message;
    }

    setVal(move.getMoveX(), move.getMoveY(), move.getPlayer().getType());

    message.setMoveValidity(true);
    message.setMessage("Valid Move");

    checkForWinner();
    toggleTurn();

    return message;
  }

  public String toJson() { //https://stackoverflow.com/questions/18106778/convert-java-object-to-json-and-vice-versa

    Gson gson = new Gson(); 
    return gson.toJson(this);
  }
  
  //method has side effects - will alter the message
  private boolean validateMove(Move move, Message message) { 

    if (!isGameStarted()) {

      message.setMessage("Game has not begun yet");
      message.setMoveValidity(false);
      return false;
    }

    if (isGameFinished()) {

      message.setMessage("Game ended");
      message.setMoveValidity(false);
      return false;
    }

    //not their turn
    if (move.getPlayer().getId() != turn) {

      message.setMessage("Not this player turn");
      message.setMoveValidity(false);
      return false;
    }

    //out of bounds
    if (move.getMoveX() > 2 || move.getMoveX() < 0 || move.getMoveY() > 2 || move.getMoveY() < 0) {

      message.setMessage("Move out of bounds");
      message.setMoveValidity(false);
      return false;
    }

    //already a mark in that spot
    if (getVal(move.getMoveX(), move.getMoveY()) != 0) {

      message.setMessage("Already a turn played in this spot");
      message.setMoveValidity(false);
      return false;
    }

    return true;
  }

  private void toggleTurn() { 

    int newTurn = getTurn() == 1 ? 2 : 1; 
    setTurn(newTurn);
  }

  private int getPlayerNumFromType(char type) {

    if (p1.getType() == type) {
      return p1.getId();
    }

    return p2.getId();
  }

  private void checkForWinner() {

    //check horiz
    for (int i = 0; i <= 2; i++) {

      char curMatchedVal = 0;

      for (int j = 0; j <= 2; j++) {

        char curVal = getVal(i, j);

        if (curVal == 0) {
          break;
        }

        if (j == 0) {
          curMatchedVal = curVal;
          continue;
        }

        if (curMatchedVal != curVal) {
          break;
        }

        if (j == 2) {
          setWinner(getPlayerNumFromType(curMatchedVal));
          return;
        }
      }
    }

    //check vert
    for (int j = 0; j <= 2; j++) {

      char curMatchedVal = 0;

      for (int i = 0; i <= 2; i++) {

        char curVal = getVal(i, j);

        if (curVal == 0) {
          break;
        }

        if (j == 0) {
          curMatchedVal = curVal;
          continue;
        }

        if (curMatchedVal != curVal) {
          break;
        }

        if (j == 2) {           
          setWinner(getPlayerNumFromType(curMatchedVal));
          return;
        }
      }
    }

    //check diag
    char centerVal = getVal(1, 1);
    if (centerVal == 0) {
      return; //no winner or draw possible in this case
    }

    if ((getVal(0, 0) == centerVal && centerVal == getVal(2, 2)) 
        || (getVal(2, 0) == centerVal && centerVal == getVal(0, 2))) {
      setWinner(getPlayerNumFromType(centerVal)); //set winner as middle marker either way
      return;
    }

    if (!hasOpenSlots()) {
      isDraw = true;
    }
  }
}
