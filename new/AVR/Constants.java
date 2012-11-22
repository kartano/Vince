//=============================================
// Constants Class
//
// Class used for general constants and enumerators
//
// AUTHOR:  Simon Mitchell
//   DATE:  28/06/2003
// VERSION: 1.0.2
//
// MODIFICATION HISTORY:
// 28/10/2003     -     Indentation fixes
// NOTE:  As of 28/10/2003 this now uses CVS for version numbers
// 1.0.2 - 28/06/2003 - Added stack pointer exceptions
// 1.0.1 - Unknown    -  Added BREAK_MODE
//=============================================

package AVR;

public final class Constants
{
      //------------------------------------------------
      // Private Constructor
      //------------------------------------------------

      private Constants() {};

      public static final int INVALID_INSTRUCTION = 1;
      public static final int UPPER_MEMORY_LIMIT = 2;
      public static final int LOAD_OPERATIONS_FAILED = 3;  // DEPRECATED
      public static final int INVALID_STACK_POINTER_LO = 4;
      public static final int INVALID_STACK_POINTER_HI = 5;
      public static final int FILE_NOT_FOUND = 6;
      public static final int OUT_OF_MEMORY = 7;
      public static final int INVALID_HEXFILE = 8;
      public static final int INVALID_HARDWARE_STACK_POINTER = 9;
      public static final int INVALID_GENERAL_REGISTER = 10;
      public static final int INVALID_IO_REGISTER = 11;

      public static final int BREAK_MODE = 100;

      /**
      * Returns the error message for a given error number
      * @param errorNumber The error number constant
      * @return An error message as a String
      */
      public static String Error(int errorNumber)
      {
            String returnValue;

            switch (errorNumber)
            {
                  case INVALID_INSTRUCTION :
                        returnValue = "Invalid Instruction";
                        break;
                  case UPPER_MEMORY_LIMIT :
                        returnValue = "Program Counter exceeds upper program memory limit";
                        break;
                  case LOAD_OPERATIONS_FAILED :
                        // This error is deprecated as of Atmel 1.1.0
                        returnValue = "Failed to load operations";
                        break;
                  case BREAK_MODE :
                        returnValue = "Break mode encountered";
                        break;
                  case INVALID_STACK_POINTER_LO :
                        returnValue = "Invalid Stack Pointer - LO";
                        break;
                  case INVALID_STACK_POINTER_HI :
                        returnValue = "Invalid Stack Pointer - HI";
                        break;
                  case FILE_NOT_FOUND :
                        returnValue = "File not found";
                        break;
                  case OUT_OF_MEMORY :
                        returnValue = "Out of memory";
                        break;
                  case INVALID_HEXFILE :
                        returnValue = "Input file not Intel hex format";
                        break;
                  case INVALID_HARDWARE_STACK_POINTER :
                        returnValue = "Invalid Hardware Stack Pointer";
                        break;
                  case INVALID_GENERAL_REGISTER :
                        returnValue = "Invalid General Register Number";
                        break;
                  case INVALID_IO_REGISTER :
                        returnValue = "Invalid IO Register Number";
                        break;
                  default :
                        returnValue = "Unknown Error Code";
            }
            return "[" + errorNumber + "] " + returnValue;
      }
}
