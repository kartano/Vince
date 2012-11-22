//=============================================
// InvalidRegisterException Classe
//
// General exception class definitions
//
// AUTHOR:  Simon Mitchell
//   DATE:  22/04/2003
//
// Any custom exceptions go in here
//
// MODIFICATION HISTORY:
//=============================================

package AVR;

import java.lang.Exception;

public class InvalidRegisterException extends Exception
{
	public InvalidRegisterException(String errmsg)
	{
		super(errmsg);
	}
}
