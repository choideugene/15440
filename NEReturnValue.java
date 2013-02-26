/*
 * Class      : NEReturnValue.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This class encapsulates all possible return values resulting 
 *              from the execution of a method invocation on the server side of
 *              RMI.
 *
 * Notes      : If a remote object is resulted, the NEReturnValue contains 
 *              instead a NERemoteObjectReference of that remote object.
 */

import java.io.Serializable;

public class NEReturnValue implements NEMessageable {
  private Serializable object;
  
  /*
   * A new return value packet.
   * The constructor for a new NEReturnValue. If a remote object is the return
   * value, then a NERemoteObjectReference is passed as the argument. Otherwise,
   * any other Serializable object is passed.
   */
  public NEReturnValue (Serializable object) {
    this.object = object;
  }
  
  /*
   * Get the return value stored in this NEReturnValue message. If the
   * stored object is a NERemoteObjectReference, then the stub for the
   * remote object reference by the NERemoteObjectReference is returned 
   * instead.
   */
  public Object getReturnValue () {
    if (object.getClass ().equals (NERemoteObjectReference.class)) {
      NERemoteObjectReference objectRef = (NERemoteObjectReference) object;
      return objectRef.localise ();
    }
    else return object;
  } 
}
