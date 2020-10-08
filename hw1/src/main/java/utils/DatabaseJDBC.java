package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    
    Connection c = null;
    ResultSet rs = null;
    Statement stmt = null;
    Statement stmt2 = null;
    
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
    
    //https://stackoverflow.com/questions/1601151/how-do-i-check-in-sqlite-whether-a-table-exists
    String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='game_data';";
    
    try {
      
      c = getConn();
      
      stmt = c.createStatement();
      rs = stmt.executeQuery(query);
      
      if (isEmptyResult(rs)) {
        
        stmt2 = c.createStatement();
        query = "CREATE TABLE game_data "
              + "(field_name CHAR(50) PRIMARY KEY NOT NULL,"
              + "field_value CHAR(50))";
        
        stmt2.executeUpdate(query);
      }
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt2 != null) {
          stmt2.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }      
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
  private boolean gameExists() {
    
    Connection c = null;
    ResultSet rs = null;
    Statement stmt = null;
    
    boolean gameExists = false;
    
    try {
      
      c = getConn();
      
      String query = "SELECT field_name FROM game_data LIMIT 1;";
      stmt = c.createStatement();
      
      rs = stmt.executeQuery(query);
      
      gameExists = !isEmptyResult(rs);
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    return gameExists;
  }
  
  /** Deletes all saved data from db. 
   * 
   */
  public void resetGameData() {
    
    Connection c = null;
    Statement stmt = null;
    
    try {
      c = getConn();
      
      String query = "DELETE FROM game_data;";
      stmt = c.createStatement();
      stmt.executeUpdate(query);  
      
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
    
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** Get value of field in db.
   * @param fieldName the name of field needing data value.
   * @return gets value of db for given field.
   */
  private String getValue(String fieldName) {
    
    String value = "";
    Connection c = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
      
      c = getConn();
      
      stmt = c.prepareStatement("SELECT field_value FROM game_data WHERE field_name = ?;");
      stmt.setString(1, fieldName);
      
      rs = stmt.executeQuery();
      
      if (!isEmptyResult(rs)) {
        while (rs.next()) {
          value = rs.getString("field_value");
          break;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    return value;
  }
  
  /** Sets a field in db with given value. Overwrites existing value if present.
   * @param fieldName name of field in db to set.
   * @param value value of the data to set.
   */
  private void setValue(String fieldName, String value)  {
    
    Connection c = null;
    PreparedStatement stmt = null;
    PreparedStatement stmt2 = null;
    
    try {
      
      c = getConn();
      
      //first delete existing tuple if present
      stmt = c.prepareStatement("DELETE FROM game_data WHERE field_name = ?;");
      stmt.setString(1, fieldName);
      stmt.executeUpdate();
      
      //insert new tuple
      stmt2 = c.prepareStatement("INSERT INTO game_data ('field_name','field_value') "
          + "VALUES (?,?);");
      stmt2.setString(1, fieldName);
      stmt2.setString(2, value);
      
      stmt2.executeUpdate();
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      try {
        if (stmt2 != null) {
          stmt2.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }      
    }  
  }
  
  /** Find out if ResultSet is empty.
   * @param rs the provided ResultSet object to test.
   * @return whether or not provided resultset is empty.
   */
  private boolean isEmptyResult(ResultSet rs) {
    
    boolean isEmpty = false;
    
    try {
      isEmpty = !rs.isBeforeFirst();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return isEmpty;
  }
  
  /** Get a sqlite connection.
   * @return new db Connection object.
   */
  private Connection getConn() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:game.db");
  }
}
