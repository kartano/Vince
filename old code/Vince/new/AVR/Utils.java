//=============================================
// Utils Class
//
//   DATE:  12/10/2003
// VERSION: 1.0.8
//
// MODIFICATION HISTORY:
// 23/05/2004     -     Fixed bug with getExtension where it was returning
//                      nulls for filenames with extensions.
// 28/10/2003     -     Indentation fixes
// 25/10/2003     -     Added hex with optional output width
// ** NOTE:  As of the 25/10/2003, now using CVS for version numbers
// 12/10/2003 - 1.0.8 - Added "GetOperand" functions - moved from Atmel
// 27/07/2003 - 1.0.7 - Added "hex" method
// 08/07/2003 - 1.0.6 - Fixed bug in setbit method
// 03/07/2003 - 1.0.5 - Removed use of Math.pow
// 02/07/2003 - 1.0.4 - Added unsigned_byte method
// 30/06/2003 - 1.0.3 - General changes
// 26/06/2003 - 1.0.2 - Bug fixes in dump methods
// 25/06/2003 - 1.0.1 - Added some new methods
//=============================================

package AVR;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.*;

/**
 * This is a general utilities class for use with Vince
 * @author Simon Mitchell
 * @version 1.0.8
 */
public final class Utils
{
      // Private Constructor
      private void Utils() {}

      /**
      * Return the low byte of a 16 bit number
      * @param RHS Number to use when calculating low byte
      * @return Low byte
      */
      public static int get_lobyte(int RHS) { return (byte)(RHS & 0x00FF); }

      // Return high byte of a 16 bit number
      /**
      * Return the high byte of a 16 bit number
      * @param RHS Number to use when calculating high byte
      * @return High byte
      */
      public static int get_hibyte(int RHS) { return (byte)((RHS & 0xFF00) >> 8); }

      /**
      * Test the state of bit 0 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit0(byte value) { return (value & 0x01) > 0; }
    
      /**
      * Test the state of bit 1 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit1(byte value) { return (value & 0x02) > 0; }

      /**
      * Test the state of bit 2 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit2(byte value) { return (value & 0x04) > 0; }
    
      /**
      * Test the state of bit 3 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit3(byte value) { return (value & 0x08) > 0; }

      /**
      * Test the state of bit 4 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit4(byte value) { return (value & 0x10) > 0; }

      /**
      * Test the state of bit 5 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit5(byte value) { return (value & 0x20) > 0; }

      /**
      * Test the state of bit 6 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit6(byte value) { return (value & 0x40) > 0; }

      /**
      * Test the state of bit 7 of a byte
      * @param value Byte value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit7(byte value) { return (value & 0x80) > 0; }

      /**
      * Test the state of bit 0 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit0(int value) { return (value & 0x0001) > 0; }

      /**
      * Test the state of bit 1 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit1(int value) { return (value & 0x0002) > 0; }
    
      /**
      * Test the state of bit 2 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit2(int value) { return (value & 0x0004) > 0; }
    
      /**
      * Test the state of bit 3 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit3(int value) { return (value & 0x0008) > 0; }
      
      /**
      * Test the state of bit 4 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit4(int value) { return (value & 0x0010) > 0; }

      /**
      * Test the state of bit 5 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit5(int value) { return (value & 0x0020) > 0; }

      /**
      * Test the state of bit 6 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit6(int value) { return (value & 0x0040) > 0; }

      /**
      * Test the state of bit 7 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit7(int value) { return (value & 0x0080) > 0; }

      /**
      * Test the state of bit 8 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */

      public static boolean bit8(int value) { return (value & 0x0100) > 0; }

      /**
      * Test the state of bit 9 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit9(int value) { return (value & 0x0200) > 0; }

      /**
      * Test the state of bit 10 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit10(int value) { return (value & 0x0400) > 0; }

      /**
      * Test the state of bit 11 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit11(int value) { return (value & 0x0800) > 0; }

      /**
      * Test the state of bit 12 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit12(int value) { return (value & 0x1000) > 0; }

      /**
      * Test the state of bit 13 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit13(int value) { return (value & 0x2000) > 0; }

      /**
      * Test the state of bit 14 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit14(int value) { return (value & 0x4000) > 0; }

      /**
      * Test the state of bit 15 of a Word
      * @param value Int value to test
      * @return TRUE if bit is set, FALSE if not
      */
      public static boolean bit15(int value) { return (value & 0x8000) > 0; }

      /**
      * Dump the contents of an array of UBytes to an ascii file
      * @param theData An array of UBytes
      * @param filename The name of the file to dump the data into
      * @param MAX_SIZE The amount of data from the array to dump
      */
      public static void dump(UByte theData[], String filename, int MAX_SIZE)
      {
            FileOutputStream os;
            int row, column, numOfRows, memLocation;
            String buffer = new String();
            try
            {
                  os = new FileOutputStream(new File(filename));
                  numOfRows = MAX_SIZE / 8;
                  memLocation = 0;
                  // Print column headings
                  for(column=0;column<=7;column++)
                  {
                        buffer = "\t+" + column;
                        os.write(buffer.getBytes());
                  }
                  buffer = "\n";
                  os.write(buffer.getBytes());

                  for(row=0;row<numOfRows;row++)
                  {
                        buffer = Integer.toHexString(memLocation).toUpperCase() + "\t";
                        os.write(buffer.getBytes());

                        for(column=0;column<=7;column++)
                        {
                              if(memLocation < MAX_SIZE)
                              {
                                    buffer = theData[memLocation++].toHexString().toUpperCase() + "\t";
                                    os.write(buffer.getBytes());
                              }
                        }
                        buffer = "\n";
                        os.write(buffer.getBytes());
                  }
                  buffer = "\n";
                  os.write(buffer.getBytes());
                  os.close();
            }
            catch (IOException e)
            {
                  System.err.println("Exception while dumping array: " + e.toString());
                  System.exit(-1);
            }
      }

      /**
      * Dump the contents of an array of ints to an ascii file
      * @param theData An array of ints to be dumped to the fle
      * @param filename The name of the file to dump the data into
      * @param MAX_SIZE The amount of data from the array to dump
      */
      public static void dump(int theData[], String filename, int MAX_SIZE)
      {
            FileOutputStream os;
            int row, column, numOfRows, memLocation;
            String buffer = new String();
            try
            {
                  os = new FileOutputStream(new File(filename));
                  numOfRows = MAX_SIZE / 8;
                  memLocation = 0;
                  // Print column headings
                  for(column=0;column<=7;column++)
                  {
                        buffer = "\t+" + column;
                        os.write(buffer.getBytes());
                  }
                  buffer = "\n";
                  os.write(buffer.getBytes());

                  for(row=0;row<numOfRows;row++)
                  {
                        buffer = Integer.toHexString(memLocation).toUpperCase() + "\t";
                        os.write(buffer.getBytes());

                        for(column=0;column<=7;column++)
                        {
                              if(memLocation < MAX_SIZE)
                              {
                                    buffer = Integer.toHexString(theData[memLocation++]).toUpperCase() + "\t";
                                    os.write(buffer.getBytes());
                              }
                        }
                        buffer = "\n";
                        os.write(buffer.getBytes());
                  }
                  buffer = "\n";
                  os.write(buffer.getBytes());
                  os.close();
            }
            catch (IOException e)
            {
                  System.err.println("Exception while dumping array: " + e.toString());
                  System.exit(-1);
            }
      }

      /**
      * Toggle a given bit in a Word
      * @param bitno The bit number to toggle - must be from 0 to 7
      * @param turniton TRUE to turn the bit on, FALSE to turn it off
      * @param RHS The value to set the bit in
      * @return RHS with the appropriate bit number set or cleared
      */
      public static int setbit(int bitno, boolean turniton, int RHS)
      {
            // TO DO:  Range checking?  Bit no must be from 0 to 7
            byte BitValue = (byte)(1 << bitno);
            if (turniton)
                  return (RHS | BitValue);
            else
                  return (RHS & (255-BitValue));
      }

      /**
      * Generates a unique filename, using a base (or starting point) filename
      * Note that this method does NOT actually create the file!!!
      * Therefore, you can't be 100% certain that the file doesn't exist
      * when you create it!
      * @param basefilename The base filename to use
      * @return Unique filename to use
      */
      public static String generateUniqueFilename(String basefilename)
      {
            boolean unique;
            int suffix = 1;
            File currentFile = new File(basefilename);
 
            unique = !(currentFile.exists());
            while (!unique)
            {
                  currentFile = new File(basefilename + suffix);
                  unique = !(currentFile.exists());
                  suffix++;
            }
            return currentFile.toString();
      }

      /**
      * Returns the unsigned value of a byte in the form of an int
      * @param theByte The byte value to convert
      * @return The unsigned byte equivalent in the form of an int
      */
      public static int unsigned_byte(byte theByte)
      {
            if (Utils.bit7(theByte))
                  return (theByte & 0x7f) + 0x80;
            else
                  return theByte;
      }

      /**
      * Returns the extension portion of a filename
      * @param The file whose extension you need
      * @return String value representing the file extension.
      */  
      public static String getExtension(File f) 
      {
            String ext = new String("");
            String s = f.getName();
            int i = s.lastIndexOf(".");
            if (i > 0 &&  i < s.length() - 1) {
                  ext = s.substring(i+1).toLowerCase();
            }
            return ext;
      }
      
      /**
      * Returns an int value as a hex string.  Here for convenience, basically.
      * @param value Value to convert
      * @return Hex value as a string
      */
      public static String hex(int value) { return Integer.toHexString(value).toUpperCase(); }

      /**
      * Returns an int value as a hex string, with an optional length.
      * Length can be used to specify an exact hex string length (I.E: 4 chars)
      * @param Length Length in chars of the string to return
      */
      public static String hex(int value, int length)
      {
            String theValue = Integer.toHexString(value).toUpperCase();
            if (theValue.length() > length)
                  return theValue.substring(theValue.length() - length,theValue.length());
            else if (theValue.length() < length)
            {
                  String buffer = new String();
                  for (int i=0;i < (length-theValue.length()); i++)
                        buffer += "0";
                  return buffer + theValue;
            }
            else
                  return theValue;
      }

      public static int GetOperand_XXXXXXX11111XXXX(int MBR) {  return(MBR & 0x01f0) >> 4; }
      public static int GetOperand_XXXXXX1XXXXX1111(int MBR) 
         { 
           return(MBR & 0x000F) + ((MBR & 0x020f) >> 5); 
      }

      public static int GetOperand_XX1X11XXXXXXX111(int MBR)
      {
            int returnValue = 0;
            returnValue = MBR & 0x0007;
            returnValue += ((MBR & 0x0C00) >> 10);
            returnValue += ((MBR & 0x2000) >> 13);
            return returnValue;
      }

      public static int GetOperand_XXXXXXXX1111XXXX(int MBR) { return (((MBR & 0x00F0) >> 4) + 0x10); }
    
      public static int GetOperand_XXXXXXXXXX11XXXX(int MBR) { return (MBR & 0x0030) + 24; }

      public static int GetOperand_XXXX1111XXXX1111(int MBR) { return (((MBR & 0x0F00) >> 4) + (MBR & 0x000F)); }

      public static int GetOperand_XXXXXXXX11XX1111(int MBR) { return ((MBR & 0x00C0) >> 2) + (MBR & 0x000F); }
    
      public static int GetOperand_XXXXXXXXX111XXXX(int MBR) { return (MBR & 0x0070) >> 4; }

      public static byte GetOperand_XXXXXXXXXXXXX111(int MBR) { return (byte)(MBR & 0x007); }

      public static int GetOperand_XXXXXX1111111XXX(int MBR) { return (MBR & 0x03f8) >> 3; }

      public static int GetOperand_XXXXXXX111111111(int MBR) { return (MBR & 0x03ff); }

      public static int GetOperand_XXXXXXXXXXXX1111(int MBR) { return (MBR & 0x000f); }
      
      public static int GetOperand_XXXX111111111111(int MBR) { return (MBR & 0X0fff); }

      public static int GetOperand_XXXXXX1111111111(int MBR) { return (MBR & 0x03ff); }

      public static int GetOperand_XXXXXXXX11111XXX(int MBR) { return ((MBR & 0x00f8) >> 3); }
    
      public static int GetOperand_XXXXXXX11111XXX1(int MBR) { return (MBR & 0x01f1 >> 3) + (MBR & 0x0001); }

      public static int GetOperand_XXXXX11XXXXX1111(int MBR) { return ((MBR & 0x0600) >> 5) + (MBR & 0x000f); }

      // Returns a value to add to the program counter
      // if any branch condition is true
      public static int branch_offset(int kvalue) { return ((kvalue ^ 0x40) - 0x40); }

      //  Returns a value to add to the program counter
      // for instructions that use 12 bit offsets (I.E: RJMP)
      public static int branch_offset_jmp(int kvalue) { return ((kvalue ^ 0x0800) - 0x0800); }

      /**
      * This method returns the names of all classes supported by
      * the specified Package.  This rather lovely bit of code is
      * taken from here:
      * http://www.javaworld.com/javaworld/javatips/jw-javatip113.html
      * The only change to the original is that this one dumps
      * class names into a Vector, instead of to stdout.
      * @param pckgname The Package to enumerate
      * @return A vector of String objects with all supported classes.
      */
      public static Vector supportedClasses(String pckgname)
      {
            Vector classList = new Vector();
            // Code from JWhich
            // ======
            // Translate the package name into an absolute path
            String name = new String(pckgname);
            if (!name.startsWith("/"))
                  name = "/" + name;
            name = name.replace('.','/');
        
            // Get a File object for the package
            URL url = Utils.class.getResource(name);
            File directory = new File(url.getFile());

            // New code
            // ======
            if (directory.exists()) 
            {
                  // Get the list of the files contained in the package
                  String [] files = directory.list();
                  for (int i=0;i<files.length;i++) 
                  {
                        // we are only interested in .class files
                        if (files[i].endsWith(".class")) 
                        {
                              // removes the .class extension
                              String classname = files[i].substring(0,files[i].length()-6);
                              try 
                              {
                                    // Try to create an instance of the object
                                    Object o = Class.forName(pckgname+"."+classname).newInstance();
                                    classList.add(classname);
                              }
                              catch (ClassNotFoundException cnfex) 
                              {
                                    // Something really odd happened here.
                                    System.err.println(cnfex);
                              }
                              catch (InstantiationException iex)
                              {
                                    // We try to instantiate an interface
                                    // or an object that does not have a 
                                    // default constructor
                              }
                              catch (IllegalAccessException iaex) 
                              {
                                    // The class is not public
                              }
                        }
                  }
            }
            return classList;
      }
}

