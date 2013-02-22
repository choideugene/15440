import java.io.*;
import java.net.*;
import java.lang.reflect.*;

public class RMIMethodInvocation extends RMIMessage {

  public RMIMethodInvocation (Object returnValue) {
    object = returnValue;
  }
}
