import java.net.*;
import java.io.*;

public class NERegistryLocator {
  public static NERegistry getRegistry (String address, int port)
      throws NERegistryNotFoundException {
    try {
      Socket socket = new Socket (address, port);
      System.out.println ("Created socket");
      
      ObjectOutputStream out = new ObjectOutputStream (socket.getOutputStream ());
      System.out.println ("Created output stream");
      
      out.writeChar ('a');
      out.flush ();
      System.out.println ("Asked registry");
      
      ObjectInputStream in = new ObjectInputStream (socket.getInputStream ()); 
      System.out.println ("Created input stream");      
      
      if (in.readChar () == 'y') {
        out.close ();
        in.close ();
        socket.close ();
        return new NERegistry (address, port);
      }
      else throw new NERegistryNotFoundException (address, port);
    }
    catch (SocketException e) {
      e.printStackTrace ();
      throw new NERegistryNotFoundException (address, port);
    }
    catch (IOException e) {
      e.printStackTrace ();
      throw new NERegistryNotFoundException (address, port);
    }
  }
}


