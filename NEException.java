/*
 * Class      : NEException.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This class encapsulates an exception thrown during the 
 *              setup or execution of a method on the server side of RMI.
 */

public class NEException implements NEMessageable {
  Exception exception;
  
  /*
   * A new Exception message packet.
   */
  public NEException (Exception e) {
    exception = e;
  }
  
  /* 
   * Get the exception stored in this NEException message
   */
  public Exception getException () {
    return exception;
  }
}
