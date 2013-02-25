public class NERegistryNotFoundException extends Exception {
  private String message;
  
  public NERegistryNotFoundException (String address, int port) {
    message = "Registry not found at " + address + ":" + port;
  }
  
  public String getLocalizedMessage () {
    return message;
  }
}
