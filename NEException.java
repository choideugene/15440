import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEException implements NEMessageable {
  Exception exception;
  
  public NEException (Exception e) {
    exception = e;
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