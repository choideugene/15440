import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.lang.NullPointerException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;


public class NERegistryListenerThread extends Thread {
	
	private ServerSocket serverSocket;
	
	
	public NERegistryListenerThread(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void run() {
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
}