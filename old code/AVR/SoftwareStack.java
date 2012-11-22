//============================================================
// Software Stack
//
// Version: 1.0.2
//
// MODIFICATION HISTORY:
// 24/07/2003 - 1.0.2 - Changed to use new Atmel get'n'set for data memory
// 21/07/2003 - 1.0.1 - Changed to use Atmels data memory accessor/mutexor
// 20/07/2003 - 1.0.0 - Prototype
//============================================================

package AVR;

/**
* This class implements a software stack.
* @author Simon Mitchell
* @version 1.0.2
*/
public class SoftwareStack implements StackInterface
{
      private AVR.Atmel mParent;

      /**
      * Constructor for a Software Stack type class
      */
      public void SoftwareStack() { }

      /**
      * Method to set the parent for this class
      * @param parent Parent Atmel class that owns this stack
      */
      public void set_parent(AVR.Atmel theParent) { mParent = theParent; }

      /**
      * Pops a current value off the stack and increments the stack pointer
      * The stack pointer is PRE-INCREMENTED
      * @return Value off the software stack
      */
      public int pop() throws AVR.RuntimeException
      {
	     inc_sp();
	     return mParent.getDataMemory(get_sp());
      }

      /**
      * Pushes a value onto the stack and decrements the stack pointer
      * The stack pointer is POST-DECREMENTED
      * @param value Value to push onto the stack
      */
      public void push(int value) throws AVR.RuntimeException
      {
		mParent.setDataMemory(get_sp(), value);
		dec_sp();
      }

      /**
      * Returns the current value of the stack pointer
      */
      public int get_sp() { return mParent.get_ioreg(0x3D) + (mParent.get_ioreg(0x3E) << 8); }

      /**
      * Set the current stack pointer
      * @param RHS Address to set stack pointer to
      */
	public void set_sp(int RHS) throws AVR.RuntimeException
	{
		if (RHS < (mParent.GeneralRegCount() + mParent.IORegCount()))
			throw new RuntimeException(Constants.Error(Constants.INVALID_STACK_POINTER_LO) + " " + RHS);
 		else if (RHS > mParent.DataMemorySize())
			throw new RuntimeException(Constants.Error(Constants.INVALID_STACK_POINTER_HI) + " " + RHS);
		else
		{
			try
			{
				mParent.set_ioreg(0x3D,Utils.get_lobyte(RHS));
				mParent.set_ioreg(0x3E,Utils.get_hibyte(RHS));
			}
			catch(InvalidRegisterException e) { }
		}
	}

	private synchronized void inc_sp() throws RuntimeException { set_sp(get_sp() + 1); }

	private synchronized void dec_sp() throws RuntimeException { set_sp(get_sp() - 1); }

      public int get_type() { return StackInterface.SOFTWARE; }
}
