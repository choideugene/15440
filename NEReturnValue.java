import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEReturnValue implements NEMessageable {
  private Serializable object;
  
  /*
   * A new return value packet.
   * The constructor for a new NEReturnValue. If a remote object is the return
   * value, then a NERemoteObjectReference is passed as the argument. Otherwise,
   * any other Serializable object is passed. So, in other words, the 
   * return value is a remote object if and only if the return value is a
   * NERemoteObjectReference.
   */
  public NEReturnValue (Serializable object) {
    this.object = object;
  }
  
  public Object getReturnValue () {
    if (object.getClass ().equals (NERemoteObjectReference.class)) {
      NERemoteObjectReference objectRef = (NERemoteObjectReference) object;
      return objectRef.localise ();
    }
    else return object;
  } 
}
