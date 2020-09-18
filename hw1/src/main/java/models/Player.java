package models;

public class Player {

  private char type;
  private int id;

  /** Constructor.
   * @param id player id - 1 or 2
   * @param type x or o
   */
  public Player(int id, char type) {  
    this.id = id;
    this.type = type;
  }

  /** Get Id.
   * @return
   */
  public int getId() {
    return id;
  }

  /** Get this player's type.
   * @return
   */
  public char getType() {
    return type;
  }

  /** Get other player's type.
   * @return
   */
  public char getOpposingType() {
    return type == 'X' ? 'O' : 'X';
  }

  /** Set type.
   * @param type x or o
   */
  public void setType(char type) {
    this.type = type;
  }

  /** Set Id.
   * @param id player id
   */
  public void setId(int id) {
    this.id = id;
  } 
}
