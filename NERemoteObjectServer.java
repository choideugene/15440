/*
 * Class      : NERemoteObjectServer.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: This object server manages the objects stored in the server
 *              side of RMI.
 */


import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.*;

public class NERemoteObjectServer {
  // The table of remote objects stored on the server, indexed by key
  private NERemoteObjectTable remoteObjectTable;
  
  // A table mapping each object (key) to its NERemoteObjectServerThread
  private HashMap<Integer, NERemoteObjectServerThread> threads;
  
  private int port;
  private ServerSocket listener;
  
  public NERemoteObjectServer (int port)
      throws SocketException, IOException {
    remoteObjectTable = new NERemoteObjectTable ();
    threads = new HashMap<Integer, NERemoteObjectServerThread> ();
    listener = new ServerSocket (port);
    this.port = port;
    
    new Thread (new Runnable () {
      public void run () { listenLoop (); }
    }).start ();  
  }
  
  /*
   * Add a remote object to the remote object table
   */
  public synchronized int add (NERemote o) {
    int key = remoteObjectTable.add (o);
    NERemoteObjectServerThread thread = new NERemoteObjectServerThread (o);
    thread.start ();
    threads.put (key, thread);
    return key;
  }
  
  /*
   * Remove a remote object from the remote object table
   */
  public synchronized void remove (int key) {
    remoteObjectTable.remove (key);
    threads.get (key).killThread ();
    threads.remove (key);
  }

  /*
   * Remove a remote object from the remote object table
   */
  public synchronized void replace (int key, NERemote o) {
    threads.get (key).killThread ();
    threads.remove (key);
    NERemoteObjectServerThread thread = new NERemoteObjectServerThread (o);
    thread.start ();
    threads.put (key, thread);    
    remoteObjectTable.replace (key, o, thread);
  }

  /*
   * Find a remote object from the remote object table
   */
  public synchronized NERemote getObject (int key) {
    return remoteObjectTable.get (key);
  }
  
  /*
   * The loop that listens for incoming method invocation requests
   */
  public void listenLoop () {
    while (true) {
      Socket client = null;
      ObjectInputStream in = null;
      try {
        // Listen for and accept client connection
        client = listener.accept ();
        System.out.println ("Client connect: " + client);
        
        // Get input stream
        in = new ObjectInputStream (client.getInputStream ());
        System.out.println ("Got input stream");
      }
      catch (SocketException e) {
        e.printStackTrace ();
        closeConnection (client, in, null);
        continue;
      }
      catch (IOException e) {
        e.printStackTrace ();
        closeConnection (client, in, null);
        continue;
      }
      
      NERemoteObjectMethodRequest request;
      NEMethodCall method = null;
      NEMethodInvocation mi = null;
      int key = -1;
      
      try {
        // Read the method invocation from the client socket
        mi = (NEMethodInvocation) in.readObject ();
        System.out.println (mi);
        System.out.println ("Table: " + remoteObjectTable);
        System.out.println (mi.getObjectKey ());
        System.out.println (getObject (mi.getObjectKey ()));
        key = mi.getObjectKey ();
      }
      catch (IOException e) {
        e.printStackTrace ();
        closeConnection (client, in, null);
        continue;
      }      
      catch (ClassNotFoundException e) {
        // Should not reach here
        continue;
      }      
      
      try {
        // Get the method from the method invocation
        synchronized (remoteObjectTable) {
          method = NEDemarshaller.demarshalMethodInvocation (mi, getObject (key));
          System.out.println ("method: " + method);
          request = new NERemoteObjectMethodRequest (method, client);
        }
      }
      catch (NoSuchMethodException e) {
        e.printStackTrace ();
        request = new NERemoteObjectMethodRequest (method, client, new NEException (e));
      }
      catch (SecurityException e) {
        e.printStackTrace ();
        request = new NERemoteObjectMethodRequest (method, client, new NEException (e));
      }
      
      // Add the method request job the the target object's request queue
      threads.get (key).addMethodRequest (request); 
    }
  }
  
  /* 
   * Close all connections to a client
   */
  private void closeConnection (Socket client, InputStream in, OutputStream out) {
    try {
      if (in != null) in.close ();
    }
    catch (SocketException e) {
      // ignore
    }
    catch (IOException e) {
      // ignore
    }
    
    try {
      if (out != null) out.close ();
    }
    catch (SocketException e) {
      // ignore
    }
    catch (IOException e) {
      // ignore
    }
    
    try {
      if (client != null) client.close ();
    }
    catch (SocketException e) {
      // ignore
    }
    catch (IOException e) {
      // ignore
    }
  }  
}
