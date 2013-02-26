import java.rmi.*;
import java.rmi.registry.*;

public class HelloClient {
  // You'll want to change this to match your own host
  private static final String HelloServerURL = "rmi://localhost/hello";

  // This takes one command line argument: A person's first name
  public static void main (String[] args) {
    try {
      //System.setSecurityManager (new RMISecurityManager());
      NERegistry reg = new NERegistry ("localhost", 5010);
      HelloInterface_Stub hello = (HelloInterface_Stub) reg.lookup("hello");
      System.out.println (hello);
      System.out.println ("Reference: " + hello.getRemoteObjectReference ());
      
      String a;
      if (args.length == 0) a = null;
      else a = args[0];
      
      String theGreeting = hello.sayHello (a);

      System.out.println (theGreeting);
    }
    catch (Exception e)
    {
      // Bad things can happen to good people
      e.printStackTrace();
    }
  }
}

