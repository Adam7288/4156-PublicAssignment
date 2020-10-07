package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.GameBoard;

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
            + "(field_value CHAR(50))";
      
      stmt.executeUpdate(query);
    }
    
    c.close();
  }
  
  public void saveGame(GameBoard gameBoard) {
    
    Connection c = getConn();
  
    
    
    
    c.close();
  }
  
  public boolean gameExists() throws SQLException {
    
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
  
  public void loadGame(GameBoard gameBoard) {
    
    Connection c = getConn();
    
    
    c.close();
  }
  
  private String getValue(String fieldName) {
    
    Connection c = getConn();
    
    
    c.close();
  }
  
  private boolean isEmptyResult(ResultSet rs) throws SQLException {
      
    return !rs.isBeforeFirst();
  }
  
  private Connection getConn() {
    
    try {
      return DriverManager.getConnection("jdbc:sqlite:game.db");
    } catch (SQLException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
  }
}
