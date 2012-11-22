package Utils.timer;
import java.util.*;

public class ClockCycle extends TimerTask
{
    private boolean mFlag;

    public void run()
    {
        mFlag = !mFlag;
        if (mFlag)
            System.out.println("Tick!");
        else
            System.out.println("Tock!");
    }
}
