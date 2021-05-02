package other.python.py4j.p2j.stack;

import java.util.LinkedList;
import java.util.List;

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 11:10
 *  \* Description: 
 *  \
 */
public class Stack {
  private List<String> internalList = new LinkedList<String>();

  public void push(String element) {
    internalList.add(0, element);
  }

  public String pop() {
    return internalList.remove(0);
  }

  public List<String> getInternalList() {
    return internalList;
  }

  public void pushAll(List<String> elements) {
    for (String element : elements) {
      this.push(element);
    }
  }
}
