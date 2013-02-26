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

public class NERemoteObjectServer {
  // The table of remote objects stored on the server, indexed by key
  private NERemoteObjectTable remoteObjectTable;
  
  // A queue of method invocations of some objects
  // TODO: make thread per object
  private ConcurrentLinkedQueue<NEMethodRequest> requests;
  
  private int port;
  private ServerSocket listener;
  
  public NERemoteObjectServer (int port)
      throws SocketException, IOException {
    remoteObjectTable = new NERemoteObjectTable ();
    requests = new ConcurrentLinkedQueue<NEMethodRequest> ();
    listener = new ServerSocket (port);
    this.port = port;
    
    new Thread (new Runnable () {
      public void run () { listenLoop (); }
    }).start ();
    new Thread (new Runnable () {
      public void run () { executeLoop (); }
    }).start ();    
  }
  
  /*
   * Add a remote object to the remote object table
   */
  public synchronized int add (NERemote o) {
    return remoteObjectTable.add (o);
  }
  
  /*
   * Remove a remote object from the remote object table
   */
  public synchronized void remove (int key) {
    remoteObjectTable.remove (key);
  }

  /*
   * Remove a remote object from the remote object table
   */
  public synchronized int replace (int key, NERemote o) {
    return remoteObjectTable.replace (key, o);
  }

  /*
   * Find a remote object from the remote object table
   */
  public synchronized NERemote getObject (int key) {
    return remoteObjectTable.get (key);
  }
  
  /*
   * The loop that executes method invocation requests
   */
  public void executeLoop () {
    while (true) {
      while (requests.isEmpty ()) {
        try {
          Thread.sleep (100);
        }
        catch (InterruptedException e) {
          // Should not reach here
          continue;
        }
      }
      
      NEMethodRequest request = requests.remove ();
      Socket client = request.getClient ();
      OutputStream out = null;
      
      try {
        out = client.getOutputStream ();
        
        try {
          if (request.getException () != null) {
            NEMarshaller.marshalException (request.getException (), out);
          }
          else {
            NEMethodCall method = request.getMethodCall ();
            System.out.println ("Method: " + method);
            Serializable result = (Serializable) request.getMethodCall ().invoke ();
            NEMarshaller.marshalReturnValue (new NEReturnValue (result), out);
          }
        }
        catch (IllegalAccessException e) {
          // should not reach here
          NEMarshaller.marshalException (new NEException (e), out);
        }
        catch (IllegalArgumentException e) {
          // Bad arguments were passed to the method
          NEMarshaller.marshalException (new NEException (e), out);
        }
        catch (InvocationTargetException e) {
          // Exception was thrown during method invocation
          NEMarshaller.marshalException (
            new NEException (new RuntimeException (e.getCause ())), 
            out);
        }
        
        out.close ();
        client.close ();
      }
      catch (SocketException e) {
        e.printStackTrace ();
        closeConnection (client, null, out);
        continue;
      }
      catch (IOException e) {
        e.printStackTrace ();
        closeConnection (client, null, out);
        continue;
      }
      

      
      // Execute command in new thread
      /*new Thread (new Runnable () {
        public void run () { 
          try {
            Object result = method.invoke ();
          }
          catch (IllegalAccessException e) {
          
          }
          catch (IllegalArgumentException e) {
          
          }
          catch (InvocationTargetException e) {
          
          }
        }
      }).start ();*/
    }
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
      
      NEMethodRequest request;
      NEMethodCall method = null;
      try {
        // Get the method from the method invocation
        synchronized (remoteObjectTable) {
          NEMethodInvocation mi = (NEMethodInvocation) in.readObject ();
          System.out.println (mi);
          System.out.println ("Table: " + remoteObjectTable);
          System.out.println (mi.getObjectKey ());
          System.out.println (getObject (mi.getObjectKey ()));
          method = NEDemarshaller.demarshalMethodInvocation
              (mi, getObject (mi.getObjectKey ()));
          System.out.println ("method: " + method);
          request = new NEMethodRequest (method, client);
        }
      }
      catch (ClassNotFoundException e) {
        // Should not reach here
        continue;
      }
      catch (IOException e) {
        e.printStackTrace ();
        request = new NEMethodRequest (method, client, new NEException (e));
      }
      catch (NoSuchMethodException e) {
        e.printStackTrace ();
        request = new NEMethodRequest (method, client, new NEException (e));
      }
      catch (SecurityException e) {
        e.printStackTrace ();
        request = new NEMethodRequest (method, client, new NEException (e));
      }
      
      requests.add (request);      
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
  
  /*
   * A private class representing a method invocation request from a 
   * client, storing the method and the client that invoked it.
   */
  private class NEMethodRequest {
    private NEMethodCall method;
    private Socket client;
    private NEException exception;
    
    public NEMethodRequest (NEMethodCall method, Socket client) {
      this.method = method;
      this.client = client;
      this.exception = null;
    }
    
    public NEMethodRequest (NEMethodCall method, Socket client, 
        NEException exception) {
      this.method = method;
      this.client = client;
      this.exception = exception;
    }    
    
    public NEMethodCall getMethodCall () {
      return method;
    }
    
    public Socket getClient () {
      return client;
    }
    
    public NEException getException () {
      return exception;
    }
  }
}
