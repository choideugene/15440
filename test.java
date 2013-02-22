import java.rmi.*;
import java.rmi.registry.*;
import java.io.*;

class test {
  public static void main (String[] args) {
    try {
      String a = "Asdf";
      Integer b = new Integer (1234);
      b += 1;
      RMIReturnValue rv = new RMIReturnValue ("xs12");
      RMIException e = new RMIException (new Exception ("asdfwer"));
      ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream ("file"));
      
      out.writeObject (a);
      out.writeObject (b);
      out.flush ();
      out.close ();
      
      ObjectInputStream in = new ObjectInputStream (new FileInputStream ("file"));
      System.out.println (in.readObject ());
      System.out.println (in.readObject ());
      in.close ();
      
      rv.sendTo ("file");
      
      in = new ObjectInputStream (new FileInputStream ("file"));
      System.out.println (in.readObject ());
      in.close ();
      
      e.sendTo ("file");
      
      in = new ObjectInputStream (new FileInputStream ("file"));
      Exception ex = (Exception) in.readObject ();
      ex.printStackTrace ();
      in.close ();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

