import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.lang.NullPointerException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;


/* 
 * 0 = list
 * 1 = lookup
 * 2 = isRegistry
 */

 
 /* The thread first writes an int signalling success or error (and what type of error)
  * If there is a success, it will write the return as a second object
  * Else it will write the exception as the second object
  * 1 is success
  * -1 is NERemoteException
  * -2 is NEAccessException
  * -3 is NENotBoundException
  * //addendum: I suppose specific error codes is not necessary but
  * //it might be useful later, leaving it in for now though its not being used
  */ 
public class NERegistryThread extends Thread {
	
	private NERegistry registry;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private Socket socket;
	
	public NERegistryThread (NERegistry registry, Socket socket) {
		this.registry = registry;
		this.socket = socket;
	}
	
	private void closeThread() {
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (Exception ex) { 
			System.out.println("error ending registry listener thread: " + ex.toString());
		}
	}
	
	
	public void run() {
		try {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			System.out.println("error creating input/output streams for registry listener thread: " + ex.toString());
			return;
		}
		int methodKey = -1;
		String name = "";
		try {
			methodKey = inputStream.readInt();
			if (methodKey == 1) {
				name = (String) inputStream.readObject();
			}
		} catch (Exception ex) {
			System.out.println("error reading method request: " + ex.toString());
			try {
				outputStream.writeInt(-1);
				outputStream.writeObject(ex);
				outputStream.flush();
			} catch (Exception ex2) {
				System.out.println("error outputting response: " + ex2.toString());
			}
			closeThread();
			return;
		}
		try {
			if (methodKey != 1 || (methodKey != 0) || (methodKey != 3)) {
				System.out.println("invalid method request, code: " + methodKey);
				outputStream.writeInt(-2);
				outputStream.writeObject(new NEAccessException());
				closeThread();
				return;
			} else {
					
				if (methodKey == 1) {
					try {
						NERemoteObjectStub ror = registry.lookup(name);
						ror.getRemoteObjectReference ();
						outputStream.writeInt(1);
						outputStream.writeObject(ror);
					} catch (NENotBoundException ex) {
						outputStream.writeInt(-3);
						outputStream.writeObject(ex);
					} catch (Exception ex) {
						outputStream.writeInt(-1);
						outputStream.writeObject(ex);
					}
					
				} else if (methodKey == 0) {
					try { 
						outputStream.writeObject(registry.list());
						outputStream.writeInt(1);
					} catch (Exception ex) {
						outputStream.writeInt(-1);
						outputStream.writeObject(ex);
					}
				} else if (methodKey == 3) {
					outputStream.writeBoolean(true);
					
				}
			}
				outputStream.flush();
		}	catch (Exception ex) {
				System.out.println("error sending response: " + ex.toString());
		}
		
		closeThread();
	}
}
