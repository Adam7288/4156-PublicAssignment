package controllers;

import io.javalin.Javalin;
import models.GameBoard;
import models.Move;
import models.Player;

import java.io.IOException;
import java.util.Queue;
import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;
  
  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
	  
	GameBoard gameboard = new GameBoard();  
	  
    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
  
    app.get("/newgame", ctx -> {
    	ctx.redirect("tictactoe.html");
    });
   
    app.post("/startgame:type", ctx -> {
    	
    	gameboard.setP1(new Player(1, ctx.pathParam("type").charAt(0)));
 
    	char[][] initBoardState = { {0,0,0}, {0,0,0}, {0,0,0} };    	
    	gameboard.setBoardState(initBoardState);
    	
    	ctx.result(gameboard.toJson());
    });

    app.get("/joingame", ctx -> {
    	
    	gameboard.setP1(new Player(2, gameboard.getP1().getOpposingType()));

    	sendGameBoardToAllPlayers(gameboard.toJson());
    	
    	ctx.redirect("tictactoe.html?p=2");
    });   

    app.post("/move/:playerId", ctx -> {
    	
    	int playerId = (int) ctx.pathParam("playerId").charAt(0);
    	int moveX = (int) ctx.pathParam("x").charAt(0);
    	int moveY = (int) ctx.pathParam("y").charAt(0);
    	
    	Player player = playerId == 1 ? gameboard.getP1() : gameboard.getP2();
    	Move move = new Move(player, moveX, moveY);
    	
    	boolean validity = gameboard.addMove(move);
    	
    	//message: move entered, move invalid, or (won?? -> might be sent with websocket)
    	
    	sendGameBoardToAllPlayers(gameboard.toJson());
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
