package Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import java.sql.SQLException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class) 
public class GameTest {

  /**
   * Runs only once before the testing starts.
   */
  @BeforeAll
  public static void init() throws SQLException {
    // Start Server
    PlayGame.main(null);
    System.out.println("Before All");
  }

  /**
   * This method starts a new game before every test run. It will run every time before a test.
   */
  @BeforeEach
  public void startNewGame() {
    // Test if server is running. You need to have an endpoint /
    // If you do not wish to have this end point, it is okay to not have anything in this method.
    HttpResponse<String> response = Unirest.get("http://localhost:8080/").asString();
    //int restStatus = response.getStatus();

    System.out.println("Before Each");
  }

  //TODO test p2 joining before p1
  
  
  /**
   * This is a test case to evaluate the newgame endpoint.
   */
  @Test
  @Order(1)
  public void newGameTest() {

    // Create HTTP request and get response
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();

    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }

  /**
   * This is a test case to evaluate the startgame endpoint.
   */
  @Test
  @Order(2)
  public void startGameTest() {

    // Create a POST request to startgame endpoint and get the body
    // Remember to use asString() only once for an endpoint call. Every time you call asString(), 
    //a new request will be sent to the endpoint. Call it once and then use the data in the object.
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = response.getBody();

    // --------------------------- JSONObject Parsing ----------------------------------

    System.out.println("Start Game Response: " + responseBody);

    // Parse the response to JSON object
    JSONObject jsonObject = new JSONObject(responseBody);

    // Check if game started after player 1 joins: Game should not start at this point
    assertEquals(false, jsonObject.get("gameStarted"));

    // ---------------------------- GSON Parsing -------------------------

    // GSON use to parse data to object
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();

    // Check if player type is correct
    assertEquals('X', player1.getType());

    System.out.println("Test Start Game");
  }

  /**
   * This is a test case to evaluate if player can make move before both players join.
   */
  @Test
  @Order(3)   
  public void testMoveBeforeBothPlayersJoin() {
    
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test cannot make move before both players join");
    
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Game has not begun yet", jsonObject.get("message"));
  }
  
  /*
   * This tests if player 1 can only make the first move by seeing if player 2 can
   */
  @Test
  @Order(4)   
  public void testP2FirstMove() {
    
    HttpResponse<String> response = Unirest.get("http://localhost:8080/joingame").asString();
    
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test player 2 cannot make first move");
    
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Not this player turn", jsonObject.get("message"));
  }

  /*
   * This tests if player 1 can make 2 moves in a row
   */
  @Test
  @Order(5)   
  public void testP1TwoConsecutiveMoves() {
    
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test player cannot make consecutive moves");
    
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Not this player turn", jsonObject.get("message"));
  }
  
  /*
   * This tests if move rejected with missing params
   */
  @Test
  @Order(6)   
  public void testRejectMoveMissingParams() {
    
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test reject move with missing parameters");
    
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Invalid or missing parameters supplied.", jsonObject.get("message"));
  }
  
  /*
   * This tests if a player (1) can win game
   */
  @Test
  @Order(7)   
  public void testPlayer1WinsGame() {
    
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    
    response = Unirest.get("http://localhost:8080/gameboard").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test player can win game");
    
    assertEquals(1, jsonObject.get("winner"));
  }
  
  /*
   * This tests if a game can end in a draw\
   */
  @Test
  @Order(7)   
  public void testGameEndsInDraw() throws SQLException, InterruptedException {
    
    //restart game and initialize players
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    response = Unirest.get("http://localhost:8080/joingame").asString();
    
    //fill up gameboard with non-winning moves
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    PlayGame.stop();
    PlayGame.main(null);
    Thread.sleep(2000);
    response = Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    System.out.println(response);
    response = Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    
    response = Unirest.get("http://localhost:8080/gameboard").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    System.out.println("Test game can end in a draw");
    
    assertEquals(true, jsonObject.get("isDraw"));
  }
  
  /**
   * This will run every time after a test has finished.
   */
  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }

  /**
   * This method runs only once after all the test cases have been executed.
   */
  @AfterAll
  public static void close() {
    // Stop Server
    PlayGame.stop();
    System.out.println("After All");
  }
}
