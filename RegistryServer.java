import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.*;

public class RegistryServer {
  public static void main (String[] args) {
    try {
      if (args.length != 1) {
        System.out.println ("usage: RegistryServer <port>");
        return;
      }
      
      int port = -1;
      try {
        port = Integer.parseInt (args[0]);
      }
      catch (NumberFormatException e) {
        System.out.println ("usage: RegistryServer <port>");
        return;
      }
      
      NERegistryServer registryServer = new NERegistryServer (port);
    }
    catch (IOException e) {
      e.printStackTrace ();
      return;
    }
    
    return;
  }
}
