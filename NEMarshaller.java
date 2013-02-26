/*
 * Class      : NEMarshaller.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: Handles marshalling NEMessageable messages
 */


import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEMarshaller {
  /*
   * Marshal out the NEMessageable message to the given output stream. If
   * some sort of error occurred, throw a NEMarshalException.
   */
  public static void marshal (NEMessageable message, OutputStream outputStream)
      throws NEMarshalException {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream (outputStream);
      System.out.println ("Opened object output stream");
      out.writeObject (message);
      out.flush ();
    }
    catch (IOException e) {
      e.printStackTrace ();
      throw new NEMarshalException ("Message could not be marshalled");
    }
  } 
  
  /*
   * Marshal out the NEException message to the given output stream. If
   * some sort of error occurred, throw a NEMarshalException.
   */
  public static void marshalException (NEException message, OutputStream outputStream)
      throws NEMarshalException {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream (outputStream);
      System.out.println ("Opened object output stream");
      out.writeChar ('e');
      out.writeObject (message);
      out.flush ();
    }
    catch (IOException e) {
      e.printStackTrace ();
      throw new NEMarshalException ("Message could not be marshalled");
    }
  } 
  
  /*
   * Marshal out the NEReturnValue message to the given output stream. If
   * some sort of error occurred, throw a NEMarshalException.
   */
  public static void marshalReturnValue (NEReturnValue message, OutputStream outputStream)
      throws NEMarshalException {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream (outputStream);
      System.out.println ("Opened object output stream");
      out.writeChar ('r');
      out.writeObject (message);
      out.flush ();
    }
    catch (IOException e) {
      e.printStackTrace ();
      throw new NEMarshalException ("Message could not be marshalled");
    }
  }
  
  /*
   * Marshal out the NEMethodInvocation message to the given output stream. If
   * some sort of error occurred, throw a NEMarshalException.
   */
  public static void marshalMethodInvocation (NEMethodInvocation message, OutputStream outputStream)
      throws NEMarshalException {
    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream (outputStream);
      System.out.println ("Opened object output stream");
      out.writeObject (message);
      out.flush ();
    }
    catch (IOException e) {
      e.printStackTrace ();
      throw new NEMarshalException ("Message could not be marshalled");
    }
  }    
}
