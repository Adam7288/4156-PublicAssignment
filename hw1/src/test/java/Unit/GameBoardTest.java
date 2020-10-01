package Unit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameBoardTest {

  GameBoard gameboard = new GameBoard();
  Player p1 = new Player(1, 'X');
  Player p2 = new Player(2, 'O');

  /*
   * This method clears the gameboard and starts the game.
   */    
  @BeforeEach
  public void startNewGame() {

    gameboard.resetGameBoard();

    gameboard.setP1(p1);
    gameboard.setP2(p2);

    gameboard.setGameStarted(true);
  }
  
  /**
   * This method tests the initial settings of the gameboard.
   */
  @Test
  public void testInitialGameBoardState() {

    gameboard.resetGameBoard();

    char[][] correctBoardState = { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} };

    assertArrayEquals(correctBoardState, gameboard.getBoardState());
    assertEquals(false, gameboard.isGameStarted());
    assertEquals(1, gameboard.getTurn());
    assertEquals(0, gameboard.getWinner());
    assertEquals(false, gameboard.isDraw());
  }

  /**
   * This method tests that a player cannot make a move before the game has started.
   */
  @Test
  public void testGameNotStartedYet() {

    gameboard.resetGameBoard();

    gameboard.setP1(p1);
    gameboard.setP2(p2);

    Move move = new Move(p2, 1, 0);

    Message msg = gameboard.addMove(move);

    assertEquals(false, msg.getMoveValidity());
    assertEquals("Game has not begun yet", msg.getMessage());
  }

  /**
   * This method tests if a player cannot make a move twice in a row.
   */
  @Test
  public void testDoubleMove() {

    char[][] filledMoves = { {'O', 'X', 'O'}, {'O', 'X', 'O'}, {0, 0, 0} };

    gameboard.setBoardState(filledMoves);

    Move move = new Move(p1, 0, 0);

    Message msg = gameboard.addMove(move);

    assertEquals(false, msg.getMoveValidity());
    assertEquals("Already a turn played in this spot", msg.getMessage());
  }

  /**
   * This method tests if a player cannot go out of turn .
   */
  @Test
  public void testNotPlayerTurnMove() {

    Move move = new Move(p2, 1, 0);

    Message msg = gameboard.addMove(move);

    assertEquals(false, msg.getMoveValidity());
    assertEquals("Not this player turn", msg.getMessage());
  }

  /**
   * This method tests if a player can a move that is not inside the game board dimensions.
   */
  @Test
  public void testOutofBoundsMove() {

    Move move = new Move(p1, 3, 0);

    Message msg = gameboard.addMove(move);

    assertEquals(false, msg.getMoveValidity());
    assertEquals("Move out of bounds", msg.getMessage());
  }

  /**
   * This method tests if a player can add a valid non-winning move.
   */
  @Test
  public void testAddValidMove() {
    Move move = new Move(p1, 1, 0);

    Message msg = gameboard.addMove(move);

    assertEquals(true, msg.getMoveValidity());
  }

  /**
   * This method tests a winning move detected within a horizontal row.
   */
  @Test
  public void testAddWinningMoveHoriz() {

    char[][] aboutToWin = { {'O', 'X', 'O'}, {'O', 'X', 'O'}, {0, 0, 0} };

    gameboard.setBoardState(aboutToWin);

    Move move = new Move(p1, 2, 1);

    Message msg = gameboard.addMove(move);

    assertEquals(true, msg.getMoveValidity());
    assertEquals(1, gameboard.getWinner());
  }

  /**
   * This method tests a winning move detected within a vertical row.
   */
  @Test
  public void testAddWinningMoveVert() {

    char[][] aboutToWin = { {'X', 0, 0}, {'X', 'X', 0}, {0, 0, 0} };

    gameboard.setBoardState(aboutToWin);

    Move move = new Move(p1, 1, 2);

    Message msg = gameboard.addMove(move);

    assertEquals(true, msg.getMoveValidity());
    assertEquals(1, gameboard.getWinner());
  }

  /**
   * This method tests a winning move detected within a diagonal row.
   */
  @Test
  public void testAddWinningMoveDiag() {

    char[][] aboutToWin = { {'X', 0, 0}, {0, 'X', 0}, {0, 0, 0} };

    gameboard.setBoardState(aboutToWin);

    Move move = new Move(p1, 2, 2);

    Message msg = gameboard.addMove(move);

    assertEquals(true, msg.getMoveValidity());
    assertEquals(1, gameboard.getWinner());
  }

  /**
   * This method tests a non-winning move that results in a draw.
   */
  @Test
  public void testDraw() {

    char[][] aboutToDraw = { {'X', 'O', 'X'}, {'X', 'O', 'O'}, {'O', 'X', 0} };

    gameboard.setBoardState(aboutToDraw);

    Move move = new Move(p1, 2, 2);

    Message msg = gameboard.addMove(move);

    assertEquals(true, msg.getMoveValidity());
    assertEquals(true, gameboard.isDraw());
  }

  /**
   * This method tests if a player from can submit a move after the game ends.
   */
  @Test
  public void testMoveAfterGameEnded() {

    char[][] aboutToWin = { {'X', 0, 0}, {0, 'X', 0}, {0, 0, 0} };

    gameboard.setBoardState(aboutToWin);

    //winning move
    Move move = new Move(p1, 2, 2);
    Message msg = gameboard.addMove(move);

    //invalid move
    move = new Move(p1, 2, 0);
    msg = gameboard.addMove(move);    

    assertEquals(false, msg.getMoveValidity());
    assertEquals("Game ended", msg.getMessage());
  }
}
