//=============================================
// RuntimeException Class
//
// General exception class definitions
//
// AUTHOR:  Simon Mitchell
//   DATE:  09/05/2003
//
// Any custom exceptions go in here
//
// MODIFICATION HISTORY:
//=============================================

package AVR;

import java.lang.Exception;

public class RuntimeException extends Exception
{
	public RuntimeException(String errMsg)
	{
	    super(errMsg);
	}
}
