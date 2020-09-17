package models;

import com.google.gson.Gson;

public class Message {

  private boolean moveValidity;
  private int code;
  private String message;

  public Message() {
    code = 100;
  }

  public boolean getMoveValidity() {
    return moveValidity;
  }

  public void setMoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String toJson() { //https://stackoverflow.com/questions/18106778/convert-java-object-to-json-and-vice-versa

    Gson gson = new Gson(); 
    return gson.toJson(this);
  }

}
