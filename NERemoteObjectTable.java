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
  private HashMap<Integer,NERemoteObjectServerThread> threads;
  private int nextKey;
  
  /*
   * Create a new hash map for use
   */
  public NERemoteObjectTable () {
    nextKey = 0;
    table = new HashMap<Integer,NERemote> ();
    threads = new HashMap<Integer,NERemoteObjectServerThread> ();
  }
  
  /*
   * Add a new remote object to the hash map
   */
  public synchronized int add (NERemote object, NERemoteObjectServerThread thread) {
    table.put (nextKey, object);
	  threads.put (nextKey, thread);
    nextKey++;
    return nextKey - 1;
  }
  
  /*
   * Remove the object pointed to by the key from the hash map
   */
  public synchronized void remove (int key) {
    table.remove (key);
	  NERemoteObjectServerThread oldThread = threads.remove(key);
  }
  
  /*
   * Replace the object pointed to by the key with the given remote object
   */
  public synchronized int replace (int key, NERemote object, NERemoteObjectServerThread thread) {
    table.remove (key);
    table.put (nextKey, object);
	  NERemoteObjectServerThread oldThread = threads.remove(key);
	  oldThread.running = false;
	  threads.put(nextKey, thread);
    nextKey++;
    return nextKey - 1;
  }  
  
  /*
   * Find the object in the hash map pointed to by the key
   */
  public synchronized NERemote getObj (int key) {
    return table.get (key);
  }
  
  public synchronized NERemote getThread(int key) {
	  return threads.get(key);
  }
  
  public String toString () {
    return table.toString () + "/n" + threads.toString();
  }
}
