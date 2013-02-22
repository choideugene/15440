import java.io.*;
import java.net.*;
import java.lang.*;
import java.lang.reflect.*;

public class NEMethodInvocation implements Serializable {
  private int methodId;
  private Serializable[] args;
  private boolean[] remote;

  public NEMethodInvocation (int methodId, Serializable[] args, boolean[] remote) {
    this.methodId = methodId;
    this.args = new Serializable[args.length];
    this.remote = new boolean[args.length];
    for (int i = 0; i < args.length; i++) {
      this.args[i] = args[i];
      this.remote[i] = remote[i];
    }
  }
  
  public int getMethodId () {
    return methodId;
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
