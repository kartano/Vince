package gui;

//==============================================================
// HexTableModel
//
// MODIFICATION HISTORY:
// 31/10/2003     -     Added support for styles
//==============================================================

import AVR.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;

/*
 * Hex Memory Model
 * This class is used to display the current contents of an Atmel based micro.
 * It's based on the original by Craig Eales.
 * @author Simon Mitchell
 */
public class HexTableModel extends AbstractTableModel implements Observer
{
      private Atmel mAtmelParent;
      private int mWhatIObserve;
      private int mStyle;
      
      // Constants for what this model should observe
      public static final int OBSERVE_PROGRAM_MEMORY = 1;
      public static final int OBSERVE_DATA_MEMORY = 2;

      // Other things to look for
      // These are all special cases.
      public static final int PROGRAM_COUNTER = 3;
      
      // Style constants - determines how the display should be shown
      public static final int STYLE_HEX = 1;
      public static final int STYLE_CODE = 2;
      
      // The number of locations to display per row on the table.
      public static final int VALUES_PER_ROW = 8;

      /*
      * Constructor for Memory Table Model.
      * @param theParent The "parent" Atmel class the table model observes
      * @param whichToObserver Which to observer - Program, Data or Stack.  See field values.
      */
      public HexTableModel(Atmel theParent, int whichToObserve, int theStyle)
      {
            mAtmelParent = theParent;
            mWhatIObserve = whichToObserve;
            mStyle = theStyle;
            if (mAtmelParent != null) mAtmelParent.addObserver(this);
      }

      public int getStyle() { return mStyle; }
      
      public void setStyle(int newStyle)
      {
            if (mStyle != newStyle)
            {
                  mStyle = newStyle;
                  fireTableStructureChanged();
                  fireTableDataChanged();
            }
      }
      
      public int getColumnCount() 
      {
            switch (mStyle)
            {
                  case STYLE_CODE :
                        return 4;
                  default :
                        return VALUES_PER_ROW + 1;
            }
      }
      
      public void setParent(Atmel newParent)
      {
            mAtmelParent = newParent;
            if (mAtmelParent != null) 
            {
                  mAtmelParent.addObserver(this);
                  fireTableDataChanged();
            }
      }
      public Atmel getParent() { return mAtmelParent; }

      public int getRowCount() 
      {
            if (mAtmelParent == null)
                  return 0;
            else
            {
                  switch (mStyle)
                  {
                        case STYLE_CODE :
                              return getRowCount_StyleCode();
                        default :
                              return getRowCount_StyleHex();
                  }
            }
      }

      public String getColumnName(int columnIndex)
      {
            switch (mStyle)
            {
                  case STYLE_CODE:
                        return getColumnName_StyleCode(columnIndex);
                  default :
                        return getColumnName_StyleHex(columnIndex);
            }
      }

      public Object getValueAt(int row, int col)
      {
            if (mAtmelParent == null)
                  return "";
            else
            {
                  switch (mStyle)
                  {
                        case STYLE_CODE :
                              return codeAtLocation(row,col);
                        default :
                              return valueAtLocation(row, col);
                  }
            }
      }

      /*
      * Update method for use by Observer model
      * @param theObservable The observable object
      * @param argument Expected to be of type "MemoryChangeDetails"
      */
      public void update(Observable theObservable, Object argument)
      {
            MemoryChangeDetails theDetails = (MemoryChangeDetails)argument;
            int therow = theDetails.getWhichAddress() / VALUES_PER_ROW;
            int thecol = theDetails.getWhichAddress() % VALUES_PER_ROW;
            
            if ((mAtmelParent != null) && (theDetails.getWhichMemory() == mWhatIObserve))
                  fireTableCellUpdated(therow, thecol);
      }
      
      /*
      * JTable uses this method to determine the default renderer/
      * editor for each cell.
      */
      public Class getColumnClass(int c) { return Color.class; }

      //--------------------------------------------------------
      // All methods under here and just utility functions for
      // the main ones above.  Just makes code a little easier to read      
      //--------------------------------------------------------
      private int getRowCount_StyleCode() 
      {
            int returnValue;
            switch (mWhatIObserve)
            {
                  case OBSERVE_PROGRAM_MEMORY :
                        returnValue = mAtmelParent.ProgramMemorySize(); 
                        break;
                  case OBSERVE_DATA_MEMORY :
                        // We divide this by 2 because data memory locations
                        // are all 8-bits - we need a minimum of 2 locations
                        // to display any code
                        returnValue = mAtmelParent.DataMemorySize() / 2; 
                        break;
                  default :
                        returnValue = 0;
                        break;
            }
            return returnValue;
      }

      // Returns the row count for a hex dump
      private int getRowCount_StyleHex()
      {
            int returnValue;
            if (mAtmelParent != null)
            {
                  switch (mWhatIObserve)
                  {
                        case OBSERVE_PROGRAM_MEMORY:
                              returnValue = mAtmelParent.ProgramMemorySize() / VALUES_PER_ROW;
                              break;
                        case OBSERVE_DATA_MEMORY:
                              returnValue = (mAtmelParent.DataMemorySize() / VALUES_PER_ROW);
                              break;
                        default :
                              returnValue = 0;
                              break;
                  }
            }
            else
                  returnValue = 0;
            return returnValue;
      }

      private String codeAtLocation(int row, int col)
      {
            int memoryContents, actualMemoryAddress;
            int MBR, MBR2;
            switch (mWhatIObserve)
            {
                  case OBSERVE_PROGRAM_MEMORY :
                        actualMemoryAddress = row;
                        MBR = mAtmelParent.getProgramMemory(actualMemoryAddress);
                        MBR2 = mAtmelParent.getProgramMemory(actualMemoryAddress)+1;
                        break;
                  case OBSERVE_DATA_MEMORY :
                        actualMemoryAddress = row * 2;
                        MBR = mAtmelParent.getDataMemory(actualMemoryAddress);
                        MBR2 = 0;
                        break;
                  default :
                        actualMemoryAddress = row;
                        MBR = MBR2 = 0;
                        break;
            }
            switch (col)
            {
                  case 0 :
                        return Utils.hex(actualMemoryAddress,4).toUpperCase();
                  case 1 :
                        return Utils.hex(Utils.get_lobyte(MBR),2).toUpperCase();
                  case 2 :
                        return Utils.hex(Utils.get_hibyte(MBR),2).toUpperCase();
                  case 3 :
                        // Add one to the actual instruction address, because the PC in the
                        // disassembler code actually expects the program counter to be
                        // post-incremented (I.E: Following a FETCH).
                        return InstructionDecoder.DisassembleInstruction(MBR, MBR2, actualMemoryAddress + 1);
                  default :
                        return "";
            }
      }

      // Returns hex string of a specific memory location, based on
      // a table row and column.  Only used if mStyle is STYLE_HEX
      private String valueAtLocation(int row, int col)
      {
            if (col ==0)
                  return Utils.hex(row * VALUES_PER_ROW,4).toUpperCase();
            else
            {
                  int actualMemoryLocation = (row * VALUES_PER_ROW) + (col-1);
                  switch (mWhatIObserve)
                  {
                        case OBSERVE_PROGRAM_MEMORY:
                              // All ones indicates an unprogramed memory location
                              if (mAtmelParent.getProgramMemory(actualMemoryLocation) == 0xFFFF)
                                    return "-";
                              else
                                    return Utils.hex(mAtmelParent.getProgramMemory(actualMemoryLocation),4);
                              case OBSERVE_DATA_MEMORY:
                                    return Utils.hex(mAtmelParent.getDataMemory(actualMemoryLocation),2);
                              default :
                                    return "";
                  }
            }
      }                       

      private String getColumnName_StyleCode(int columnIndex)
      {
            switch(columnIndex)
            {
                  case 0 :
                        return "Address";
                  case 1 :
                        return "First byte";
                  case 2 :
                        return "Second byte";
                  case 3 :
                        return "Instruction";
                  default :
                        return "";
            }
      }      

      private String getColumnName_StyleHex(int columnIndex)
      {
            switch(columnIndex)
            {
                  case 0x00:
                        return "";
                  default:
                        return "+" + Utils.hex(columnIndex-1,2);
            }
      }
}
