/*
 * Class      : NERegsitryServer.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This is where the heart of the RMI registry is. The table
 *              mapping names to remote object references are here, and this
 *              class deals with the registry commands (lookup, bind, rebind,
 *              unbind, list)
 */

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.*;

public class NERegistryServer {
  private ConcurrentHashMap<String,NERemoteObjectReference> table;
  private int port;
  
  private LinkedList<NERegistryCommand> commands;
  
  private ServerSocket listener;
  
  /*
   * New registry server
   */
  public NERegistryServer (int port) 
      throws SocketException, IOException {
    this.port = port;
    table = new ConcurrentHashMap<String,NERemoteObjectReference> ();
    listener = new ServerSocket (port);
    commands = new LinkedList<NERegistryCommand> ();
    
    new Thread (new Runnable () {
      public void run () { acceptLoop (); }
    }).start ();
    new Thread (new Runnable () {
      public void run () { executeLoop (); }
    }).start ();
    
    System.out.println ("Registry Server Started");
  }
  
  /*
   * Execution each registry command
   */
  // TODO: fixup closing streams on exceptions
  private void executeLoop () {
    String name, addr, riname;
    int port, key;
    
    while (true) {
      while (commands.isEmpty ()) {
        try {
          Thread.sleep (100);
        }
        catch (InterruptedException e) {
          // Should not reach here
          continue;
        }
      }
      
      System.out.println (commands);
      executeCommand (commands.remove ());
    }
  }
  
  /*
   * Execute requested command. 
   * The following symbols are flags to indicate certain results after
   * the execution of some of the above commands:
   *    f = requested object found
   *    n = requested object not found
   *    a = rebind successful
   *    y = this is a registry server
   */
  // TODO: fixup closing streams on exceptions
  private void executeCommand (NERegistryCommand command) {
    String name, addr, riname;
    NERemoteObjectReference ref;
    int port, key;
    
    try {
      Socket client = command.getClient ();
      ObjectOutputStream out = new ObjectOutputStream (client.getOutputStream ());
      
      char request = (Character) command.removePart ();
      
      switch (request) {
        // lookup
        case 'l': 
          name = (String) command.removePart ();
          if (table.containsKey (name)) {
            out.writeChar ('f');
            out.writeObject (table.get (name));
          }
          else out.writeChar ('n');
          out.flush ();
          break;
        
        // rebind
        case 'r':
          name = (String) command.removePart ();
          addr = (String) command.removePart ();
          port = (Integer) command.removePart ();
          key = (Integer) command.removePart ();
          riname = (String) command.removePart ();
          
          ref = new NERemoteObjectReference (addr, port, key, riname);
          table.put (name, ref);
          break;
          
        // bind
        case 'b':
          name = (String) command.removePart ();
          if (table.containsKey (name)) {
            out.writeChar ('1');
            out.flush ();
            break;
          }
          
          addr = (String) command.removePart ();
          port = (Integer) command.removePart ();
          key = (Integer) command.removePart ();
          riname = (String) command.removePart ();
          
          ref = new NERemoteObjectReference (addr, port, key, riname);
          table.put (name, ref);
          
          out.writeChar ('0');
          out.flush ();
          break;          
          
        // unbind
        case 'u':
          name = (String) command.removePart ();
          if (! table.containsKey (name)) {
            out.writeChar ('2');
            out.flush ();
            break;
          }
          table.remove (name);
          
          out.writeChar ('0');
          out.flush ();
          break;          
          
        // list
        case 'p':
          out.writeInt (table.size ());
          for (String n : table.keySet ()) {
            out.writeObject (n);
            out.writeObject (table.get (n));
          }
          out.flush ();
          break;     
        
        // ask
        case 'a':
          out.writeChar ('y');
          out.flush ();
          break;
      }
      
      out.close ();
      client.close ();
    }
    catch (SocketException e) {
      // Ignore
      e.printStackTrace ();
    }
    catch (IOException e) {
      // Ignore
      e.printStackTrace ();
    }
  }  
    
  /*
   * Listen for incoming requests.
   * The following symbols indicate various commands the registry server
   * should execute:
   *    l = lookup
   *    r = rebind
   *    u = unbind
   *    p = list
   *    a = ask (Are you a registry server?)
   */
  // TODO: fixup closing streams on exceptions
  private void acceptLoop () {
    while (true) {
      Socket client = null;
      NERegistryCommand command;
      try {
        client = listener.accept ();
        
        command = new NERegistryCommand (client);
        ObjectInputStream in = new ObjectInputStream (client.getInputStream ());
        
        char request = in.readChar ();
        System.out.println ("Request: " + request);
        command.addPart (new Character (request));
        
        switch (request) {
          // lookup
          case 'l': 
            command.addPart ((String) in.readObject ());
            commands.add (command);
            break;
          
          // rebind
          case 'r':
            command.addPart ((String) in.readObject ());
            command.addPart ((String) in.readObject ());
            command.addPart (new Integer (in.readInt ()));
            command.addPart (new Integer (in.readInt ()));
            command.addPart ((String) in.readObject ());
            commands.add (command);
            break;
            
          // bind
          case 'b':
            command.addPart ((String) in.readObject ());
            command.addPart ((String) in.readObject ());
            command.addPart (new Integer (in.readInt ()));
            command.addPart (new Integer (in.readInt ()));
            command.addPart ((String) in.readObject ());
            commands.add (command);
            break;            
            
          // unbind
          case 'u':
            command.addPart ((String) in.readObject ());
            commands.add (command);
            break;          
            
          // list
          case 'p':
            commands.add (command);
            break;
            
          // ask
          case 'a':
            commands.add (command);
            break;       
            
          // not a command
          default :
            break;           
        }
      }
      catch (ClassNotFoundException e) {
        // Should not reach here
        try {
          if (client != null) client.close ();
        }
        catch (IOException ex) {
          // ignore
        }
        continue;
      }
      catch (SocketException e) {
        // Ignore
        try {
          if (client != null) client.close ();
        }
        catch (IOException ex) {
          // ignore
        }
        continue;
      }
      catch (IOException e) {
        //System.out.println ("Could not accept slave connection on port " + 
        //                    localport + ": " +e);
        try {
          if (client != null) client.close ();
        }
        catch (IOException ex) {
          // ignore
        }
        continue;
      }
    }
  }
  
  private class NERegistryCommand {
    private Socket client;
    private LinkedList<Object> command;
    
    public NERegistryCommand (Socket client) {
      this.client = client;
      this.command = new LinkedList<Object> ();
    }
    
    public void addPart (Object o) {
      command.add (o);
    }
    
    public Socket getClient () {
      return client;
    }
    
    public Object removePart () {
      return command.remove ();
    }
    
    public String toString () {
      return command.toString ();
    }
  }
}
