import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEMethodCall {
  private Method method;
  private Object[] args;
  private NERemote object;
  
  public NEMethodCall (Object object, Method method, Object[] args) {
    this.method = method;
    this.args = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      this.args[i] = args[i];
    }
  }
  
  /*
   * Returns the return value of the method invocation if successful.
   */
  public Object invoke ()
      throws IllegalAccessException, IllegalArgumentException,
             InvocationTargetException {
    System.out.println ("Methodcall method: " + method);
    System.out.println ("Methodcall args: ");
    for (Object o : args) {
      System.out.println ("\t" + o);
    }
    System.out.println ("Object: " + object);
    Object result = method.invoke (object, args);
    if (result.getClass ().getSuperclass ().equals (NERemoteObjectStub.class)) {
      // Return type is a remote object, so return the reference to it
      return ((NERemoteObjectStub) result).getRemoteObjectReference ();
    }
    else return result;
  }
  
  public NERemote getRemoteObject () {
    return object;
  }
}
