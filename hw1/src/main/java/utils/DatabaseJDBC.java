package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.GameBoard;
import models.Player;

public class DatabaseJDBC {
 
  /** Default constuctor. Sets up schema if necessary. 
   * 
   */
  public DatabaseJDBC() {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    
    //https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
    String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='game_data';";
    Statement stmt;
    try {
      Connection c = getConn();
      stmt = c.createStatement();
      
      ResultSet rs = stmt.executeQuery(query);
      
      if (isEmptyResult(rs)) {
        
        stmt = c.createStatement();
        query = "CREATE TABLE game_data "
              + "(field_name CHAR(50) PRIMARY KEY NOT NULL,"
              + "field_value CHAR(50))";
        
        stmt.executeUpdate(query);
      }
      
      c.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /** Saves gameboard object to internal database. Implicit assumption one game at a time.
   * @param gameBoard the GameBoard object.
   */
  public void saveGame(GameBoard gameBoard) throws SQLException {
    
    if (gameBoard.getP1() != null) {
      setValue("p1Code", Character.toString(gameBoard.getP1().getType()));
    }
    
    if (gameBoard.getP2() != null) {
      setValue("p2Code", Character.toString(gameBoard.getP2().getType()));
    }
    
    String gameStartedString = gameBoard.isGameStarted() ? "true" : "false";
    setValue("gameStarted", gameStartedString);
    
    String isDrawString = gameBoard.isDraw() ? "true" : "false";
    setValue("isDraw", isDrawString);
    
    setValue("turn", Integer.toString(gameBoard.getTurn()));
    setValue("winner", Integer.toString(gameBoard.getWinner()));
    
    for (int i = 0; i <= 2; i++) {
      for (int j = 0; j <= 2; j++) {
        
        char val = gameBoard.getVal(i, j);
        val = val == 0 ? '0' : val;
        
        setValue(i + ":" + j, Character.toString(val));
      }
    }
  }
  
  /** Loads saved game from db into GameBoard object.
   * @param gameBoard the GameBoard object.
   */
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
        
        char val = getValue(i + ":" + j).charAt(0);
        val = val == '0' ? 0 : val;
        gameBoard.setVal(i, j, val);
      }
    }
  }
  
  /** Find out if ResultSet is empty.
   * @return whether or not provided resultset is empty.
   */
  private boolean gameExists() throws SQLException {
    
    Connection c = getConn();
    
    String query = "SELECT field_name FROM game_data LIMIT 1;";
    Statement stmt = c.createStatement();
    ResultSet rs = stmt.executeQuery(query);
    
    c.close();
    
    return !isEmptyResult(rs);
  }
  
  /** Deletes all saved data from db. 
   * 
   */
  public void resetGameData() throws SQLException {
    
    Connection c = getConn();
    
    String query = "DELETE FROM game_data;";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(query);  
    
    c.close();
  }
  
  /** Get value of field in db.
   * @param fieldName the name of field needing data value.
   * @return gets value of db for given field.
   */
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
  
  /** Sets a field in db with given value. Overwrites existing value if present.
   * @param fieldName name of field in db to set.
   * @param value value of the data to set.
   */
  private void setValue(String fieldName, String value) throws SQLException {
    
    Connection c = getConn();
    
    //first delete existing tuple if present
    String query = "DELETE FROM game_data WHERE field_name = '" + fieldName + "';";
    Statement stmt = c.createStatement();
    stmt.executeUpdate(query);
    
    //insert new tuple
    query = "INSERT INTO game_data ('field_name','field_value') "
        + "VALUES ('" + fieldName + "','" + value + "');";
    stmt = c.createStatement();
    stmt.executeUpdate(query);
    
    c.close();    
  }
  
  /** Find out if ResultSet is empty.
   * @param rs the provided ResultSet object to test.
   * @return whether or not provided resultset is empty.
   */
  private boolean isEmptyResult(ResultSet rs) throws SQLException {
    return !rs.isBeforeFirst();
  }
  
  /** Get a sqlite connection.
   * @return new db Connection object.
   */
  private Connection getConn() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:game.db");
  }
}
