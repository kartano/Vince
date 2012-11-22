package AVR;

//=============================================
// Atmel thread
//
// MODIFICATION HISTORY:
// 11/11/2003     -     Added support for desk checker
// 07/11/2003     -     Fixed bug with setThreadState.
//                      Was setting thread state variable AFTER notifying
//                      observers.
// 04/11/2003     -     Modified to use Break Points
// 29/10/2003     -     Prototype
//=============================================

import java.util.*;
import gui.*;

/**
* AtmelThread class.
* <p>This class is designed to allow an Atmel object to execute code
* in it's own thread.</p>
* @author Simon Mitchell
*/
public class AtmelThread extends Observable implements Runnable
{
      protected Atmel mAtmel;
      
      protected int mThreadState;
      
      protected Thread myThread;
      
      protected VinceApplication mVinceApplication;
      
      protected Vector mBreakPoints;
      
      public static final int THREAD_STOP = 0;
      public static final int THREAD_RUN = 1;
      public static final int THREAD_STEP = 2;
      public static final int THREAD_PAUSE = 3;
            
      /**
      * Default constructor
      */
      public AtmelThread(VinceApplication sausage) 
      {
            System.out.println("AtmelThread.AtmelThread");
            // NOTE:  Nothing to do with Megadeth.  Breakpoints?
            // Push me towards break point?  Push .... um, forget it.
            mBreakPoints = new Vector();
            mVinceApplication = sausage;
            SetDefaults();
      }
      
      /**
      * Returns the Break Points collection
      * @return Vector containing break points
      */
      public Vector getBreakPoints() { return mBreakPoints; }
      
      /**
      * Remove all breakpoints - just a convenience method, really
      */
      public void clearBreakPoints() { mBreakPoints.removeAllElements(); }

      /**
      * Set a new breakpoint
      * @param newBreakPoint New Break Point object to watch
      */      
      public void setBreakPoints(BreakPoint newBreakPoint) { mBreakPoints.add(newBreakPoint); }
      
      private void SetDefaults()
      {
            mThreadState = THREAD_STOP;
            myThread = new Thread(this);
      }
      
      /**
      * Set the atmel object that this thread should manage.
      * Calling this function will also stop the thread from executing.
      * @param theAtmelParent The Atmel object to manage
      */
      public void setAtmel(Atmel theAtmelParent) 
      {
            // Stop the thread executing first
            this.setThreadState(THREAD_STOP);
            mAtmel = theAtmelParent; 
      }
      /**
      * Returns the Atmel object this is currently managing.
      * @return Atmel The Atmel object this is managing.
      */
      public Atmel getAtmel() { return mAtmel; }
      
      public synchronized int getThreadState() { return mThreadState; }      
      
      /**
      * Sets the current thread execution state.
      * @param newState The new thread state.
      */
      public synchronized void setThreadState(int newState)
      {
            // If the thread isn't running, start it
            if ((mThreadState == THREAD_STOP) && (newState != THREAD_STOP))
            {
                  try
                  {
                        myThread = null;
                        myThread = new Thread(this);
                        myThread.start();
                  }
                  catch (IllegalThreadStateException ITSE)
                  {
                        System.out.println("Warning:  Exception in AtmelThread.setThreadState:");
                        ITSE.printStackTrace();
                  }
            }
            mThreadState = newState;
            // If the thread state changes, notify any observers
            this.setChanged();
            notifyObservers();
      }
          
      /**
      * Run method used by the Runnable interface.
      * DO NOT CALL THIS METHOD DIRECTLY!!!!!!!!
      * Instead, set the Atmel parent property (or supply one in the
      * constructor) and then set the Thread State to THREAD_RUN or THREAD_STEP
      */
      public void run()
      {
            String deskcheckString = new String();
            System.out.println("AtmelThread.run");
            while (this.getThreadState() != THREAD_STOP)
            {
                  try 
                  {
                        waitWhilePaused();
                        if (mAtmel != null)
                        {
                              if (mBreakPoints.size() != 0)
                              {
                                    BreakPoint aBreakPoint;
                                    for (int i=0;i<mBreakPoints.size();i++)
                                    {
                                          aBreakPoint = (BreakPoint)mBreakPoints.elementAt(i);
                                          if (aBreakPoint.rulesSatisfied())
                                          {
                                                // TO DO:  Display message or pass
                                                // event that a break point has been
                                                // reached.
                                                this.setThreadState(THREAD_PAUSE);
                                          }
                                    }
                              }
                              // We will be paused here if a break
                              // point has occured
                              if (this.getThreadState() != THREAD_PAUSE) 
                              {
                                    mAtmel.ExecuteNextInstruction();
                                    if (mVinceApplication.DeskCheckerActive())
                                    {
                                          int thePC = mAtmel.getLastInstructionAddress();
                                          deskcheckString = Utils.hex(thePC,4)+"\t\t";
                                          int MBR = mAtmel.getProgramMemory(thePC);
                                          int MBR2 = mAtmel.getProgramMemory(thePC+1);
                                          // NOTE:  Add 1 to PC here, as the InstructionDecoder
                                          // expects the Program Counter to have already been
                                          // advanced by the Atmel.Fetch() instruction
                                          deskcheckString += InstructionDecoder.DisassembleInstruction(MBR, MBR2, thePC+1)+"\n";
                                          mVinceApplication.appendToDeskChecker(deskcheckString);
                                    }
                              }
                        }
                        if (this.getThreadState() == THREAD_STEP)
                              this.setThreadState(THREAD_PAUSE);
                        //myThread.yield();
                        myThread.sleep(1);
                  }
                  catch (InterruptedException e)
                  {
                        this.setThreadState(THREAD_STOP);
                        System.out.println("Stopping the Atmel thread because:"+e.toString());
                        e.printStackTrace();
                  }
                  catch (RuntimeException RE)
                  {
                        this.setThreadState(THREAD_STOP);
                        mVinceApplication.appendToOutput("*** Runtime Exception occured at address " +Utils.hex(mAtmel.getLastInstructionAddress(),4)+" ***\n");
                        mVinceApplication.appendToOutput(RE.toString()+"\n");
                  }
            }     
      }

      private void waitWhilePaused() throws InterruptedException
      {
            while (this.getThreadState() == THREAD_PAUSE) { myThread.sleep(1); }
      }
}     
