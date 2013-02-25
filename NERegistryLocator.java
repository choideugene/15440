import java.net.*;
import java.io.*;

public class NERegistryLocator {
  public static NERegistry getRegistry (String address, int port)
      throws NERegistryNotFoundException {
    try {
      Socket socket = new Socket (address, port);
      
      ObjectInputStream in = new ObjectInputStream (socket.getInputStream ()); 
      ObjectOutputStream out = new ObjectOutputStream (socket.getOutputStream ());
      
      out.writeChar ('a');
      
      if ((in.readChar ()).equals ('c')) {
        return new NERegistry (address, port);
      }
      else throw new NERegistryNotFoundException (address, port);
    }
    catch (SocketException e) {
      System.out.println (e);
      throw new NERegistryNotFoundException (address, port);
    }
    catch (IOException e) {
      System.out.println (e);
      throw new NERegistryNotFoundException (address, port);
    }
  }
}


