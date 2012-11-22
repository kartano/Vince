package AVR;

//=============================================
// AtmelFactory
//
// MODIFICATION HISTORY:
// 28/10/2003     -     Indentation fixes
// NOTE:  As of 28/10/2003 this uses CVS for version numbers
// 10/10/2003     -     1.0.0       Prototype
//=============================================

/**
 * The Atmel AVR factory
 * @author Simon Mitchell
 * @version 1.0.0
 */

public final class AtmelFactory
{
      // Private constructor
      private void AtmelFactory() { }
      
      /*
      * Creates a new instance of an Atmel class
      * @param NewAVRName String value representing the new AVR name
      */
      public static Atmel CreateAtmel(String NewAVRName)
      {
            Atmel newAVR;
            try
            {
                  newAVR = (Atmel)Class.forName("AVR."+NewAVRName).newInstance();
            }
            catch (Exception e)
            {
                  System.out.println("Exception creating " + NewAVRName + "\n" + e.toString());
                  newAVR = null;
            }
            return newAVR;                  
      }
}
