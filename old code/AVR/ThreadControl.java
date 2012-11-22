package AVR;

//================================================
// ThreadControl class
//
// Version 1.0.1
//
// MODIFICATION HISTORY:
// 21/07/2003 - 1.0.1 - Added wait while paused method
// 16/07/2003 - 1.0.0 - Prototype
//================================================

import java.lang.*;

/**
 * The ThreadControl class is used to allow other threads
 * to control a class running in a thread.
 * @author Simon Mitchell
 * @version 1.0.1
 */
public class ThreadControl
{
    // Request flags.
    /**
     * This is the default (and useless) value for the request flag
     */
    public static final int UNDEFINED = 0;
    /**
     * This flag sets the associated thread to run
     */
    public static final int RUN = 1;
    /**
     * Causes the associated thread to pause
     */
    public static final int PAUSE = 2;
    /**
     * Causes the associated thread to exit its "run" method
     */
    public static final int STOP = 3;

    private int mRequestFlag;

    /**
     * Constructor - no arguments
     */
    public void ThreadControl()
    {
	mRequestFlag = UNDEFINED;
    }

    /**
     * Causes the current thread to sleep, if the current flag
     * has been set to PAUSE
     */
    public void waitWhilePaused() throws InterruptedException
    {
	while (this.getRequestFlag() == PAUSE) { Thread.sleep(0); }
    }

    /**
     * Sets the request flag for this thread controller
     */
    public synchronized void setRequestFlag(int theflag)
    { 
	mRequestFlag = theflag; 
	notifyAll();
    }

    /**
     * Returns the current request flag for this thread controller
     */
    public synchronized int getRequestFlag() { return mRequestFlag; }
}
