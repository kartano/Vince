//============================================================
// Stack Interface
//
// Version: 1.0.1
//
// MODIFICATION HISTORY:
// 26/10/2003	-	Removed "set_parent" method, fixed constructor
// NOTE:  As of 26/10/2003, using CVS for version numbers
// 20/07/2003 - 1.0.0 - Prototype added
// 24/07/2003 - 1.0.1 - Removed get/set stack pointer methods
//============================================================

package AVR;

/**
* The stack interface class is used for the Atmel class.  It gives
* a standard interface to use when coding either software or hardware
* stacks for various AVR MCU types.
* @author Simon Mitchell
* @author Paul Pearce
* @version 1.0.1
*/
public interface StackInterface
{
      public static final int SOFTWARE = 1;
      public static final int HARDWARE = 2;

      /**
      * Method for pushing data onto the stack
      */
      public void push(int value) throws AVR.RuntimeException;

      /**
      * Method for popping data off the stack
      */
      public int pop() throws AVR.RuntimeException;

      /**
      * Method to return the type for this stack
      */
      public int get_type();
}
