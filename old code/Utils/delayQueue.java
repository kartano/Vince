package Utils;

/**
 * <p>Title: Delay Queue</p>
 * <p>Description: Queue for maintaining delays in components</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class delayQueue
{
  private int head;
  private int tail;
  private int size;
  private int[] queue;

  // creates a new queue of size s, where s should be the number of clock
  //   periods delayed.
  // ie if the delay of the component is, say 700us
  //   and the clock period is 100us, then there is a delay
  //   of 7 clock periods, and thus the queue should be of size 7.
  //
  // Number n is the initial state of all items in the queue.
  //   The queue only provides support for primtive data types and their
  //   wrapper class.

  public delayQueue(int s, int n)
  {
    queue = new int[s];
    head = 0;
    size = s;
    for (int index = 0;index<queue.length;index++)
    {
      queue[index]=n;
    }
    tail = s - 1;
  }

  public void push(int value)
  {
    queue[head]=value;
    head--;
    if(head<0){head=size-1;}
  }

  public int pop()
  {
    int value = queue[tail];
    tail++;
    if(tail==size){tail=0;}
    return value;
  }
}