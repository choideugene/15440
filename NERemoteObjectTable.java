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
  private HashMap<Integer,NERemoteServerThread> threads;
  private int nextKey;
  
  /*
   * Create a new hash map for use
   */
  public NERemoteObjectTable () {
    nextKey = 0;
    table = new HashMap<Integer,NERemote> ();
    threads = new HashMap<Integer,NERemoteServerThread> ();
  }
  
  /*
   * Add a new remote object to the hash map
   */
  public synchronized int add (NERemote object) {
    table.put (nextKey, object);
    NERemoteServerThread thread = new NERemoteServerThread();
    thread.start();
	  threads.put (nextKey, thread);
    nextKey++;
    return nextKey - 1;
  }
  
  /*
   * Remove the object pointed to by the key from the hash map
   */
  public synchronized void remove (int key) {
    table.remove (key);
	  NERemoteServerThread oldThread = threads.remove(key);
    NERemoteServerThread.running = false;
  }
  
  /*
   * Replace the object pointed to by the key with the given remote object
   */
  public synchronized int replace (int key, NERemote object) {
    table.remove (key);
    table.put (nextKey, object);
	  NERemoteServerThread oldThread = threads.remove(key);
	  oldThread.running = false;
    NERemoteServerThread thread = new NERemoteServerThread();
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
