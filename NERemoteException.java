import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class NERemoteException extends IOException {
  private String message;
  
  public NERemoteException (String s) {
    message = s;
  }
  
  public String getMessage () {
    return message;
  }
}
