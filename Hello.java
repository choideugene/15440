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
      NERegistry reg = new NERegistry (5010);
      System.out.println ("Got the registry");
      
      NERemoteObjectServer ros = new NERemoteObjectServer (5002);
      System.out.println ("Started object server");
      
      int key = ros.add (server);
      System.out.println ("Added remote object to object server: " + key);
      System.out.println (ros.getObject (key));
      
      System.out.println ("Converted remote object to reference");
      
      NERemoteObjectReference ror = new NERemoteObjectReference 
        ("localhost", 5001, key, "HelloInterface");
      reg.bind (serverName, ror);

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
    if (name == null) throw new NullPointerException ();
    return "Hello World " + count +  "! Hello " + name; 
  }
}

