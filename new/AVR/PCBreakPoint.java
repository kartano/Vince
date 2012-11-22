package AVR;

//=============================================
// PCBreakPoint
//
// This class is used when a break point is desired
// based on the program counter
//
// MODIFICATION HISTORY:
// 04/11/2003     -     Prototype
//=============================================

public class PCBreakPoint extends BreakPoint
{
      public PCBreakPoint(AtmelThread myAtmelThread, int newLocation)
      { 
            super(myAtmelThread, newLocation); 
      }
      
      public boolean rulesSatisfied() 
      { 
            return (mAtmelThread.getAtmel().PC() == mLocation); 
      }
}