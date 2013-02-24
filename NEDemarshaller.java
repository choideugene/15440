import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEDemarshaller {
  public static Exception demarshalException (NEException message) {
    return message.getException ();
  }
  
  public static Object demarshalReturnValue (NEReturnValue message) {
    return message.getReturnValue ();
  }
  
  // TODO: methodDatabase should be mapping methodId to Method objects
  public static NEMethodCall demarshalMethodInvocation (NEMethodInvocation message)
      throws NoSuchMethodException, SecurityException {
    Method method = null;
    
    if (message.isUsingId ()) {
      // get method from methodDatabase
    }
    else  {
      try {
        Class<?> objectType = message.getObjectType ();
        method = objectType.getMethod (
          message.getMethodName (), message.getArgumentTypes ());        
      }
      catch (ClassNotFoundException e) {
        // TODO: first check if class exists
        // Download class file
      }
    }
    
    Object[] args = message.getArguments ();
    return new NEMethodCall (method, args);
  }  
}
