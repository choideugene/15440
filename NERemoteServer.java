
import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;



public abstract class NERemoteServer { 
	private ConcurrentHashMap<NERemote> objTable;
	public static int REGISTRY_PORT = 1099;
	
	private ConcurrentHashMap<NERemoteServerThread> threadPool;
	
	private int port;
	private String addr;
	private int index;
	private NERegistry registry;
	
	public NERemoteServer(String addr, int port) throws IOException {
		objTable =  new ConcurrentHashMap();
		registry = new NERegistry();
		index = 0;
	}
	
	//call this upon object creation
	private void remoteInit(String name, NERemote obj, int portnum) throws NEAlreadyBoundException {
		NERemoteObjectReference ror = new NERemoteObjectReference(addr, portnum, index++, "");
		
		
		registry.bind(name, ror);
		objTable.insert(index, obj);
		NERemoteServerThread rst = new NERemoteServerThread(portnum, obj);
		rst.start();
		threadPool.insert(index, rst);
		
	}
	
	
	//rebind
	private void remoteReassign(String name, NERemote obj, int portnum) throws NERemoteException,  NEAccessException {
		NERemoteObjectReference ror = new NERemoteObjectReference(addr, portnum, index++, "");
		
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