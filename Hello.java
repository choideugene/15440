import java.rmi.*;
import java.rmi.server.*;

public class Hello implements HelloInterface {
  private static final String serverName = "hello";
  private int count;

  public Hello () {
    count = 0;
  }

  // This is actually the starting point that registers the object, &c
  public static void main (String[] args) {
    try {
      // Instantiate an instance of this class -- create a Hello object
      Hello server = new Hello ();
      System.out.println ("Created remote object");

      // Tie the name "Hello" to the Hello object we just created
      NERegistry reg = NERegistryLocator.getRegistry ("localhost", 5000);
      System.out.println ("Got the registry");
      
      try {
        NERemoteObjectServer ros = new NERemoteObjectServer (5001);
      }
      catch (Exception e) {
        e.printStackTrace ();
        return;
      }
      System.out.println ("Started object server");
      
      System.out.println ("Converted remote object to reference");
      
      NERemoteObjectReference ror = new NERemoteObjectReference 
        ("localhost", 5001, 0, "HelloInterface");
      reg.rebind (serverName, ror);

      // Just a console message
      System.out.println ("Hello Server ready");
    }
    catch (Exception e) {
      // Bad things happen to good people
      e.printStackTrace();
    }
  }

  // This is the one real method
  public String sayHello (String name) throws NERemoteException {
    count++;
    return "Hello World " + count +  "! Hello " + name; 
  }
}

