import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;

public class RMIReturnValue extends RMIMessage {
  public RMIReturnValue (Class returnType, Object returnValue) {
    if (Arrays.asList (returnType.getInterfaces ()).contains ("Remote")) {
      System.out.println ("Is Remote");
    }
    object = returnValue;
  }
  
  public static void main (String[] args) {
    RMIReturnValue rv = new RMIReturnValue ("asdf".getClass (), "asdf");
    
  }
}
