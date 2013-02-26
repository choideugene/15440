/*
 * Class      : NERemoteException.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This class encapsulates all exceptions generated during the
 *              communication between the client and server side of RMI.
 */
 
import java.io.IOException;

public class NERemoteException extends IOException {
  public NERemoteException () {
  }
  
  public NERemoteException (String message) {
    super (message);
  }
}
