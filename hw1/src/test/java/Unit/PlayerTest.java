package Unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import models.Player;
import org.junit.jupiter.api.Test;

public class PlayerTest {
  
  Player player1 = new Player(1, 'X');
  Player player2 = new Player(2, 'O');
  
  /**
   * This method tests that the opposing type of X is O.
   */
  @Test
  public void testGetOpposingTypeX() {
    
    assertEquals('O', player1.getOpposingType());   
  }
 
  /**
   * This method tests that the opposing type of O is X.
   */
  @Test
  public void testGetOpposingTypeO() {
    
    assertEquals('X', player2.getOpposingType());
  }  
  
}
