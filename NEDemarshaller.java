import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEDemarshaller {
  public static Exception demarshal (NEException message) {
    return message.getException ();
  }
  
  public static Object demarshal (NEReturnValue message) {
    return message.getReturnValue ();
  }
  
  // TODO: methodDatabase should be mapping methodId to Method objects
  public static NEMethodCall demarshal (NEMethodInvocation message, Object methodDatabase) {
    Method method = null; // get method from methodDatabase
    Object[] args = message.getArguments ();
    return new NEMethodCall (method, args);
  }  
}
