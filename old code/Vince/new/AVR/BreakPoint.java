package AVR;

//=============================================
// BreakPoint - abstract class for all derived
// breakpoints.
//
// MODIFICATION HISTORY:
// 04/11/2003     -     Prototype
//=============================================

public abstract class BreakPoint
{
      protected int mLocation;
      protected AtmelThread mAtmelThread;
      
      public BreakPoint(AtmelThread myAtmelThread, int newLocation)
      {
            mAtmelThread = myAtmelThread;
            mLocation = newLocation;
      }

      public abstract boolean rulesSatisfied();
}
