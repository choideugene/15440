import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEReturnValue implements NEMessageable {
  private Serializable object;
  private boolean isRemote;
  
  /*
   * A new return value packet.
   * The constructor for a new NEReturnValue. If a non-remote, serializable
   * object is meant to be the return value, then isRemote should be false.
   * Otherwise, if the return value should be a remote object, a 
   * NERemoteObjectReference is expected to be passed in to the constructor
   * instead, and isRemote should be true.
   */
  public NEReturnValue (Serializable object, boolean isRemote) {
    this.isRemote = isRemote;
    this.object = object;
  }
  
  public Object getReturnValue () {
    if (isRemote) {
      NERemoteObjectReference objectRef = (NERemoteObjectReference) object;
      return objectRef.localise ();
    }
    else return object;
  } 
}
