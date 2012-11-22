//============================================================
// Hardware Stack
//
// Version: 1.0.0
//
// MODIFICATION HISTORY:
// 28/10/2003     -     Indentation fixes
// 26/10/2003     -     New StackInterface
// NOTE:  As of 26/10/2003, using CVS for version numbers
// 20/07/2003 - 1.0.0 - Prototype
//============================================================

package AVR;

/**
* This class implements a hardware stack.
* @author Simon Mitchell
* @version 1.0.0
*/
public class HardwareStack implements StackInterface
{
      private AVR.Atmel mParent;
      private int[] hStack = new int[3];
      private int currentPos = 0;

      /**
      * Constructor for a Hardware Stack type class
      */
      public HardwareStack(Atmel parent) { mParent = parent; }

      /**
      * Pops a current value off the stack and increments the stack pointer
      * The stack pointer is PRE-INCREMENTED
      * @return Value off the hardware stack
      */
      public int pop() throws AVR.RuntimeException
      {
            if(currentPos <= 2 && currentPos >= 0)
                  return hStack[currentPos--];
            else if(currentPos < 0)
            {
                  //stack is empty
                  currentPos = 0;
                  return 0;
            }
            else
                  //stack pointer somehow left stack boundaries
                  throw new RuntimeException(Constants.Error(Constants.INVALID_HARDWARE_STACK_POINTER));
      }

      /**
      * Pushes a value onto the stack and decrements the stack pointer
      * The stack pointer is POST-DECREMENTED
      * @param value Value to push onto the stack
      */
      public void push(int value) throws AVR.RuntimeException
      {
            currentPos++;
            if(currentPos <= 2 && currentPos >= 0)
                  hStack[currentPos] = value;
            else if(currentPos > 2)
            {
                  //overwrite value in top of stack if stack is full
                  currentPos = 2;
                  hStack[currentPos] = value;
            }
            else
                  //stack pointer somehow left stack boundaries
                  throw new RuntimeException(Constants.Error(Constants.INVALID_HARDWARE_STACK_POINTER));
      }

      public int get_type() { return StackInterface.HARDWARE; }
}
