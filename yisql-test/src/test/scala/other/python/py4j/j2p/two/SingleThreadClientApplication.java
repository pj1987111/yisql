package other.python.py4j.j2p.two;

import other.python.py4j.j2p.one.IHello;
import py4j.ClientServer;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 15:13
 *  \* Description: 
 * 单线程 模式
 *  \
 */
public class SingleThreadClientApplication {
  public static void main(String[] args) {
    ClientServer clientServer = new ClientServer(null);
    // We get an entry point from the Python side
    IHello hello = (IHello) clientServer.getPythonServerEntryPoint(new Class[] { IHello.class });
    // Java calls Python without ever having been called from Python
    System.out.println(hello.sayHello());
    System.out.println(hello.sayHello(2, "Hello World"));
    clientServer.shutdown();
  }
}
