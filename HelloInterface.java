import java.rmi.*;
import java.rmi.server.*;

interface HelloInterface extends NERemote {
  public String sayHello (String name) throws NERemoteException;
}

