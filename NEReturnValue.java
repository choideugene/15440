import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEReturnValue implements NEMessageable {
  Serializable object;
  
  public NEReturnValue (Serializable o) {
    object = o;
  }
  
  public NEReturnValue (NERemote o) {
    object = new NERemoteObjectReference (o);
  } 
  
  public void sendTo (String file) {
    try {
      ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (file));
      out.writeObject (exception);
      out.flush ();
      out.close ();
    }
    catch (Exception e) {
      e.printStackTrace ();
    }
  }
  
  public void sendTo (Socket dest) {
    try {
      ObjectOutputStream out = new ObjectOutputStream (dest.getOutputStream ());
      out.writeObject (exception);
      out.flush ();
      out.close ();
    }
    catch (Exception e) {
      e.printStackTrace ();
    }
  }  
}
