/*
 * Class      : NERemoteObjectTable.java
 * Authors    : Eugene Choi, Norbert Chu
 * Andrew IDs : dechoi, nrchu
 * Description: The hashmap mapping object keys to remote objects themselves.
 *              This is primarily used by the server side of RMI.
 */

import java.util.HashMap;

public class NERemoteObjectTable {
  private HashMap<Integer,NERemote> table;
  private int nextKey;
  
  /*
   * Create a new hash map for use
   */
  public NERemoteObjectTable () {
    nextKey = 0;
    table = new HashMap<Integer,NERemote> ();
  }
  
  /*
   * Add a new remote object to the hash map
   */
  public synchronized int add (NERemote object) {
    table.put (nextKey, object);
    nextKey++;
    return nextKey - 1;
  }
  
  /*
   * Remove the object pointed to by the key from the hash map
   */
  public synchronized void remove (int key) {
    table.remove (key);
  }
  
  /*
   * Replace the object pointed to by the key with the given remote object
   */
  public synchronized int replace (int key, NERemote object) {
    table.remove (key);
    table.put (nextKey, object);
    nextKey++;
    return nextKey - 1;
  }  
  
  /*
   * Find the object in the hash map pointed to by the key
   */
  public synchronized NERemote get (int key) {
    return table.get (key);
  }
  
  public String toString () {
    return table.toString ();
  }
}
