import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public abstract class NEMessage implements Serializable {
  protected Serializable object;
  
  public void sendTo (String file) {
    try {
      ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (file));
      out.writeObject (object);
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
      out.writeObject (object);
      out.flush ();
      out.close ();
    }
    catch (Exception e) {
      e.printStackTrace ();
    }
  }
}
