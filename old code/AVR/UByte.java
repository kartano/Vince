//=============================================
// UByte Class
//
// Unsigned Byte class
//
// AUTHOR:  Simon Mitchell
//   DATE:  04/07/2003
// VERSION: 1.0.0
//
// A pseudo unsigned byte class that works using
// ints.  Principally because Java doesn't have
// a native type for unsigned byte, like C/C++.
//
// MODIFICATION HISTORY:
//=============================================

package AVR;

import java.io.*;

public class UByte extends java.lang.Number
{
    private int mValue = 0;

    // Constructor - no arguments
    public UByte() { mValue = 0; }

    // Constructor - byte value
    public UByte(byte value) 
    { 
	mValue = Utils.unsigned_byte(value);
    }

    // Constructor - int value
    public UByte(int value) { mValue = value & 0xff; }

    // Inherited from Number
    public double doubleValue() { return (double)mValue; }

    // Inherited from Number
    public float floatValue() { return (float)mValue; }

    // Inherited from Number
    public int intValue() { return Math.abs(mValue); }

    // Inherited from Number
    public  long longValue() {return (long)mValue; } 

    // Inherited from Number
    public short shortValue() {return (short)mValue; }

	public void setValue(int theValue)
	{
		mValue = (Math.abs(theValue)) & 0xFF;
	}

	public String toHexString()
	{
		return Integer.toHexString(mValue);
	}
}
