package models;

import com.google.gson.Gson;

public class Message {

  private boolean moveValidity;
  private int code;
  private String message;

  /** Constructor - sets code to 100 by default.
   * 
   */
  public Message() {
    code = 100;
  }

  /** Get move validity.
   * @return
   */
  public boolean getMoveValidity() {
    return moveValidity;
  }

  /** Set move validity.
   * @param moveValidity move validity
   */
  public void setMoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }

  /** Get code.
   * @return
   */
  public int getCode() {
    return code;
  }

  /** Set code.
   * @param code the message code
   */
  public void setCode(int code) {
    this.code = code;
  }

  /** Get message.
   * @return
   */
  public String getMessage() {
    return message;
  }

  /** Set message.
   * @param message the text message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /** Converts object to JSON.
   * @return
   */
  public String toJson() { //https://stackoverflow.com/questions/18106778/convert-java-object-to-json-and-vice-versa

    Gson gson = new Gson(); 
    return gson.toJson(this);
  }

}
