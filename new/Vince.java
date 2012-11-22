//=============================================
// Vince
//
// MODIFICATION HISTORY:
//
// 06/11/2003     -     Now uses event dispatching thread to bring up the GUI
// ** NOTE:  As of 06/11/2003, this now uses CVS for version numbers
// 16/08/2003     -     1.0.0       Prototype
//=============================================

import gui.*;

/*
 * Main Vince class
 * @author Simon Mitchell
 * @version 1.0.0
 */
 
 public class Vince
 {
      // The Vince application
      private static VinceApplication mVinceApplication;
      
      public static void main(String[] args)
      {
            System.out.println("Vince.main");
            javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                  public void run()
                  {             
                        mVinceApplication = new VinceApplication();
                  }
            });
      }
 }
