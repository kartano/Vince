//=============================================
// Utils Class
//
//   DATE:  27/07/2003
// VERSION: 1.0.7
//
// MODIFICATION HISTORY:
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

/**
 * This is a general utilities class for use with Vince
 * @author Simon Mitchell
 * @version 1.0.6
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
	 * Returns an int value as a hex
	 * @param value Value to convert
	 * @return Hex value as a string
	 */
	 public static String hex(int value) { return Integer.toHexString(value); }
}

