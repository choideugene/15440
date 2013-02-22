import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class RMIException extends RMIMessage {
  public RMIException (Exception exception) {
    object = exception;
  }
}
