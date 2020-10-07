package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.GameBoard;
import models.Player;

public class DatabaseJDBC {
  
  public DatabaseJDBC() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    
    Connection c = getConn();
    
    //https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
    String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='game_data';";
    Statement stmt = c.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    
    if(isEmptyResult(rs)) {
      
      stmt = c.createStatement();
      query = "CREATE TABLE game_data "
            + "(field_name CHAR(50) PRIMARY KEY NOT NULL,"
            + "field_value CHAR(50))";
      
      stmt.executeUpdate(query);
    }
    
    c.close();
  }
  
  public void saveGame(GameBoard gameBoard) throws SQLException {
    
    if (gameBoard.getP1() != null) {
      setValue("p1Code", Character.toString(gameBoard.getP1().getType()));
    }
    
    if (gameBoard.getP2() != null) {
      setValue("p2Code", Character.toString(gameBoard.getP1().getType()));
    }
    
    String gameStartedString = gameBoard.isGameStarted() ? "true" : "false";
    setValue("gameStarted", gameStartedString);
    
    String isDrawString = gameBoard.isDraw() ? "true" : "false";
    setValue("isDraw", isDrawString);
    
    setValue("turn", Integer.toString(gameBoard.getTurn()));
    setValue("winner", Integer.toString(gameBoard.getWinner()));
    
    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        setValue(i + ":" + j, Character.toString(gameBoard.getVal(i, j)));
      }
    }
  }
  
  public void loadGame(GameBoard gameBoard) throws SQLException {
    
    if (!gameExists()) {
      return;
    }
    
    String p1Code = getValue("p1Code");
    if (!p1Code.equals("")) {
      gameBoard.setP1(new Player(1, p1Code.charAt(0)));
    }
    
    String p2Code = getValue("p2Code");
    if (!p2Code.equals("")) {
      gameBoard.setP2(new Player(2, p2Code.charAt(0)));
    }
    
    boolean gameStarted = getValue("gameStarted").equals("true");
    gameBoard.setGameStarted(gameStarted);
    
    boolean isDraw = getValue("isDraw").equals("true");
    gameBoard.setDraw(isDraw);
    
    gameBoard.setTurn(Integer.parseInt(getValue("turn")));
    gameBoard.setWinner(Integer.parseInt(getValue("winner")));
    
    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        
        gameBoard.setVal(i, j, getValue(i + ":" + j).charAt(0));
        
        setValue(i + ":" + j, Character.toString(gameBoard.getVal(i, j)));
      }
    }
  }
  
  private boolean gameExists() throws SQLException {
    
    Connection c = getConn();
    
    String query = "SELECT field_name FROM game_data LIMIT 1;";
    Statement stmt = c.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    
    c.close();
    
    return !isEmptyResult(rs);
  }
  
  public void resetGameData() throws SQLException {
    
    Connection c = getConn();
    
    String query = "DELETE FROM game_data;";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(query);  
    
    c.close();
  }
  
  private String getValue(String fieldName) throws SQLException {
    
    Connection c = getConn();
    
    Statement stmt = c.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT field_value FROM game_data WHERE field_name = '" 
        + fieldName + "';");
    
    String value = "";
    
    if (!isEmptyResult(rs)) {
      while (rs.next()) {
        value = rs.getString("field_value");
        break;
      }
    }
    
    c.close();
    
    return value;
  }
  
  private void setValue(String fieldName, String value) throws SQLException {
    
    Connection c = getConn();
    
    //first delete existing tuple if present
    String query = "DELETE FROM game_data WHERE field_name = '" + fieldName + "';";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(query);
    
    //insert new tuple
    query = "INSERT INTO game_date ('field_name','field_value') "
        + "VALUES ('" + fieldName + "','" + value + "');";
    stmt = c.createStatement();
    stmt.executeUpdate(query);
    
    c.close();    
  }
  
  private boolean isEmptyResult(ResultSet rs) throws SQLException {
    return !rs.isBeforeFirst();
  }
  
  private Connection getConn() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:game.db");
  }
}
