import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NERemoteObjectStub implements NERemote {
  protected String hostAddress;
  protected int port;
  protected int key;
  
  public void init (NERemoteObjectReference objectRef) {
    hostAddress = objectRef.getHostAddress ();
    port = objectRef.getPort ();
    key = objectRef.getKey ();
  }
}
