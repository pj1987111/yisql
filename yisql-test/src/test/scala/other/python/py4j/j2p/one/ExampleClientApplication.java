package other.python.py4j.j2p.one;

import py4j.GatewayServer;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 14:34
 *  \* Description: 
 * python启动server，java调用
 *  \
 */
public class ExampleClientApplication {
  public static void main(String[] args) {
    GatewayServer.turnLoggingOff();
    GatewayServer server = new GatewayServer();
    server.start();
    IHello hello = (IHello) server.getPythonServerEntryPoint(new Class[] { IHello.class });
    try {
      System.out.println(hello.sayHello());
      System.out.println(hello.sayHello(2, "Hello World"));
      System.out.println(hello.sayHello(3, "Hello World3"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    server.shutdown();
  }
}
