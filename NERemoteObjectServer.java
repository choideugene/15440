import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;

public class NERemoteObjectServer {
  private NERemoteObjectTable remoteObjectTable;
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
  
  public synchronized void add (NERemote o) {
    remoteObjectTable.add (o);
  }
  
  public synchronized void remove (int key) {
    remoteObjectTable.remove (key);
  }  
  
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
          if (request.getResult () != null) {
            NEMarshaller.marshal (request.getResult (), out);
          }
          else {
            NEMethodCall method = request.getMethodCall ();
            System.out.println ("Method: " + method);
            Serializable result = (Serializable) request.getMethodCall ().invoke ();
            NEMarshaller.marshal (new NEReturnValue (result), out);
          }
        }
        catch (IllegalAccessException e) {
          NEMarshaller.marshal (new NEException (e), out);
        }
        catch (IllegalArgumentException e) {
          NEMarshaller.marshal (new NEException (e), out);
        }
        catch (InvocationTargetException e) {
          NEMarshaller.marshal (new NEException (e), out);
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
        NEMethodInvocation mi = (NEMethodInvocation) in.readObject ();
        System.out.println (mi);
        method = NEDemarshaller.demarshalMethodInvocation
            (mi, remoteObjectTable);
        System.out.println ("method: " + method);
        request = new NEMethodRequest (method, client);    
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
  
  private class NEMethodRequest {
    private NEMethodCall method;
    private Socket client;
    private NEMessageable result;
    
    public NEMethodRequest (NEMethodCall method, Socket client) {
      this.method = method;
      this.client = client;
      this.result = null;
    }
    
    public NEMethodRequest (NEMethodCall method, Socket client, 
        NEMessageable result) {
      this.method = method;
      this.client = client;
      this.result = result;
    }    
    
    public NEMethodCall getMethodCall () {
      return method;
    }
    
    public Socket getClient () {
      return client;
    }
    
    public NEMessageable getResult () {
      return result;
    }
  }
}
