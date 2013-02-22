import java.io.*;
import java.net.*;
import java.lang.*;
import java.lang.reflect.*;

public class NEMethod implements Serializable {
  private String className;
  private String methodName;
  private String[] argTypes;

  public NEMethod (Class classType, String methodName, Class[] argTypes) {
    className = classType.getName ();
    this.methodName = methodName;
    this.argTypes = new String[argTypes.length];
    for (int i = 0; i < argTypes.length; i++) {
      this.argTypes[i] = argTypes[i].getName ();
    }
  }
  
  public String getName () {
    return methodName;
  }
  
  public Class getObjectType () {
    try {
      return Class.forName (className);
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace ();
      return null;
    }
  }
  
  public Class[] getArgTypes () {
    try {
      Class[] argTypes = new Class[this.argTypes.length];
      for (int i = 0; i < argTypes.length; i++) {
        argTypes[i] = Class.forName (this.argTypes[i]);
      }
      return argTypes;
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace ();
      return null;
    }
  }
}
