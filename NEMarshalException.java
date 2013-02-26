/*
 * Class      : NEMarshalException.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This exception is generated when an IOException occurred
 *              during the marshalling of Messageable messages.
 */

public class NEMarshalException extends NERemoteException {
  public NEMarshalException () {
  }
  
  public NEMarshalException (String message) {
    super (message);
  }
}
