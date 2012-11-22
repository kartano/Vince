//=============================================
// MemoryChangeDetails
//
// This class is used to send a "payload" to any Atmel observers.
// It lets observers know what memory address has changed, the new value,
// which memory change (Program, Data).
//
// The only place this gets used is in the "notifyObservers" method of the Atmel class.
//
// MODIFICAION HISTORY:
// 28/10/2003     -     Indentation fixes
//=============================================

package gui;

import AVR.*;
import java.util.*;

public class MemoryChangeDetails
{
      private int mWhichAddress;
      private int mWhichMemory;
      
      /*
      * Constructor
      * @param WhichAddress The address that changed
      * @param WhichMemory What actually changed
      */
      public MemoryChangeDetails(int WhichAddress, int WhichMemory)
      {
            mWhichAddress = WhichAddress;
            mWhichMemory = WhichMemory;
      }
      
      public int getWhichAddress() { return mWhichAddress; }
      public int getWhichMemory() { return mWhichMemory; }      
}
