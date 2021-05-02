package other.python.py4j.p2j.one;

import py4j.GatewayServer;

import java.util.ArrayList;
import java.util.List;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 13:35
 *  \* Description: 
 * python 调用java 2
 * 从python端调用
 * 提供接口让python实现
 *  \
 */
public class ListenerApplication {

  List<ExampleListener> listeners = new ArrayList<ExampleListener>();

  public void registerListener(ExampleListener listener) {
    listeners.add(listener);
  }

  public void notifyAllListeners() {
    for (ExampleListener listener: listeners) {
      Object returnValue = listener.notify(this);
      System.out.println(returnValue);
    }
  }

  @Override
  public String toString() {
    return "<ListenerApplication> instance";
  }

  public static void main(String[] args) {
    ListenerApplication application = new ListenerApplication();
    GatewayServer server = new GatewayServer(application);
//    GatewayServer server = new GatewayServer(application, 0);
    server.start(true);
  }
}
