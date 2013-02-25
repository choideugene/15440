import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEMarshaller {
  public static void marshal (NEMessageable message, OutputStream outputStream) {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream (outputStream);
      out.writeObject (message);
      out.flush ();
      out.close ();
    }
    catch (Exception e) {
      try {
        if (out != null) out.close ();
      }
      catch (IOException ex) {
        // ignore
      }
      e.printStackTrace ();
    }
  } 
}
