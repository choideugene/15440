/*
 * Class      : NERemoteObjectReference
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: Encapsulates server and other information about a remote
 *              object. This class is meant to be the "reference" to a 
 *              remote object that can be passed around remotely (as a 
 *              Serializable object).
 */

import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NERemoteObjectReference implements Serializable {
  private String hostAddress, remoteInterfaceName;
  private int port;
  private int key;

  public NERemoteObjectReference (String addr, int p, int k, String riname) {
    hostAddress = addr;
    port = p;
    remoteInterfaceName = riname;
    key = k;
  }
  
  /*
   * Get the address of the server hosting the remote object
   */
  public String getHostAddress () {
    return hostAddress;
  }
  
  /*
   * Get the port of the server hosting the remote object
   */
  public int getPort () {
    return port;
  }
  
  /*
   * Get the key of the remote object. The key is an id that allows the
   * server to locate the actual remote object.
   */
  public int getKey () {
    return key;
  }
  
  /*
   * Get the name of the remote interface that the remote object implements
   */
  public String getRemoteInterfaceName () {
    return remoteInterfaceName;
  }

  /*
   * Localise the remote object reference. Returns the stub for the remote
   * object
   */
  public NERemoteObjectStub localise () {
    // TODO:
    // 1. Check if riname + "_Stub" class exists
    // 2. If not, automatically create it
    try {
      String stubTypeName = remoteInterfaceName + "_Stub";
      System.out.println ("Stubname: " + stubTypeName);
      Class stubType = Class.forName (stubTypeName);
      NERemoteObjectStub stub = (NERemoteObjectStub) stubType.newInstance ();
      stub.init (this);
      return stub;
    }
    catch (ClassNotFoundException e) {
      // generate stub, etc
      e.printStackTrace ();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace ();
    }    
    catch (InstantiationException e) {
      e.printStackTrace ();
    }
    return null;
    
    // Implement this as you like: essentially you should 
    // create a new stub object and returns it.
    // Assume the stub class has the name e.g.
    //
    //       Remote_Interface_Name + "_stub".
    //
    // Then you can create a new stub as follows:
    // 
    //       Class c = Class.forName(Remote_Interface_Name + "_stub");
    //       Object o = c.newinstance()
    //
    // For this to work, your stub should have a constructor without arguments.
    // You know what it does when it is called: it gives communication module
    // all what it got (use CM's static methods), including its method name, 
    // arguments etc., in a marshalled form, and CM (yourRMI) sends it out to 
    // another place. 
    // Here let it return null.
    //return null;
  }
  
  /*
   * Are to references pointing to the same remote object?
   */
  public boolean equals (Object o) {
    NERemoteObjectReference r = (NERemoteObjectReference) o;
    try {
      return (InetAddress.getByName (r.getHostAddress ()).equals 
                (InetAddress.getByName(hostAddress)) &&
              r.getRemoteInterfaceName ().equals (remoteInterfaceName) &
              r.getPort () == port &&
              r.getKey () == key);
    }
    catch (UnknownHostException e) {
      return false;
    }
  }
  
  public String toString () {
    return "Address: " + hostAddress + "\n" +
           "Port   : " + port + "\n" +
           "RIName : " + remoteInterfaceName + "\n" +
           "Key    : " + key + "\n";
  } 
}
