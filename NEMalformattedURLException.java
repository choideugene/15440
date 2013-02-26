/*
 * Class      : NEMalformattedURLException
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: Exception caused by malformatted RMI url
 */

public class NEMalformattedURLException extends RuntimeException {
  public NEMalformattedURLException () {
    super ();
  }
  
  public NEMalformattedURLException (String message) {
    super (message);
  }
}
