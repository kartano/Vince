package Utils.timer;
import java.util.*;

public class test
{
    public static void main(String[] args)
    {
        ClockCycle cycle = new ClockCycle();
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(cycle, (long)0, (long)1000);
        while (true) { }
    }
}
