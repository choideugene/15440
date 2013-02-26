
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;



public class NERemoteObjectServerThread extends Thread {
  public ConcurrentLinkedQueue<NEMethodRequest> requests;
  public boolean running;
  
  public NERemoteObjectServerThread() {
    running = true;
    requests = new ConcurrentLinkedQueue();
  }
  
  public void run() {
    while (running) {
      if (requests.isEmpty()) {
        try {
          Thread.sleep(100);
        } catch (Exception ex) {
          System.out.println("error with thread sleep: " + ex.toString());
        }
      } else {
        NEMethodRequest request = requests.remove ();
        Socket client = request.getClient ();
        OutputStream out = null;
      
        try {
          out = client.getOutputStream ();
          
          try {
            if (request.getException() != null) {
              NEMarshaller.marshalException(request.getException (), out);
            }
            else {
              NEMethodCall method = request.getMethodCall ();
              System.out.println("Method: " + method);
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
  }
  
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