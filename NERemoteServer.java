
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public abstract class NERemoteServer { 
	private static NERemoteObjectTable objTable;
	public static int REGISTRY_PORT = 1099;
	
	
	private int port;
	private String addr;
	private NERegistry registry;
	
	public NERemoteServer(String addr, int port) throws IOException {
		objTable =  new NERemoteObjectTable();
		registry = new NERegistry();
    
		NEServerListeningThread listener = new NEServerListeningThread(port);
    listener.start();
	}
	
	public NERemoteServer(String addr) throws IOException {
		objTable =  new NERemoteObjectTable();
		registry = new NERegistry();
		NEServerListeningThread listener = new NEServerListeningThread(REGISTRY_PORT);
    listener.start();
	}
	
	//call this upon object creation
  
	private void remoteInit(String name, NERemote obj) throws NEAlreadyBoundException {
		
		int id = objTable.add(obj);
		
		NERemoteObjectReference ror = new NERemoteObjectReference(addr, port, id, "");
		registry.bind(name, ror);
		NERemoteObjectServerThread rst = new NERemoteObjectServerThread(portnum, obj);
		rst.start();
		threadPool.insert(index, rst);
		
	}
	
	
	//rebind
	private void remoteReassign(String name, NERemote obj) throws NERemoteException,  NEAccessException {
		NERemoteObjectReference ror = new NERemoteObjectReference(addr, port, index++, "");
		
		try {
			remoteRemove(name);
		} catch (NENotBoundException ex) {}  
		
		try {
			remoteInit(name, obj);
		} catch (NEAlreadyBoundException ex) {
			System.out.println("something went horribly wrong: " + ex.toString());
			
			//should never get here
		}
	}
	
	private void remoteRemove(String name) throws NERemoteException, NENotBoundException, NEAccessException {
		NERemoteObjectReference ror = registry.lookup(name);
		int key = ror.getKey();
		objTable.remove(ror.getKey());
		registry.unbind(name);
		NERemoteServerThread rst = threadPool.remove(key);
		rst.running = false;;
	}
}