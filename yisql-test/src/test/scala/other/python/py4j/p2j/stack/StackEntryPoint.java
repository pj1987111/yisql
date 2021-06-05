package other.python.py4j.p2j.stack;

import py4j.GatewayServer;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 11:14
 *  \* Description: 
 * python 调用 java
 * 普通调用
 *  \
 */
public class StackEntryPoint {
  private Stack stack;

  public StackEntryPoint() {
    stack = new Stack();
    stack.push("Initial Item");
  }

  public Stack getStack() {
    return stack;
  }

  public static void main(String[] args) {
    GatewayServer gatewayServer = new GatewayServer(new StackEntryPoint());
    gatewayServer.start();
    System.out.println("Gateway Server Started");
  }
}
