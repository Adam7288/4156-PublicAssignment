package controllers;

import io.javalin.Javalin;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;

public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /** Main method of the application.
   * @param args Command line arguments
   * @throws SQLException 
   */
  public static void main(final String[] args) throws SQLException {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    
    GameBoard gameboard = new GameBoard(true); 

    app.get("/newgame", ctx -> {
      
      ctx.redirect("tictactoe.html");
      
    });

    app.post("/startgame", ctx -> {
      
      gameboard.resetGameBoard();
      
      gameboard.setP1(new Player(1, ctx.formParam("type").charAt(0))); 
      ctx.result(gameboard.toJson());
      
      gameboard.saveGameBoard();
    });

    app.get("/joingame", ctx -> {
      /* //removing to reduce testing complexity
      if (gameboard.getP1() == null) {
        ctx.html("<h1 style=\"color:red;\">Player 1 has not started a game yet.</h1>");
        return;
      }
      */
      gameboard.setP2(new Player(2, gameboard.getP1().getOpposingType()));
      gameboard.setGameStarted(true);

      sendGameBoardToAllPlayers(gameboard.toJson());

      ctx.redirect("/tictactoe.html?p=2");
      
      gameboard.saveGameBoard();
    }); 
    
    /*
     * Gets gameboard in JSON format for testing purposes
     * Not enough time to write a websocket client
     * That would be an interesting endeavor one day
     */
    app.get("/gameboard", ctx -> {
      
      ctx.result(gameboard.toJson());
    });

    app.post("/move/:playerId", ctx -> {

      Message message;

      if (ctx.pathParam("playerId") == null 
          || ctx.formParam("x") == null 
          || ctx.formParam("y") == null) {

        message = new Message();
        message.setMoveValidity(false);
        message.setMessage("Invalid or missing parameters supplied.");
      } else {

        int playerId = Integer.parseInt(ctx.pathParam("playerId"));
        int moveX = Integer.parseInt(ctx.formParam("x"));
        int moveY = Integer.parseInt(ctx.formParam("y"));

        Player player = playerId == 1 ? gameboard.getP1() : gameboard.getP2();
        Move move = new Move(player, moveX, moveY);

        message = gameboard.addMove(move);
      }

      ctx.result(message.toJson());

      if (message.getMoveValidity()) {
        sendGameBoardToAllPlayers(gameboard.toJson());
        gameboard.saveGameBoard();
      }
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
