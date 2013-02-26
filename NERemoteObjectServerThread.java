/*
 * Class      : NERemoteObjectServerThread.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: A server thread that deals with method invocations specific
 *              to a designated object.
 */


import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.*;

public class NERemoteObjectServerThread extends Thread {
  private NERemote object;
  private LinkedList<NERemoteObjectMethodRequest> requests;
  private boolean kill;
  
  public NERemoteObjectServerThread (NERemote object) {
    this.object = object;
    requests = new LinkedList<NERemoteObjectMethodRequest> ();
    kill = false;
  }
  
  /*
   * Add method request to this object's request queue
   */
  public synchronized void addMethodRequest (NERemoteObjectMethodRequest req) {
    requests.add (req);
  }
  
  /*
   * Setup the thread for exit
   */
  public void killThread () {
    kill = true;
  }
  
  /*
   * The loop that executes method invocation requests
   */
  public void run () {
    while (true && ! kill) {
      while (requests.isEmpty ()) {
        try {
          Thread.sleep (100);
        }
        catch (InterruptedException e) {
          // Should not reach here
          continue;
        }
      }
      
      NERemoteObjectMethodRequest request = requests.remove ();
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
