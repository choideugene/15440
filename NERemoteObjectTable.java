import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.concurrent.*;

public class NERemoteObjectTable {
  private ConcurrentHashMap<Integer,NERemote> table;
  private int nextKey;
  
  public NERemoteObjectTable () {
    nextKey = 0;
    table = new ConcurrentHashMap<Integer,NERemote> ();
  }
  
  public synchronized void add (NERemote object) {
    table.put (nextKey, object);
    nextKey++;
  }
  
  public synchronized void remove (int key) {
    table.remove (key);
  }
  
  public synchronized NERemote get (int key) {
    return table.get (key);
  }
}
