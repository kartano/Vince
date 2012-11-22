package Utils;

/**
 * <p>Title: Queue Node</p>
 * <p>Description: Node object for implementaion of delay queue</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class QueueNode
{
  private Object element;
  private QueueNode prevNode;
  private QueueNode nextNode;

  public QueueNode(Object e)
  {
    prevNode = null;
    nextNode = null;
    element = e;
  }

  public void setElement(Object e)
  {
    element = e;
  }

  public void setPrev(QueueNode n)
  {
    prevNode = n;
  }

  public void setNext(QueueNode n)
  {
    nextNode = n;
  }

  public Object getElement()
  {
    return element;
  }

  public Object getPrev()
  {
    return prevNode;
  }

  public Object getNext()
  {
    return nextNode;
  }
}