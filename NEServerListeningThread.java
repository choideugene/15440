
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;




public class NEServerListeningThread extends Thread {
	
	private ServerSocket serverSocket;
	private ObjectInputStream ois;
	
	public NEServerListeningThread(int port)  throws  IOException{

		serverSocket = new ServerSocket(port);
	}
	
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ois = new ObjectInputStream(socket.getInputStream());
				
				synchronized (NERemoteServer.objTable) {
					NEMethodInvocation msg = ois.readObject();
					int id = msg.getObjectKey();
					
					
					NEMethodCall methodCall = NEDemarshaller.demarshalMethodInvocation(msg, NERemoteServer.objTable);
					NEMethodRequest req = new NEMethodRequest(method, socket);
					
					NERemoteObjectServerThread thread = NERemoteServer.objTable.getThread(id);
					thread.requests.add(req);
				}
				
			} catch (Exception ex) {
				System.out.println("error in listening to req: " + ex.toString());
			}
		}
	}
}