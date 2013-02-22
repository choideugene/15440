import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NEException implements NEMessageable {
  Exception exception;
  
  /*
   * A new Exception message packet.
   */
  public NEException (Exception e) {
    exception = e;
  }
  
  public Exception getException () {
    return exception;
  }
}
