package Unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import models.GameBoard;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.DatabaseJDBC;

public class DatabaseJDBCTest {
  
  DatabaseJDBC db = new DatabaseJDBC();
  GameBoard gameBoard = new GameBoard();
  
  /**
   * This method tests that the save game feature is working.
   */
  @Test
  @Order(1)
  public void testSaveGame() throws SQLException {
    
    gameBoard.setVal(0, 0, 'X');
    gameBoard.saveGameBoard();
    gameBoard.loadGameBoard();
    assertEquals('X', gameBoard.getVal(0, 0));
  }
 
  /**
   * This method tests that the load game feature is working.
   */
  @Test
  @Order(2)
  public void testLoadGame() throws SQLException {
    
    gameBoard.setVal(0, 0, 'X');
    gameBoard.saveGameBoard();    
    
    gameBoard = new GameBoard(true); //load new gameboard object and load the db if avail

    assertEquals('X', gameBoard.getVal(0, 0));
  }
  
  /**
   * This method tests resetting game data.
   */
  @Test
  @Order(3)
  public void testResetGameData() throws SQLException {
    
    db.resetGameData();
    
    //load new gameboard object and load the db if avail (nothing avail this time)
    gameBoard = new GameBoard(true); 
    
    assertEquals(0, gameBoard.getVal(0, 0));
  } 
}
