import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEMethodCall {
  private Method method;
  private Object[] args;
  
  public NEMethodCall (Method method, Object[] args) {
    this.method = method;
    this.args = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      this.args[i] = args[i];
    }
  }
  
  /*
   * Returns the return value of the method invocation if successful.
   */
  public Object invoke (Object object)
      throws IllegalAccessException, IllegalArgumentException,
             InvocationTargetException {
    return method.invoke (object, args);
  }
}
