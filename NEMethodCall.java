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
  
  public Object invoke (Object object) {
    try {
      return method.invoke (object, args);
    }
    catch (Exception e) {
      e.printStackTrace ();
    }
    return null;
  }
}
