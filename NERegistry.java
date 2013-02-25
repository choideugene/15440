import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;

public class NERegistry extends NERemote {

	public static int REGISTRY_PORT = 1099; //well known port for registry
	
	private ConcurrentHashMap reg;
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String host;
	private int port;
	
	//localhost
	public NERegistry() { 
		ConcurrentHashMap<String, NERemoteObjectReference> reg = new HashMap();
		serverSocket = new ServerSocket(REGISTRY_PORT);
		host = "localhost";
		listen();
	}
	
	public NERegistry(String host) {
		this.host = host;
		this.port = REGISTRY_PORT;
	}
	
	public NERegistry(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	
	private void listen() {
		Socket socket;
		while (true) {
			try {
				socket = serverSocket.accept();
				NERegistryThread regThread = new NERegistryThread(this, socket);
				regThread.start();
			} catch (IOException ex) {
				System.out.println("error accepting socket: " + ex.toString());
			}
		}
	}
	
	public String[] list() throws NERemoteException, NEAccessException {
		if (host.equals("localhost")) {
			return localList();
		}
		return remoteList();
	}
	
	private String[] localList() {
		Set<String> set = (HashSet) reg.keySet();
		return (set.toArray(new String[0]));
	}
	
	private String[] remoteList() throws NERemoteException, NEAccessException {
		openRequest();
		try {
			outputStream.writeInt(0);
			outputStream.flush();
			int code = inputStream.readInt();
			Object obj = inputStream.readObject();
			if (code < 0) {
				Exception ex = (Exception) obj;
				System.out.println("error in remotelookup response: " + ex.toString());
				throw ex;
			} else {
				return (String[]) obj;
			}
		} catch(Exception ex) {
			System.out.println("datastream error in remotelookup: " + ex.toString());
			throw new NERemoteException();
		} finally {
			closeRequest();
		}
	}

	public NERemoteObjectReference lookup(String name) throws NERemoteException, NENotBoundException, NEAccessException {
		if (host.equals("localhost")) { 
			return localLookup(name);
		} 
		return remoteLookup(name);
	}

	private NERemoteObjectReference remoteLookup(String name) throws NERemoteException, NENotBoundException, NEAccessException {
		openRequest();
		try {
			outputStream.writeInt(1);
			outputStream.writeObject(name);
			outputStream.flush();
			int code = inputStream.readInt();
			Object obj = inputStream.readObject();
			if (code < 0) {
				Exception ex = (Exception) obj;
				System.out.println("error in remotelookup response: " + ex.toString());
				throw ex;
			} else {
				return (NERemoteObjectReference) obj;
			}
		} catch(Exception ex) {
			System.out.println("datastream error in remotelookup: " + ex.toString());
			throw new NERemoteException();
		} finally {
			closeRequest();
		}
	}
	
	private NERemoteObjectReference localLookup(String name) throws  NENotBoundException {
		NERemoteObjectReference ror;
		if ((ror = reg.get(name)) == null) {
			throw new NENotBoundException();
		}
		return ror;
	}
	
	public void bind(String name, NERemoteObjectReference obj) throws NERemoteException, NEAlreadyBoundException, NEAccessException{
		if (!host.equals("localhost")) {
			throw new NEAccessException();
		}
		if (reg.containsKey(name)) {
			throw new NEAlreadyBoundException();
		}
		reg.put(name, obj);
	}
	
	public void rebind(String name, NERemoteObjectReference obj) throws NERemoteException, NEAccessException {
		if (!host.equals("localhost")) {
			throw new NEAccessException();
		}
		reg.put(name, obj);
	}
	
	public void unbind (String name) throws NERemoteException, NENotBoundException, NEAccessException {
		if (!host.equals("localhost")) {
			throw new NEAccessException();
		}
		if (reg.remove(name) == null) {
			throw new NENotBoundException();
		}
	}
		
	private void openRequest() throws NERemoteException {
		try{ 
			this.socket = new Socket(host, REGISTRY_PORT);
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (Exception ex) {
			throw new NERemoteException(ex.toString());
		}
	}
	
	private void closeRequest() {
		try { 
			outputStream.close();
			inputStream.close();
		} catch (IOException ex) {
			System.out.println("error closing streams: " + ex.toString());
		}
		try {
			socket.close();
		} catch (IOException ex) {
			System.out.println("error closing socket: " + ex.toString());
		}
	}
	
	

}
