/*
 * Class      : NEDemarshaller.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: Handles demarshalling NEMessageable messages
 */

import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEDemarshaller {
  /*
   * Returns the exception stored in the message.
   */
  public static Exception demarshalException (NEException message) {
    return message.getException ();
  }
  
  /*
   * Returns the return value stored in the message. If the return value is
   * a remote object reference, then the remote object stub is returned.
   */
  public static Object demarshalReturnValue (NEReturnValue message) {
    Object rv = message.getReturnValue ();
    
    // Return value is a remote object
    if (rv.getClass ().toString ().equals ("NERemoteObjectReference")) {
      return ((NERemoteObjectReference) rv).localise ();
    }
    else return rv;
  }
  
  /*
   * Get the method stored in the message and return a NEMethodCall
   * representing the invocation of the method on the target object.
   */
  public static NEMethodCall demarshalMethodInvocation 
    (NEMethodInvocation message, NERemote targetObject)
      throws NoSuchMethodException, SecurityException {
    Method method = message.getMethod ();
    
    Object[] args = message.getArguments ();
    System.out.println ("In method call: " + targetObject);
    return new NEMethodCall (targetObject, method, args);
  }  
  
  // TODO:  methodDatabase should be mapping methodId to Method objects
  //        perhaps create methods database?
}
