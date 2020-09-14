package models;

public class Player {

  private char type;

  private int id;
  
  public Player(int id, char type) {  
	  this.id = id;
	  this.type = type;
  }
  
  public int getId() {
	  return id;
  }
  
  public char getType() {
	  return type;
  }
  
  public char getOpposingType() {
	  return type == 'X' ? 'O' : 'X';
  }

public void setType(char type) {
	this.type = type;
}

public void setId(int id) {
	this.id = id;
} 
}
