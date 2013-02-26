
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;



public abstract class NERemoteServerThread extends Thread { 
	
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
	}
}