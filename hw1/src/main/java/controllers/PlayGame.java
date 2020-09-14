package controllers;

import io.javalin.Javalin;
import models.GameBoard;

import java.io.IOException;
import java.util.Queue;
import org.eclipse.jetty.websocket.api.Session;

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
    	gameboard.setPlayer1(ctx.pathParam("type").charAt(0));
    	
    });

    app.get("/joingame", ctx -> {
    	ctx.redirect("tictactoe.html?p=2");
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
