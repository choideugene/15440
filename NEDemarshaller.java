import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEDemarshaller {
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
  
  // TODO: methodDatabase should be mapping methodId to Method objects
  public static NEMethodCall demarshalMethodInvocation 
    (NEMethodInvocation message, NERemoteObjectTable table)
      throws NoSuchMethodException, SecurityException {
    Method method = null;
    
    if (message.isUsingId ()) {
      // get method from methodDatabase
    }
    else  {
      try {
        System.out.println (message);
        Class<?> objectType = message.getObjectType ();
        Method[] ms = objectType.getMethods ();
        for (int i = 0; i < ms.length; i++) {
          System.out.println (ms[i]);
        }        
        System.out.println (objectType);
        method = objectType.getMethod (
          message.getMethodName (), message.getArgumentTypes ());        
      }
      catch (ClassNotFoundException e) {
        // TODO: first check if class exists
        // Download class file
      }
    }
    
    Object[] args = message.getArguments ();
    NERemote object = table.get (message.getObjectKey ()); // TODO: error check
    System.out.println (message.getObjectKey ());
    System.out.println (object);
    return new NEMethodCall (object, method, args);
  }  
}
