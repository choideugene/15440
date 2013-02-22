import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class RMIRemoteMethodCall {
  private String name;
  private Serializable [] args;

  public RMIRemoteMethodCall (String name, Serializable[] args) {
    this.name = name;
    this.args = new Serializable [args.length];
    for (int i = 0; args.length; i++) {
      this.args [i] = args [i];
    }
  }
  
  public String getName () {
    return name;
  }
  
  public Serializable [] getArgs () {
    
  }  
}
