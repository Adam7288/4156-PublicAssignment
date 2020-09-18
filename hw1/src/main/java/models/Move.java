package models;

public class Move {

  private Player player;
  private int moveX;
  private int moveY;

  /** Constructor.
   * @param player player making move
   * @param x x coordinate
   * @param y y coordinate
   */
  public Move(Player player, int x, int y) {

    this.player = player;   
    moveX = x;
    moveY = y;
  }

  /** Get player.
   * @return
   */
  public Player getPlayer() {
    return player;
  }

  /** Set player.
   * @param player the player
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /** Get move in x coordinate.
   * @return
   */
  public int getMoveX() {
    return moveX;
  }

  /** Set move in x coordinate.
   * @param moveX x coordinate
   */
  public void setMoveX(int moveX) {
    this.moveX = moveX;
  }

  /** Get move in y coordinate.
   * @return
   */
  public int getMoveY() {
    return moveY;
  }

  /** Set move in y coordinate.
   * @param moveY y coordinate
   */
  public void setMoveY(int moveY) {
    this.moveY = moveY;
  }
}
