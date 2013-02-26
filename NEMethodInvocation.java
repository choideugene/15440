/*
 * Class      : NEMethodInvocation.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This class encapsulates the method invocation from a client
 *              of RMI. 
 *
 * Notes      : If an argument to the method is a remote object, then 
 *              the NERemoteObjectReference of the remote object should replace
 *              that argument when constructing a new NEMethodInvocation.
 *              For example, if a client requests the invocation of a method
 *              call "Integer add (RemoteNum number, Integer x)" from the remote 
 *              object RemoteCalc (which, say, has object key 0 at some object 
 *              server), then one would create the NEMethodInvocation as 
 *              follows:
 *                  Class[] argTypes = { RemoteNum.class, Integer.class };
 *                  Serializable[] args = { number.getRemoteObjectReference (),
 *                                          x };
 *                  NEMethodInvocation mi = new NEMethodInvocation 
 *                     (0, RemoteCalc.class, "add", argTypes, args);
 */

import java.io.Serializable;
import java.lang.reflect.*;

public class NEMethodInvocation implements NEMessageable {
  private boolean usingId;
  private int methodId;
  
  private String objectType;
  private String methodName;
  private String[] argTypes;
  private int objectKey;
  
  private Serializable[] args;

  /* 
   * If the method id is known, create a NEMethodInvocation object with that.
   */
  public NEMethodInvocation (int objectKey, int methodId, Serializable[] args) {
    usingId = true;
    this.objectKey = objectKey;
    this.methodId = methodId;
    this.args = new Serializable[args.length];
    for (int i = 0; i < args.length; i++) {
      this.args[i] = args[i];
    }
  }
  
  /*
   * If the method id is not known, create a NEMethodInvocation object 
   * from the method name and method parameter types.
   */
  public NEMethodInvocation (int objectKey, String objectType, String methodName, 
      String[] argTypes, Serializable[] args) {
    usingId = false;
    this.objectKey = objectKey;
    this.objectType = objectType;
    this.methodName = methodName;
    this.argTypes = new String[args.length];
    this.args = new Serializable[args.length];
    for (int i = 0; i < args.length; i++) {
      this.argTypes[i] = argTypes[i];
      this.args[i] = args[i];
    }
  }
  
  /*
   * If the method id is not known, create a NEMethodInvocation object 
   * from the method name and method parameter types.
   */
  public NEMethodInvocation (int objectKey, Class<?> objectType, String methodName, 
      Class<?>[] argTypes, Serializable[] args) {
    usingId = false;
    this.objectKey = objectKey;
    this.objectType = objectType.getName ();
    this.methodName = methodName;
    this.argTypes = new String[args.length];
    this.args = new Serializable[args.length];
    for (int i = 0; i < args.length; i++) {
      this.argTypes[i] = argTypes[i].getName ();    
      this.args[i] = args[i];
    }
  }  
  
  /*
   * Get the method id
   */
  public int getMethodId () {
    return methodId;
  }
  
  public boolean isUsingId () {
    return usingId;
  }
  
  /*
   * Get the type of the object on which we are invoking this method.
   */
  public Class<?> getObjectType () throws ClassNotFoundException {
    return Class.forName (objectType);
  }
  
  /*
   * Get the name of the method
   */
  public String getMethodName () {
    return methodName;
  }
  
  /*
   * Get the object key
   */
  public int getObjectKey () {
    return objectKey;
  }
  
  /*
   * Get the types of the arguments
   */
  public Class<?>[] getArgumentTypes () throws ClassNotFoundException {
    Class<?>[] argTypes = new Class<?>[args.length];
    if (usingId) return null;
    
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = Class.forName (this.argTypes[i]);   
    }
    return argTypes;
  }
  
  /* 
   * Get the arguments to the method themselves. If a NERemoteObjectReference
   * was passed in, then the stub for the remote object represented by the
   * NERemoteObjectReference is returned instead
   */
  public Object[] getArguments () {
    Object[] arguments = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      // Argument is remote object
      if (args[i] != null &&
          args[i].getClass ().equals (NERemoteObjectReference.class)) {
        NERemoteObjectReference objectRef = (NERemoteObjectReference) args[i];
        arguments[i] = objectRef.localise ();
      }
      else arguments[i] = args[i];      
    }
    return arguments;
  }
  
  /*
   * Get the Method encapsulated within this method invocation
   */
  public Method getMethod () 
      throws NoSuchMethodException, SecurityException {
    try {
      Class<?> objectType = getObjectType ();
      return objectType.getMethod (getMethodName (), getArgumentTypes ());        
    }
    catch (ClassNotFoundException e) {
      // TODO: first check if class exists
      // Download class file
      return null;
    }
  }
  
  /* 
   * Get the String representation of this method invocation request message
   */
  public String toString () {
    String s = "";
    s += "Method name: " + methodName + "\n";
    s += "Object type: " + objectType + "\n";
    s += "Arg types  : " + "\n";
    for (int i = 0; i < argTypes.length; i++) {
      s += "\t" + argTypes[i] + "\n";
    }
    s += "Args  : " + "\n";
    for (int i = 0; i < args.length; i++) {
      s += "\t" + args[i] + "\n";
    }
    return s;
  }  
}
