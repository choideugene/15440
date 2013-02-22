import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEMarshaller {
  public static void marshal (NEMessageable message, OutputStream outputStream) {
    try {
      ObjectOutputStream out = new ObjectOutputStream (outputStream);
      out.writeObject (message);
      out.flush ();
      out.close ();
    }
    catch (Exception e) {
      e.printStackTrace ();
    }
  } 
}
