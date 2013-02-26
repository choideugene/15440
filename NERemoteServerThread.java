
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


	
	
/*
 * Class      : NERemoteServerThread.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: A server thread that deals with method invocations specific
 *              to a designated object.
 */





public class NERemoteServerThread extends Thread {
  public ConcurrentLinkedQueue<NEMethodRequest> requests;
  public boolean running;
  
  public NERemoteServerThread() {
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

  
	/*
	private NERemote obj;
	private int port;
	private ServerSocket serverSocket;
	public boolean running;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public NERemoteServerThread(int port, NERemote obj) {
		this.obj = obj;
		this.port = port;
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(200);
			running = true;
		} catch (Exception ex) {
			System.out.println("socket error: " + ex.toString());
			running = false;
			
		}
	}
	
	
	
	public void run()  {
		Socket socket = null;
		while (running) {
			try {
				socket = serverSocket.accept();
			} catch (SocketTimeoutException ex) {
				continue;
				//do nothing, it's okay for it to timeout
			} catch (Exception ex) {
				System.out.println(ex.toString());
				running = false; //not strictly needed with break but good convention
				break;
			}
			try {
				oos = new ObjectOutputStream(oos);
				ois = new ObjectInputStream(ois);
				
				NEMethodInvocation msg = ois.readObject();
			} catch (Exception ex) {
				System.out.println(ex.toString());
				running = false;
				break;
			}
			try {
				NEMethodCall methodCall = NEDemarshaller.demarshalMethodInvocation(msg, remoteObjectTable);
				NEMethodRequest req = new NEMethodRequest(method, client);
				if (req.getResult() != null) {
					NEMarshaller.marshal (req.getResult(), oos);
				} else {
					Serializable result = (Serializable) methodCall.invoke ();
					NEMarshaller.marshal(new NEReturnValue(result), oos);
				}
			} catch (Exception ex) {
				NEMarshaller.marshal(new NEException(ex), oos);
			}
			try{ 
			oos.close();
			ois.close();
			socket.close();
			} catch (Exception ex) {
				System.out.println("error closing: " + ex.toString());
			}
		}
		try {
			oos.close();
			ois.close();
			if (socket != null) {
				socket.close();
			}
		} catch (Exception ex) {
			//do nothing, they just weren't initialized
		}
	} */