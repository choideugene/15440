// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 1/22/03 1:40:21 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

import java.io.*;
import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;

public final class HelloInterface_Stub extends NERemoteObjectStub implements HelloInterface, NERemote {
  public String sayHello (String s) throws NERemoteException {
    try {
      Class<?>[] argTypes = { String.class };
      Serializable[] arguments = { s };
      System.out.println (getKey ());
      NEMethodInvocation message =
        new NEMethodInvocation 
          (getKey (), HelloInterface.class, "sayHello", argTypes, arguments);
      
      Socket reg = new Socket ("localhost", 5001);
      System.out.println ("Connected to object server");
           
      NEMarshaller.marshalMethodInvocation (message, reg.getOutputStream ());
      System.out.println ("Marhsalled method invocation");
      
      ObjectInputStream in = new ObjectInputStream (reg.getInputStream ());
      char rtype = in.readChar ();
      if (rtype == 'e') {
        NEException ex = (NEException) in.readObject ();
        System.out.println ("Received exception");
        
        reg.close ();
        throw ex.getException ();
      }
      else if (rtype == 'r') {
        NEReturnValue rv = (NEReturnValue) in.readObject ();
        System.out.println ("Received return value");
      
        String result = (String) NEDemarshaller.demarshalReturnValue (rv);
        reg.close ();
        return result;
      } 
    }
    catch (SocketException e) {
      throw new NERemoteException ("Communication with server failed");
    }    
    catch (IOException e) {
      throw new NERemoteException ("Communication with server failed");
    }
    catch (ClassNotFoundException e) {
      // Should not reach here
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
    
    }
    return null;
  }
}
