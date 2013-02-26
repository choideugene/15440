import java.net.Socket;

public class NERemoteObjectMethodRequest {
  private NEMethodCall method;
  private Socket client;
  private NEException exception;
  
  public NERemoteObjectMethodRequest (NEMethodCall method, Socket client) {
    this.method = method;
    this.client = client;
    this.exception = null;
  }
  
  public NERemoteObjectMethodRequest (NEMethodCall method, Socket client, 
      NEException exception) {
    this.method = method;
    this.client = client;
    this.exception = exception;
  }    
  
  public NEMethodCall getMethodCall () {
    return method;
  }
  
  public Socket getClient () {
    return client;
  }
  
  public NEException getException () {
    return exception;
  }
}
