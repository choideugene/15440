import java.io.*;
import java.net.*;
import java.lang.*;
import java.lang.reflect.*;

public class NEMethodInvocation implements NEMessageable, Serializable {
  private boolean usingId;
  private int methodId;
  
  private String objectType;
  private String methodName;
  private String[] argTypes;
  
  private Serializable[] args;
  private boolean[] remote;

  /* 
   * If the method id is known, create a NEMethodInvocation object with that.
   */
  public NEMethodInvocation (int methodId, Serializable[] args, boolean[] remote) {
    usingId = true;
    this.methodId = methodId;
    this.args = new Serializable[args.length];
    this.remote = new boolean[args.length];
    for (int i = 0; i < args.length; i++) {
      this.args[i] = args[i];
      this.remote[i] = remote[i];
    }
  }
  
  /*
   * If the method id is not known, create a NEMethodInvocation object 
   * from the method name and method parameter types.
   */
  public NEMethodInvocation (String objectType, String methodName, 
      String[] argTypes, Serializable[] args, boolean[] remote) {
    usingId = false;
    this.objectType = objectType;
    this.methodName = methodName;
    this.argTypes = new String[args.length];
    this.args = new Serializable[args.length];
    this.remote = new boolean[args.length];
    for (int i = 0; i < args.length; i++) {
      this.argTypes[i] = argTypes[i];
      this.args[i] = args[i];
      this.remote[i] = remote[i];
    }
  }
  
  /*
   * If the method id is not known, create a NEMethodInvocation object 
   * from the method name and method parameter types.
   */
  public NEMethodInvocation (Class<?> objectType, String methodName, 
      Class<?>[] argTypes, Serializable[] args, boolean[] remote) {
    usingId = false;
    this.objectType = objectType.getName ();
    this.methodName = methodName;
    this.argTypes = new String[args.length];
    this.args = new Serializable[args.length];
    this.remote = new boolean[args.length];
    for (int i = 0; i < args.length; i++) {
      this.argTypes[i] = argTypes[i].getName ();    
      this.args[i] = args[i];
      this.remote[i] = remote[i];
    }
  }  
  
  public int getMethodId () {
    return methodId;
  }
  
  public boolean isUsingId () {
    return usingId;
  }
  
  public Class<?> getObjectType () throws ClassNotFoundException {
    return Class.forName (objectType);
  }
  
  public String getMethodName () {
    return methodName;
  }
  
  public Class<?>[] getArgumentTypes () throws ClassNotFoundException {
    Class<?>[] argTypes = new Class<?>[args.length];
    if (! usingId) return null;
    
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = Class.forName (this.argTypes[i]);   
    }
    return argTypes;
  }
  
  public Object[] getArguments () {
    Object[] arguments = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      if (remote[i]) {
        NERemoteObjectReference objectRef = (NERemoteObjectReference) args[i];
        arguments[i] = objectRef.localise ();
      }
      else arguments[i] = args[i];      
    }
    return arguments;
  }
}
